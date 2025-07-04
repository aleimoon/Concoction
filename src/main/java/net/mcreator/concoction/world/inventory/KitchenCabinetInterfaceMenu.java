package net.mcreator.concoction.world.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.block.entity.OakKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.SpruceKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.BirchKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.AcaciaKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.OakKitchenCabinetBlock;
import net.mcreator.concoction.block.SpruceKitchenCabinetBlock;
import net.mcreator.concoction.block.BirchKitchenCabinetBlock;
import net.mcreator.concoction.block.AcaciaKitchenCabinetBlock;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class KitchenCabinetInterfaceMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
    public final static HashMap<String, Object> guistate = new HashMap<>();
    public final Level world;
    public final Player entity;
    public int x, y, z;
    private ContainerLevelAccess access = ContainerLevelAccess.NULL;
    private final net.minecraft.world.level.block.entity.BlockEntity blockEntity;
    private final Map<Integer, Slot> customSlots = new HashMap<>();
    private boolean bound = false;
    private BlockPos cabinetPos = null;

    public KitchenCabinetInterfaceMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ConcoctionModMenus.KITCHEN_CABINET_INTERFACE.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();

        if (extraData != null) {
            cabinetPos = extraData.readBlockPos();
            this.x = cabinetPos.getX();
            this.y = cabinetPos.getY();
            this.z = cabinetPos.getZ();
            access = ContainerLevelAccess.create(world, cabinetPos);

            BlockState state = world.getBlockState(cabinetPos);

            if (world.getBlockEntity(cabinetPos) instanceof OakKitchenCabinetBlockEntity be) {
                this.blockEntity = be;
                this.bound = true;
                if (state.hasProperty(OakKitchenCabinetBlock.OPEN)) {
                    world.setBlock(cabinetPos, state.setValue(OakKitchenCabinetBlock.OPEN, true), 3);
                    world.playSound(null, cabinetPos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            } else if (world.getBlockEntity(cabinetPos) instanceof SpruceKitchenCabinetBlockEntity be) {
                this.blockEntity = be;
                this.bound = true;
                if (state.hasProperty(SpruceKitchenCabinetBlock.OPEN)) {
                    world.setBlock(cabinetPos, state.setValue(SpruceKitchenCabinetBlock.OPEN, true), 3);
                    world.playSound(null, cabinetPos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            } else if (world.getBlockEntity(cabinetPos) instanceof BirchKitchenCabinetBlockEntity be) {
                this.blockEntity = be;
                this.bound = true;
                if (state.hasProperty(BirchKitchenCabinetBlock.OPEN)) {
                    world.setBlock(cabinetPos, state.setValue(BirchKitchenCabinetBlock.OPEN, true), 3);
                    world.playSound(null, cabinetPos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            } else if (world.getBlockEntity(cabinetPos) instanceof AcaciaKitchenCabinetBlockEntity be) {
                this.blockEntity = be;
                this.bound = true;
                if (state.hasProperty(AcaciaKitchenCabinetBlock.OPEN)) {
                    world.setBlock(cabinetPos, state.setValue(AcaciaKitchenCabinetBlock.OPEN, true), 3);
                    world.playSound(null, cabinetPos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            } else {
                this.blockEntity = null;
            }
        } else {
            this.blockEntity = null;
        }

        // Cabinet slots
        if (this.blockEntity instanceof OakKitchenCabinetBlockEntity || this.blockEntity instanceof SpruceKitchenCabinetBlockEntity || this.blockEntity instanceof BirchKitchenCabinetBlockEntity || this.blockEntity instanceof AcaciaKitchenCabinetBlockEntity) {
            int slotSize = 18;
            int startX = 8;
            int startY = 17;
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    int slotIndex = col + row * 9;
                    Slot slot = this.addSlot(new Slot((net.minecraft.world.Container) blockEntity, slotIndex, startX + col * slotSize, startY + row * slotSize));
                    customSlots.put(slotIndex, slot);
                }
            }
        } else {
            for (int i = 0; i < 27; i++) {
                this.addSlot(new Slot(new net.minecraft.world.SimpleContainer(1), i, 0, 0));
            }
        }

        // Player inventory
        int playerInvStartY = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int slotIndex = col + row * 9 + 9;
                this.addSlot(new Slot(inv, slotIndex, 8 + col * 18, playerInvStartY + row * 18));
            }
        }

        // Hotbar
        int hotbarY = 142;
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, hotbarY));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.bound && this.blockEntity != null) {
            return access.evaluate((world, pos) ->
                world.getBlockState(pos).getBlock() == this.blockEntity.getBlockState().getBlock(), true);
        }
        return true;
    }

    @Override
    public Map<Integer, Slot> get() {
        return customSlots;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            int containerSlots = (blockEntity instanceof net.minecraft.world.Container container) ? container.getContainerSize() : 0;

            if (index < containerSlots) {
                if (!this.moveItemStackTo(itemstack1, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        if (cabinetPos != null && world instanceof ServerLevel serverLevel) {
            BlockState state = world.getBlockState(cabinetPos);

            if (state.hasProperty(OakKitchenCabinetBlock.OPEN)) {
                world.playSound(null, cabinetPos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                serverLevel.scheduleTick(cabinetPos, state.getBlock(), 1);
            } else if (state.hasProperty(SpruceKitchenCabinetBlock.OPEN)) {
                world.playSound(null, cabinetPos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                serverLevel.scheduleTick(cabinetPos, state.getBlock(), 1);
            } else if (state.hasProperty(BirchKitchenCabinetBlock.OPEN)) {
                world.playSound(null, cabinetPos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                serverLevel.scheduleTick(cabinetPos, state.getBlock(), 1);
            } else if (state.hasProperty(AcaciaKitchenCabinetBlock.OPEN)) {
                world.playSound(null, cabinetPos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.0f, 1.0f);
                serverLevel.scheduleTick(cabinetPos, state.getBlock(), 1);
            }
        }
    }
}
