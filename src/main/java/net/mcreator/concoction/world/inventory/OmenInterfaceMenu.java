package net.mcreator.concoction.world.inventory;

import net.mcreator.concoction.block.entity.CookingCauldronEntity;
import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class OmenInterfaceMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;
    public int x, y, z;
    private ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private IItemHandler internal;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private boolean bound = false;
    private Supplier<Boolean> boundItemMatcher = null;
    private Entity boundEntity = null;
    private BlockEntity boundBlockEntity = null;

    private int progress = 0;
    private int maxProgress = 200;
    private boolean isCooking = false;
    private boolean isLit = false;

    public OmenInterfaceMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ConcoctionModMenus.OMEN_INTERFACE.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();
        this.internal = new ItemStackHandler(6); // 4 слота для ингредиентов + 1 для половника + 1 для результата
        BlockPos pos = null;
        if (extraData != null) {
            pos = extraData.readBlockPos();
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            access = ContainerLevelAccess.create(world, pos);
        }

        // Получаем доступ к блок-сущности котла
        if (pos != null && world.getBlockEntity(pos) instanceof CookingCauldronEntity cauldron) {
            this.boundBlockEntity = cauldron;
            // Создаем обертку InvWrapper для инвентаря котла
            this.internal = new InvWrapper(cauldron);
        }

        // Добавляем слоты для ингредиентов и инвентаря игрока
        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Добавляем 4 слота для ингредиентов (2x2 сетка)
        this.customSlots.put(36, this.addSlot(new SlotItemHandler(internal, 0, 26, 27)));
        this.customSlots.put(37, this.addSlot(new SlotItemHandler(internal, 1, 44, 27)));
        this.customSlots.put(38, this.addSlot(new SlotItemHandler(internal, 2, 26, 45)));
        this.customSlots.put(39, this.addSlot(new SlotItemHandler(internal, 3, 44, 45)));

        // Добавляем слот для половника/контейнера (5-й слот)
        this.customSlots.put(40, this.addSlot(new SlotItemHandler(internal, 4, 78, 15) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isValidLadleItem(stack);
            }
        }));

        // Добавляем слот для результата (6-й слот)
        this.customSlots.put(41, this.addSlot(new SlotItemHandler(internal, 5, 118, 38)  {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // нельзя положить предметы в результирующий слот
            }

            @Override
            public boolean mayPickup(Player player) {
                if (boundBlockEntity instanceof CookingCauldronEntity cauldron) {
                    return true;
                }
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);

                // Проверяем, есть ли правильный контейнер в слоте половника
                if (boundBlockEntity instanceof CookingCauldronEntity cauldron) {
                    Map<String, String> craftResult = cauldron.getCraftResult();
                    String interactionType = craftResult.get("interactionType");

                    // Расходование половника уже происходит в методе craftItem() класса CookingCauldronEntity
                    // Удаляем дублирующийся код, чтобы избежать двойного расхода

                    // Сбрасываем результат крафта в котле
                    cauldron.setCraftResult(Map.of("id", "", "count", "", "interactionType", ""));
                }

                if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer)
                    Utils.addAchievement(serverPlayer, "concoction:cauldron_boiling");
            }
        }));
    }

    // Добавляем слоты инвентаря игрока
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    // Добавляем хотбар игрока
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    // Проверка, является ли предмет валидным для слота половника
    private boolean isValidLadleItem(ItemStack stack) {
        return stack.is(Items.STICK) ||
                stack.is(Items.BOWL) ||
                stack.is(Items.GLASS_BOTTLE) ||
                stack.is(Items.BUCKET);
    }

    // Метод для получения текущего прогресса готовки
    public int getProgress() {
        if(boundBlockEntity instanceof CookingCauldronEntity entity) {
            return entity.getProgress();
        }
        return progress;
    }

    // Метод для получения максимального прогресса готовки
    public int getMaxProgress() {
        if(boundBlockEntity instanceof CookingCauldronEntity entity) {
            return entity.getMaxProgress();
        }
        return maxProgress;
    }

    // Метод для проверки, готовится ли что-то
    public boolean isCooking() {
        if(boundBlockEntity instanceof CookingCauldronEntity entity) {
            return entity.isCooking();
        }
        return isCooking;
    }

    // Метод для проверки, зажжен ли котел
    public boolean isLit() {
        if(boundBlockEntity instanceof CookingCauldronEntity entity) {
            return entity.isLit();
        }
        return isLit;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.bound) {
            if (this.boundItemMatcher != null)
                return this.boundItemMatcher.get();
            else if (this.boundBlockEntity != null)
                return AbstractContainerMenu.stillValid(this.access, player, this.boundBlockEntity.getBlockState().getBlock());
            else if (this.boundEntity != null)
                return this.boundEntity.isAlive();
        }
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            // Забор результата
            if (index == 41) {
                if (!this.moveItemStackTo(stackInSlot, 0, 35, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(playerIn, stackInSlot);
            }
            // Из инвентаря игрока (0-35) в котёл
            else if (index >= 0 && index <= 35) {
                boolean moved = false;
                // Проверяем, является ли предмет палкой, миской, бутылкой или ведром
                if (isValidLadleItem(stackInSlot)) {
                    // Пытаемся переместить в слот половника
                    Slot ladleSlot = this.slots.get(40);
                    if (ladleSlot.mayPlace(stackInSlot)) {
                        ItemStack ladleStack = ladleSlot.getItem();
                        if (ladleStack.isEmpty()) {
                            // Если слот пустой, перемещаем весь стак
                            if (this.moveItemStackTo(stackInSlot, 40, 41, false)) {
                                moved = true;
                            }
                        } else if (ladleStack.getItem() == stackInSlot.getItem() &&
                                ladleStack.getCount() < ladleStack.getMaxStackSize()) {
                            // Если в слоте уже есть такой предмет и есть место
                            int space = ladleStack.getMaxStackSize() - ladleStack.getCount();
                            int toMove = Math.min(space, stackInSlot.getCount());
                            ItemStack splitStack = stackInSlot.split(toMove);
                            if (this.moveItemStackTo(splitStack, 40, 41, false)) {
                                moved = true;
                            }
                        }
                    }
                }
                // Если не удалось переместить в слот половника или это обычный ингредиент
                if (!moved) {
                    // Пытаемся переместить в слоты ингредиентов
                    for (int i = 36; i <= 39; i++) {
                        Slot ingredientSlot = this.slots.get(i);
                        if (ingredientSlot.mayPlace(stackInSlot)) {
                            ItemStack slotStack = ingredientSlot.getItem();
                            if (slotStack.isEmpty()) {
                                // Если слот пустой, перемещаем весь стак
                                if (this.moveItemStackTo(stackInSlot, i, i + 1, false)) {
                                    moved = true;
                                    break;
                                }
                            } else if (slotStack.getItem() == stackInSlot.getItem() &&
                                    slotStack.getCount() < slotStack.getMaxStackSize()) {
                                // Если в слоте уже есть такой предмет и есть место
                                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                                int toMove = Math.min(space, stackInSlot.getCount());
                                ItemStack splitStack = stackInSlot.split(toMove);
                                if (this.moveItemStackTo(splitStack, i, i + 1, false)) {
                                    moved = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!moved) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            // Из слотов котла (36-40) в инвентарь игрока
            else if (index >= 36 && index <= 40) {
                if (!this.moveItemStackTo(stackInSlot, 0, 35, true)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stackInSlot);
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    public Map<Integer, Slot> get() {
        return customSlots;
    }

}
