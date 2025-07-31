package net.mcreator.concoction.block.entity;

import com.google.gson.Gson;
import net.mcreator.concoction.block.OvenBlock;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipeInput;
import net.mcreator.concoction.world.inventory.OvenGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.*;

public class OvenBlockEntity extends RandomizableContainerBlockEntity {
    // Слоты: 0 бутылочка, 1-6 крафт, 7 миска, 8 результат
    private final int ContainerSize = 9;
    private boolean isCooking = false;
    private RecipeHolder<OvenRecipe> recipe = null;
    private Map<String, String> craftResult = Map.ofEntries(
            Map.entry("id",""),
            Map.entry("count",""),
            Map.entry("interactionType","")
    );

    private int progress = 0;
    private int maxProgress = 200;
    private final int DEFAULT_MAX_PROGRESS = 200;

    private NonNullList<ItemStack> items = NonNullList.withSize(
            this.ContainerSize,
            ItemStack.EMPTY
    );


    public OvenBlockEntity(BlockPos pos, BlockState state) {
        super(ConcoctionModBlockEntities.OVEN_BLOCK.get(), pos, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("cooking.progress");
        this.maxProgress = tag.getInt("cooking.max_progress");
        this.isCooking = tag.getBoolean("cooking.is_cooking");
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.craftResult = (new Gson()).fromJson(tag.getString("cooking.craft_result"), HashMap.class);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    // Save values into the passed CompoundTag here.
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("cooking.progress", this.progress);
        tag.putInt("cooking.max_progress", this.maxProgress);
        tag.putBoolean("cooking.is_cooking", this.isCooking);
        tag.putString("cooking.craft_result", (new Gson()).toJson(this.craftResult));

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    public void tick(Level level, BlockPos pPos, BlockState pState) {
        if (level.isClientSide) return;

        boolean wasLit = pState.getValue(OvenBlock.LIT);
        boolean shouldBeLit = isHeated(level, pPos);
        
        if (wasLit != shouldBeLit) {
            level.setBlock(pPos, pState.setValue(OvenBlock.LIT, shouldBeLit), 3);
        }

        if (!shouldBeLit) {
            if (isCooking) {
                resetProgressOnly();
            }
            return;
        }

        Optional<RecipeHolder<OvenRecipe>> currentRecipe = getCurrentRecipe();
        
        if (currentRecipe.isPresent()) {
            if (!isCooking) {
                // Начинаем готовку
                this.recipe = currentRecipe.get();
                this.isCooking = true;
                this.maxProgress = recipe.value().getCookingTime();
                System.out.println("Oven: Starting cooking with recipe: " + recipe.id() + ", cooking time: " + maxProgress);
                setChanged();
            } else if (!currentRecipe.get().equals(this.recipe)) {
                // Рецепт изменился - сбрасываем прогресс
                System.out.println("Oven: Recipe changed, resetting progress");
                resetProgress();
                return;
            }
            
            // Проверяем, можем ли добавить результат
            if (canAddResult()) {
                increaseCraftingProgress();
                
                if (progress % 20 == 0) { // Логируем каждую секунду
                    System.out.println("Oven: Cooking progress: " + progress + "/" + maxProgress);
                }
                
                if (hasCraftingFinished()) {
                    System.out.println("Oven: Cooking finished! Crafting item.");
                    craftItem();
                    resetProgress();
                }
            } else {
                System.out.println("Oven: Cannot add result, pausing cooking");
            }
        } else if (isCooking) {
            // Рецепт больше не совпадает - сбрасываем
            System.out.println("Oven: No matching recipe found, stopping cooking");
            resetProgress();
        }
    }

    private boolean isHeated(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        Block belowBlock = belowState.getBlock();
        
        // Проверяем источники тепла
        if (belowBlock instanceof CampfireBlock) {
            return belowState.getValue(CampfireBlock.LIT);
        }
        if (belowBlock instanceof FireBlock || belowBlock instanceof MagmaBlock) {
            return true;
        }
        if (belowBlock == Blocks.LAVA) {
            return true;
        }
        
        return false;
    }

    private boolean canAddResult() {
        if (recipe == null) return false;
        
        ItemStack resultSlot = items.get(8);
        Map<String, String> recipeResult = recipe.value().getResult();
        
        if (resultSlot.isEmpty()) {
            return true;
        }
        
        // Проверяем, совпадает ли предмет в слоте результата с результатом рецепта
        ResourceLocation resultId = ResourceLocation.parse(recipeResult.get("id"));
        if (!BuiltInRegistries.ITEM.get(resultId).equals(resultSlot.getItem())) {
            return false;
        }
        
        // Проверяем, поместится ли результат
        int resultCount = Integer.parseInt(recipeResult.get("count"));
        return resultSlot.getCount() + resultCount <= resultSlot.getMaxStackSize();
    }


    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = recipe == null ? DEFAULT_MAX_PROGRESS : recipe.value().getCookingTime();
        this.isCooking = false;
        this.recipe = null;
        this.craftResult = Map.of("id", "", "count", "", "interactionType", "");
        setChanged();
    }

    private void resetProgressOnly() {
        this.progress = 0;
        this.isCooking = false;
        setChanged();
    }

    private void craftItem() {
        if (recipe == null) return;
        
        Map<String, String> recipeResult = recipe.value().getResult();
        ResourceLocation resultId = ResourceLocation.parse(recipeResult.get("id"));
        int resultCount = Integer.parseInt(recipeResult.get("count"));
        
        // Создаем результат
        ItemStack result = new ItemStack(BuiltInRegistries.ITEM.get(resultId), resultCount);
        
        // Добавляем в слот результата
        ItemStack resultSlot = items.get(8);
        if (resultSlot.isEmpty()) {
            items.set(8, result);
        } else {
            resultSlot.grow(resultCount);
        }
        
        // Тратим ингредиенты
        consumeIngredients();
        
        setChanged();
    }

    private void consumeIngredients() {
        if (recipe == null || level == null) return;
        
        List<ItemStack> bottlesAndBuckets = new ArrayList<>();
        
        // Тратим из слота бутылочки (0)
        ItemStack bottleSlot = items.get(0);
        if (!bottleSlot.isEmpty()) {
            if (bottleSlot.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "bottles")))) {
                bottlesAndBuckets.add(new ItemStack(Items.GLASS_BOTTLE));
            } else if (bottleSlot.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "buckets")))) {
                bottlesAndBuckets.add(new ItemStack(Items.BUCKET));
            }
            
            bottleSlot.shrink(1);
            if (bottleSlot.isEmpty()) {
                items.set(0, ItemStack.EMPTY);
            }
        }
        
        // Тратим из слотов крафта (1-6)
        for (int i = 1; i <= 6; i++) {
            ItemStack stack = items.get(i);
            if (!stack.isEmpty()) {
                // Проверяем теги для выброса пустых контейнеров
                if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "bottles")))) {
                    bottlesAndBuckets.add(new ItemStack(Items.GLASS_BOTTLE));
                } else if (stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "buckets")))) {
                    bottlesAndBuckets.add(new ItemStack(Items.BUCKET));
                }
                
                stack.shrink(1);
                if (stack.isEmpty()) {
                    items.set(i, ItemStack.EMPTY);
                }
            }
        }
        
        // Тратим из слота миски (7)
        ItemStack bowlSlot = items.get(7);
        if (!bowlSlot.isEmpty()) {
            if (bowlSlot.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "bottles")))) {
                bottlesAndBuckets.add(new ItemStack(Items.GLASS_BOTTLE));
            } else if (bowlSlot.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "buckets")))) {
                bottlesAndBuckets.add(new ItemStack(Items.BUCKET));
            }
            
            bowlSlot.shrink(1);
            if (bowlSlot.isEmpty()) {
                items.set(7, ItemStack.EMPTY);
            }
        }
        
        // Выбрасываем пустые контейнеры в мир
        for (ItemStack container : bottlesAndBuckets) {
            if (level != null && !level.isClientSide) {
                ItemEntity itemEntity = new ItemEntity(level, worldPosition.getX() + 0.5,
                    worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, container);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
        setChanged();
    }

    public boolean hasCraftedResult() {
        return !this.craftResult.get("id").isEmpty();
    }

    public Map<String, String> getCraftResult() {
        return this.craftResult;
    }

    public void setCraftResult(Map<String, String> result) {
        this.craftResult = result;
        this.setChanged();
    }

    private boolean hasRecipe() {
        return getCurrentRecipe().isPresent();
    }

    private Optional<RecipeHolder<OvenRecipe>> getCurrentRecipe() {
        if (level == null) return Optional.empty();
        return this.level.getRecipeManager()
                .getRecipeFor(ConcoctionModRecipes.OVEN_RECIPE_TYPE.get(),
                        new OvenRecipeInput(this.getItems()), level);
    }

    @Override
    public int getContainerSize() {
        return this.ContainerSize;
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack previousStack = this.items.get(slot);
        boolean isSlotEmpty = previousStack.isEmpty();
        boolean isStackEmpty = stack.isEmpty();
        boolean isDifferentItem = !isSlotEmpty && !isStackEmpty &&
                (!ItemStack.matches(previousStack, stack));

        // Проверяем, является ли это слотом ингредиентов (0-7)
        boolean isIngredientSlot = slot >= 0 && slot < 8;

        // Если меняется содержимое слота ингредиентов, это может повлиять на рецепт
        if (isIngredientSlot && (isDifferentItem || isSlotEmpty != isStackEmpty)) {
            // Сбрасываем прогресс готовки при изменении ингредиентов
            if (this.isCooking) {
                resetProgressOnly();
            }
        }

        // Стандартная обработка
        stack.limitSize(this.getMaxStackSize(stack));
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> Items) {
        this.items = Items;
        this.setChanged();
    }

    // Whether the container is considered "still valid" for the given player. For example, chests and
    // similar blocks check if the player is still within a given distance of the block here.
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    // Clear the internal storage, setting all slots to empty again.
    @Override
    public void clearContent() {
        items.clear();
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        // This will send the block entity data to the client every time the block entity is marked as changed.
        // This is useful for syncing data between the server and client.
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        tag.putInt("Progress", this.progress);
        tag.putInt("MaxProgress", this.maxProgress);
        tag.putBoolean("IsCooking", this.isCooking);
        return tag;
    }

    // Return our packet here. This method returning a non-null result tells the game to use this packet for syncing.
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // The packet uses the CompoundTag returned by #getUpdateTag. An alternative overload of #create exists
        // that allows you to specify a custom update tag, including the ability to omit data the client might not need.
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // Optionally: Run some custom logic when the packet is received.
    // The super/default implementation forwards to #loadAdditional.
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        CompoundTag tag = packet.getTag();
        handleUpdateTag(tag, registries);
        this.progress = tag.getInt("Progress");
        this.maxProgress = tag.getInt("MaxProgress");
        this.isCooking = tag.getBoolean("IsCooking");
    }

    // Handle a received update tag here. The default implementation calls #loadAdditional here,
    // so you do not need to override this method if you don't plan to do anything beyond that.
    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries);
        this.progress = tag.getInt("Progress");
        this.maxProgress = tag.getInt("MaxProgress");
        this.isCooking = tag.getBoolean("IsCooking");
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new OvenGUIMenu(pContainerId, pPlayerInventory,
                new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory) {
        return createMenu(pContainerId, pPlayerInventory, null);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public boolean isCooking() {
        return this.isCooking;
    }

    public boolean isLit() {
        BlockState state = this.getBlockState();
        if(state.hasProperty(OvenBlock.LIT)) {
            return state.getValue(OvenBlock.LIT);
        }
        return false;
    }
}
