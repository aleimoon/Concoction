
package net.mcreator.concoction.world.inventory;

import net.mcreator.concoction.block.entity.OvenBlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandler;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.init.ConcoctionModMenus;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

public class OvenGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
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

	public OvenGUIMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(ConcoctionModMenus.OVEN_GUI.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(9);
		BlockPos pos = null;
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
		}

		addPlayerHotbar(inv);
		addPlayerInventory(inv);

		this.customSlots.put(36, this.addSlot(new SlotItemHandler(internal, 0, 19, 33)));
		this.customSlots.put(37, this.addSlot(new SlotItemHandler(internal, 1, 41, 24)));
		this.customSlots.put(38, this.addSlot(new SlotItemHandler(internal, 2, 59, 24)));
		this.customSlots.put(39, this.addSlot(new SlotItemHandler(internal, 3, 77, 24)));
		this.customSlots.put(40, this.addSlot(new SlotItemHandler(internal, 4, 41, 42)));
		this.customSlots.put(41, this.addSlot(new SlotItemHandler(internal, 5, 59, 42)));
		this.customSlots.put(42, this.addSlot(new SlotItemHandler(internal, 6, 77, 42)));
		this.customSlots.put(43, this.addSlot(new SlotItemHandler(internal, 7, 106, 13)));
		this.customSlots.put(44, this.addSlot(new SlotItemHandler(internal, 8, 139, 34)));


	}


	// Метод для получения текущего прогресса готовки
	public int getProgress() {
		if(boundBlockEntity instanceof OvenBlockEntity entity) {
			return entity.getProgress();
		}
		return progress;
	}

	// Метод для получения максимального прогресса готовки
	public int getMaxProgress() {
		if(boundBlockEntity instanceof OvenBlockEntity entity) {
			return entity.getMaxProgress();
		}
		return maxProgress;
	}

	public boolean isCooking() {
		if(boundBlockEntity instanceof OvenBlockEntity entity) {
			return entity.isCooking();
		}
		return isCooking;
	}

	public boolean isLit() {
		if(boundBlockEntity instanceof OvenBlockEntity entity) {
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

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		return ItemStack.EMPTY;
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}
}
