package net.mcreator.concoction.world.inventory;

import net.mcreator.concoction.block.entity.CookingCauldronEntity;
import net.mcreator.concoction.block.entity.OvenBlockEntity;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandler;

import net.minecraft.tags.ItemTags;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import net.mcreator.concoction.init.ConcoctionModMenus;
import net.mcreator.concoction.init.ConcoctionModSounds;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

public class OvenGUIMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private BlockPos pos;
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
		super(ConcoctionModMenus.OVEN_GUI.get(), id); // must be first line

		this.entity = inv.player;
		this.world = inv.player.level();
		this.pos = null;

		if (extraData != null) {
			this.pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);

			world.playLocalSound(
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5,
				ConcoctionModSounds.OVEN_OPEN.get(),
				SoundSource.BLOCKS,
				1.0f,
				1.0F,
				false
			);
		}

		if (world.getBlockEntity(pos) instanceof OvenBlockEntity blockEntity) {
			this.boundBlockEntity = blockEntity;
			this.bound = true;
			this.internal = new ItemStackHandler(9) {
				@Override
				public ItemStack getStackInSlot(int slot) {
					return blockEntity.getItem(slot);
				}
				
				@Override
				public void setStackInSlot(int slot, ItemStack stack) {
					blockEntity.setItem(slot, stack);
				}
				
				@Override
				public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
					if (!simulate) {
						blockEntity.setItem(slot, stack);
					}
					return ItemStack.EMPTY;
				}
				
				@Override
				public ItemStack extractItem(int slot, int amount, boolean simulate) {
					ItemStack existing = blockEntity.getItem(slot);
					if (existing.isEmpty()) return ItemStack.EMPTY;
					
					ItemStack extracted = existing.copy();
					extracted.setCount(Math.min(amount, existing.getCount()));
					
					if (!simulate) {
						existing.shrink(amount);
						if (existing.isEmpty()) {
							blockEntity.setItem(slot, ItemStack.EMPTY);
						}
					}
					
					return extracted;
				}
			};
		} else {
			this.internal = new ItemStackHandler(9);
		}

		addPlayerHotbar(inv);
		addPlayerInventory(inv);

		this.customSlots.put(36, this.addSlot(new OvenBottleSlot(internal, 0, 19, 33)));

		this.customSlots.put(37, this.addSlot(new SlotItemHandler(internal, 1, 41, 24)));
		this.customSlots.put(38, this.addSlot(new SlotItemHandler(internal, 2, 59, 24)));
		this.customSlots.put(39, this.addSlot(new SlotItemHandler(internal, 3, 77, 24)));
		this.customSlots.put(40, this.addSlot(new SlotItemHandler(internal, 4, 41, 42)));
		this.customSlots.put(41, this.addSlot(new SlotItemHandler(internal, 5, 59, 42)));
		this.customSlots.put(42, this.addSlot(new SlotItemHandler(internal, 6, 77, 42)));

		this.customSlots.put(43, this.addSlot(new OvenBowlSlot(internal, 7, 104, 13)));

		this.customSlots.put(44, this.addSlot(new SlotItemHandler(internal, 8, 139, 34) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
			@Override
			public void onTake(Player player, ItemStack stack) {
				super.onTake(player, stack);
				if (!player.level().isClientSide() && player instanceof ServerPlayer serverPlayer)
					Utils.addAchievement(serverPlayer, "concoction:oven_cooking");
			}
		}));
	}

	public int getProgress() {
		if (boundBlockEntity instanceof OvenBlockEntity entity) {
			return entity.getProgress();
		}
		return progress;
	}

	public int getMaxProgress() {
		if (boundBlockEntity instanceof OvenBlockEntity entity) {
			return entity.getMaxProgress();
		}
		return maxProgress;
	}

	public boolean isCooking() {
		if (boundBlockEntity instanceof OvenBlockEntity entity) {
			return entity.isCooking();
		}
		return isCooking;
	}

	public boolean isLit() {
		if (boundBlockEntity instanceof OvenBlockEntity entity) {
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

	private void addPlayerInventory(Inventory playerInventory) {
		for (int i = 0; i < 3; ++i) {
			for (int l = 0; l < 9; ++l) {
				this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
			}
		}
	}

	private void addPlayerHotbar(Inventory playerInventory) {
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (index >= 36 && index <= 44) {
				if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
					if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
						return ItemStack.EMPTY;
					}
				}
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (index >= 0 && index < 36) {
				boolean moved = false;
				if (itemstack1.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "tableware")))) {
					if (this.moveItemStackTo(itemstack1, 43, 44, false)) {
						moved = true;
					}
				}
				if (!moved) {
					if (this.moveItemStackTo(itemstack1, 36, 37, false)) {
						moved = true;
					} else if (this.moveItemStackTo(itemstack1, 37, 43, false)) {
						moved = true;
					}
				}
				if (!moved) {
					return ItemStack.EMPTY;
				}
			}
			
			if (itemstack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(playerIn, itemstack1);
		}
		
		return itemstack;
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (pos != null) {
			world.playLocalSound(
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5,
				ConcoctionModSounds.OVEN_CLOSE.get(),
				SoundSource.BLOCKS,
				1.0f,
				1.0F,
				false
			);
		}
	}
}
