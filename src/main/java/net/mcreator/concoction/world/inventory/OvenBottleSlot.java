package net.mcreator.concoction.world.inventory;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class OvenBottleSlot extends SlotItemHandler {
    
    public OvenBottleSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        // Разрешаем только предметы с тегом c:bottles
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1; // Только один предмет в слоте
    }
}