package net.mcreator.concoction.block.entity;

import com.google.gson.Gson;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import net.mcreator.concoction.block.CookingCauldron;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipeInput;
import net.mcreator.concoction.world.inventory.BoilingCauldronInterfaceMenu;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.*;
import org.jetbrains.annotations.Nullable;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.*;

public class CookingCauldronEntity extends RandomizableContainerBlockEntity {
    // This can be any value of any type you want, so long as you can somehow serialize it to NBT.
    // We will use an int for the sake of example.
    // Container methods and fields
    private final int ContainerSize = 6;
    private boolean isCooking = false;
    private RecipeHolder<CauldronBrewingRecipe> recipe = null;
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


    public CookingCauldronEntity(BlockPos pos, BlockState state) {
        super(ConcoctionModBlockEntities.COOKING_CAULDRON.get(), pos, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.progress = tag.getInt("cooking.progress");
        this.maxProgress = tag.getInt("cooking.max_progress");
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
        tag.putString("cooking.craft_result", (new Gson()).toJson(this.craftResult));

        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos) {
        // Проверяем, не находится ли изменившийся блок снизу от котла
        if (fromPos.equals(pos.below())) {
            checkHeatSource(level, pos, state);
        }
    }

    private void checkHeatSource(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        Block blockBelow = level.getBlockState(pos.below()).getBlock();
        boolean hasHeatSource = level.getFluidState(pos.below()).is(Fluids.LAVA.getSource()) || 
                                blockBelow instanceof FireBlock ||
                                blockBelow instanceof MagmaBlock || 
                                blockBelow instanceof CampfireBlock;

        // Если нет источника тепла под котлом
        if (!hasHeatSource) {
            // Проверяем, нужно ли обновлять состояние
            if (state.getValue(CookingCauldron.LIT) || state.getValue(CookingCauldron.COOKING)) {
                // Используем defaultBlockState и сохраняем только уровень воды
                BlockState defaultState = state.getBlock().defaultBlockState()
                    .setValue(CookingCauldron.LEVEL, state.getValue(CookingCauldron.LEVEL));
                
                level.setBlock(pos, defaultState, 3);
                
                // Сбрасываем прогресс готовки
                resetProgress();
            }
        } else if (!state.getValue(CookingCauldron.LIT)) {
            // Если есть источник тепла, но котел не горит, то зажигаем его
            level.setBlock(pos, state.setValue(CookingCauldron.LIT, true), 3);
        }
    }

    // craft logic
    // The signature of this method matches the signature of the BlockEntityTicker functional interface.
    public void tick(Level level, BlockPos pPos, BlockState pState) {
        if (!level.isClientSide) {
            // Проверяем состояние источника тепла
            checkHeatSource(level, pPos, pState);
            
            if (pState.getValue(CookingCauldron.LIT)) {
                if (this.isCooking) {
                    // Проверяем, все ли ингредиенты и половник на месте
                    if (!validateCurrentRecipe()) {
                        level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                        resetProgress();
                        return;
                    }
                    
                    // Проверяем слот результата на превышение максимального размера стака
                    if (this.recipe != null) {
                        ItemStack resultSlot = this.items.get(5);
                        if (!resultSlot.isEmpty()) {
                            String recipeResultId = this.recipe.value().getOutput().get("id");
                            if (!recipeResultId.isEmpty()) {
                                ResourceLocation recipeItemId = ResourceLocation.parse(recipeResultId);
                                ResourceLocation currentItemId = BuiltInRegistries.ITEM.getKey(resultSlot.getItem());
                                
                                // Если предмет в слоте результата отличается от ожидаемого результата крафта
                                if (!currentItemId.equals(recipeItemId)) {
                                    level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                                    resetProgress();
                                    return;
                                }
                                
                                // Проверяем, есть ли место для результата
                                int currentCount = resultSlot.getCount();
                                int recipeCount = Integer.parseInt(this.recipe.value().getOutput().get("count"));
                                if (currentCount + recipeCount > resultSlot.getMaxStackSize()) {
                                    level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                                    resetProgressOnly();
                                    return;
                                }
                            }
                        }
                    }
                    
                    increaseCraftingProgress();
                    setChanged(level, pPos, pState);

                    if (hasCraftingFinished()) {
                        craftItem();
                        level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                    }
                } else if (!hasCraftedResult() && hasRecipe()) {
                    // Начинаем готовку только если нет результата
                    level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, true));
                    this.isCooking = true;
                    setChanged(level, pPos, pState);
                }
            } else {
                level.setBlockAndUpdate(pPos, pState.setValue(CookingCauldron.COOKING, false));
                resetProgress();
            }
        }
    }

    private boolean validateCurrentRecipe() {
        if (this.recipe == null) return false;

        // Проверяем наличие половника для не-hand рецептов
        String interactionType = this.recipe.value().getOutput().get("interactionType");
        if (!interactionType.equals("hand")) {
            ItemStack ladleStack = this.items.get(4);
            if (ladleStack.isEmpty()) {
                return false;
            }
        }

        // Создаем карту требуемых ингредиентов
        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        for (Ingredient ingredient : this.recipe.value().getInputItems()) {
            requiredIngredients.merge(ingredient, 1, Integer::sum);
        }

        // Проверяем наличие всех необходимых ингредиентов
        for (ItemStack itemStack : this.items.subList(0, 4)) {
            if (itemStack.isEmpty()) continue;

            for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
                if (entry.getValue() > 0 && entry.getKey().test(itemStack)) {
                    entry.setValue(entry.getValue() - 1);
                    break;
                }
            }
        }

        // Проверяем, что все ингредиенты найдены
        return requiredIngredients.values().stream().allMatch(count -> count <= 0);
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
        // Создаем карту для отслеживания, сколько каких ингредиентов нужно потратить
        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        for (Ingredient ingredient : this.recipe.value().getInputItems()) {
            requiredIngredients.merge(ingredient, 1, Integer::sum);
        }
        
        // Создаем список для предметов, которые нужно вернуть
        NonNullList<ItemStack> newItems = NonNullList.withSize(this.ContainerSize, ItemStack.EMPTY);
        List<ItemStack> itemsToSpawn = new ArrayList<>();
        
        // Обрабатываем каждый слот
        for (int i = 0; i < 4; i++) {
            ItemStack itemStack = this.items.get(i);
            if (itemStack.isEmpty()) continue;
            
            // Проверяем, нужен ли этот предмет для рецепта
            boolean used = false;
            for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
                if (entry.getValue() > 0 && entry.getKey().test(itemStack)) {
                    // Уменьшаем требуемое количество этого ингредиента
                    entry.setValue(entry.getValue() - 1);
                    used = true;
                    
                    // Обрабатываем возврат контейнеров
                    if (itemStack.getCount() > 1) {
                        if (itemStack.is(ItemTags.create(ResourceLocation.parse("c:buckets")))) {
                            itemsToSpawn.add(new ItemStack(Items.BUCKET));
                        } else if (itemStack.is(ItemTags.create(ResourceLocation.parse("c:bottles")))) {
                            itemsToSpawn.add(new ItemStack(Items.GLASS_BOTTLE));
                        }
                    }
                    break;
                }
            }
            
            // Если предмет не был использован в рецепте или осталось больше одного предмета
            if (!used || itemStack.getCount() > 1) {
                ItemStack remainingStack = itemStack.copy();
                if (used) {
                    remainingStack.shrink(1);
                }
                newItems.set(i, remainingStack);
            }
        }
        
        // Обрабатываем слот половника
        String interactionType = this.recipe.value().getOutput().get("interactionType");
        
        
        if (!interactionType.equals("hand")) {
            ItemStack ladleStack = this.items.get(4);
            if (!ladleStack.isEmpty()) {
                // Нужно проверить, что в слоте половника правильный предмет
                boolean isCorrectLadleItem = switch (interactionType) {
                    case "bottle" -> ladleStack.getItem() == Items.GLASS_BOTTLE;
                    case "stick" -> ladleStack.getItem() == Items.STICK;
                    case "bucket" -> ladleStack.getItem() == Items.BUCKET;
                    case "bowl" -> ladleStack.getItem() == Items.BOWL;
                    default -> false;
                };
                
                if (isCorrectLadleItem) {
                    if (ladleStack.getCount() > 1) {
                        ItemStack remainingLadle = ladleStack.copy();
                        remainingLadle.shrink(1);
                        newItems.set(4, remainingLadle);
                    }
                    // Если остался один половник, слот останется пустым после использования
                }
            }
        } else {
            // Для типа "hand" просто копируем предмет из слота половника в новый инвентарь
            newItems.set(4, this.items.get(4).copy());
        }
        
        // Устанавливаем результат крафта
        this.craftResult = this.recipe.value().getOutput();
        
        // Обрабатываем результат крафта
        if (!this.craftResult.get("id").isEmpty()) {
            ResourceLocation itemId = ResourceLocation.parse(this.craftResult.get("id"));
            int craftedCount = Integer.parseInt(this.craftResult.get("count"));
            
            // Получаем текущий стак в слоте результата
            ItemStack currentResult = this.items.get(5);
            ItemStack newResult;
            
            if (!currentResult.isEmpty() && currentResult.is(BuiltInRegistries.ITEM.get(itemId))) {
                // Если в слоте уже есть предметы того же типа
                newResult = currentResult.copy();
                int spaceLeft = newResult.getMaxStackSize() - newResult.getCount();
                int toAdd = Math.min(craftedCount, spaceLeft);
                if (toAdd > 0) {
                    newResult.grow(toAdd);
                }
            } else {
                // Если слот пустой или содержит другой предмет
                newResult = new ItemStack(BuiltInRegistries.ITEM.get(itemId), craftedCount);
            }
            
            newItems.set(5, newResult);
        }
        
        // Обновляем инвентарь
        this.setItems(newItems);
        
        // Выбрасываем лишние предметы в мир
        if (!itemsToSpawn.isEmpty() && this.level != null) {
            for (ItemStack stack : itemsToSpawn) {
                Block.popResource(this.level, this.worldPosition, stack);
            }
        }
        
        // Проверяем, можно ли продолжить крафт
        if (hasRecipe()) {
            resetProgressOnly();
        } else {
            resetProgress();
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

    private boolean isSameRecipe(RecipeHolder<CauldronBrewingRecipe> recipe1, RecipeHolder<CauldronBrewingRecipe> recipe2) {
        if (recipe1 == null || recipe2 == null) return false;
        
        // Сравниваем по ID рецептов (основной способ)
        if (recipe1.id().equals(recipe2.id())) return true;
        
        // Если ID разные, сравниваем по ингредиентам
        List<Ingredient> ingredients1 = recipe1.value().getInputItems();
        List<Ingredient> ingredients2 = recipe2.value().getInputItems();
        
        if (ingredients1.size() != ingredients2.size()) return false;
        
        // Создаем копии списков для сравнения
        List<Ingredient> sorted1 = new ArrayList<>(ingredients1);
        List<Ingredient> sorted2 = new ArrayList<>(ingredients2);
        
        // Сортируем по строковому представлению для сравнения
        sorted1.sort((a, b) -> a.toString().compareTo(b.toString()));
        sorted2.sort((a, b) -> a.toString().compareTo(b.toString()));
        
        for (int i = 0; i < sorted1.size(); i++) {
            if (!sorted1.get(i).toString().equals(sorted2.get(i).toString())) {
                return false;
            }
        }
        
        return true;
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<CauldronBrewingRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()) {
            return false;
        }

        RecipeHolder<CauldronBrewingRecipe> newRecipe = recipe.get();
        // Если рецепт изменился, сбрасываем прогресс
        if (this.recipe != null && !isSameRecipe(this.recipe, newRecipe)) {
            this.resetProgress();
            return false;
        }

        // Проверяем содержимое слота результата
        ItemStack resultSlot = this.items.get(5);
        if (!resultSlot.isEmpty()) {
            // Получаем ID предмета из результата крафта
            String recipeResultId = newRecipe.value().getOutput().get("id");
            if (!recipeResultId.isEmpty()) {
                ResourceLocation recipeItemId = ResourceLocation.parse(recipeResultId);
                ResourceLocation currentItemId = BuiltInRegistries.ITEM.getKey(resultSlot.getItem());
                
                // Если предмет в слоте результата отличается от ожидаемого результата крафта
                if (!currentItemId.equals(recipeItemId)) {
                    return false;
                }
                
                // Проверяем, есть ли место для результата
                int currentCount = resultSlot.getCount();
                int recipeCount = Integer.parseInt(newRecipe.value().getOutput().get("count"));
                if (currentCount + recipeCount > resultSlot.getMaxStackSize()) {
                    return false;
                }
            }
        }

        // Проверяем наличие нужного предмета в слоте половника
        String interactionType = newRecipe.value().getOutput().get("interactionType");
        ItemStack ladleItem = this.items.get(4); // слот половника

     

        // Для типа hand половник не нужен
        if (interactionType.equals("hand")) {
            this.recipe = newRecipe;
            this.maxProgress = newRecipe.value().getCookingTime();
            return true;
        }

        // Для остальных типов проверяем наличие нужного предмета
        boolean hasCorrectLadle = switch (interactionType) {
            case "bottle" -> !ladleItem.isEmpty() && ladleItem.getItem() == Items.GLASS_BOTTLE;
            case "stick" -> !ladleItem.isEmpty() && ladleItem.getItem() == Items.STICK;
            case "bucket" -> !ladleItem.isEmpty() && ladleItem.getItem() == Items.BUCKET;
            case "bowl" -> !ladleItem.isEmpty() && ladleItem.getItem() == Items.BOWL;
            default -> false;
        };

        if (hasCorrectLadle) {
            this.recipe = newRecipe;
            this.maxProgress = newRecipe.value().getCookingTime();
            return true;
        }

        return false;
    }

    private Optional<RecipeHolder<CauldronBrewingRecipe>> getCurrentRecipe() {
        if (isCooking) return Optional.empty();
        return this.level.getRecipeManager()
                .getRecipeFor(ConcoctionModRecipes.CAULDRON_BREWING_RECIPE_TYPE.get(),
                        new CauldronBrewingRecipeInput(this.getBlockState(), this.getItems()), level);
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
        
        // Проверяем, является ли это слотом ингредиентов (0-3)
        boolean isIngredientSlot = slot >= 0 && slot < 4;
        boolean isLadleSlot = slot == 4;
        
        // Если меняется содержимое слота ингредиентов или слота половника,
        // то это может повлиять на рецепт и процесс готовки
        if ((isIngredientSlot || isLadleSlot) && (isDifferentItem || isSlotEmpty != isStackEmpty)) {
            System.out.println("Cauldron: Item changed in slot " + slot + ", isCooking: " + this.isCooking);
            
            // Сначала устанавливаем новый предмет
            ItemStack oldStack = this.items.get(slot);
            this.items.set(slot, stack);
            
            // Теперь проверяем рецепт с новым предметом
            Optional<RecipeHolder<CauldronBrewingRecipe>> newRecipe = getCurrentRecipe();
            boolean sameRecipe = false;
            boolean recipeValid = false;
            
            if (this.isCooking && this.recipe != null && newRecipe.isPresent()) {
                sameRecipe = this.recipe.equals(newRecipe.get());
                recipeValid = true;
                System.out.println("Cauldron: Current recipe: " + this.recipe.id() + ", New recipe: " + newRecipe.get().id() + ", Same: " + sameRecipe);
            } else if (this.isCooking && newRecipe.isPresent()) {
                // Если раньше не было рецепта, но теперь есть - это новый рецепт
                recipeValid = true;
                System.out.println("Cauldron: New recipe found: " + newRecipe.get().id());
            } else if (this.isCooking) {
                System.out.println("Cauldron: No valid recipe found after change");
            }
            
            // Сбрасываем прогресс только если рецепт изменился или стал некорректным
            if (this.isCooking && (!sameRecipe || !recipeValid)) {
                System.out.println("Cauldron: Resetting progress - sameRecipe: " + sameRecipe + ", recipeValid: " + recipeValid);
                resetProgressOnly();
                
                // Устанавливаем состояние блока как не готовящий
                if (this.level != null) {
                    BlockState state = this.level.getBlockState(this.worldPosition);
                    if (state.hasProperty(CookingCauldron.COOKING)) {
                        this.level.setBlock(this.worldPosition, 
                                          state.setValue(CookingCauldron.COOKING, false), 
                                          3);
                    }
                }
            } else if (this.isCooking) {
                System.out.println("Cauldron: Keeping progress - sameRecipe: " + sameRecipe + ", recipeValid: " + recipeValid);
            }
            
            // НЕ возвращаем старый предмет - оставляем новый
            // Стандартная обработка уже произойдет ниже
            return; // Выходим, чтобы избежать двойной установки
        }
        
        // Стандартная обработка (только если не обработали выше)
        stack.limitSize(this.getMaxStackSize(stack));
        this.items.set(slot, stack);
        this.setChanged();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> Items) {
        this.items = Items;
        this.setChanged();
    }

    //Item add to container methods
    @SuppressWarnings("UnusedReturnValue")
    public boolean addItemOnClick(ItemStack addedItem, int count, boolean isCreative) {
//        if (this.isCooking) return false;
//        if (this.hasCraftedResult()) return false;

        boolean flag = false;
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack itemstack = this.getItem(i);
            if (itemstack.isEmpty()) {
                this.setItem(i, isCreative ? addedItem.copyWithCount(1) : addedItem.split(count));
                flag = true;
                break;
//            } else if ( itemstack.getItem().equals(addedItem.getItem()) ) {
//                int to_add = Math.min(count, itemstack.getMaxStackSize()-itemstack.getCount());
//                if (isCreative || addedItem.getCount() - addedItem.split(to_add).getCount() != 0) {
//                    this.items.get(i).grow(to_add);
//                    this.setChanged();
//                    flag = true;
//                    break;
//                }
            }
        }
        if (flag) this.resetProgress();
        return flag;
    }

    public ItemStack takeItemOnClick(boolean takeAll) {
//        if (this.isCooking) return ItemStack.EMPTY;
        ItemStack returnStack = ItemStack.EMPTY;
        for (int i = this.items.size()-1; i >= 0; i--) {
            ItemStack itemstack = this.items.get(i);
            if (!itemstack.isEmpty()) {
                if (takeAll) {
                    returnStack = itemstack.copy();
                    this.setItem(i, ItemStack.EMPTY);
                } else {
                    returnStack = itemstack.split(1);
                }
                this.setChanged();
                this.resetProgress();
                return returnStack;
            }
        }
        return returnStack;
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
        return Component.translatable("container.cooking_cauldron");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new BoilingCauldronInterfaceMenu(pContainerId, pPlayerInventory, 
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
        if(state.hasProperty(CookingCauldron.LIT)) {
            return state.getValue(CookingCauldron.LIT);
        }
        return false;
    }
}
