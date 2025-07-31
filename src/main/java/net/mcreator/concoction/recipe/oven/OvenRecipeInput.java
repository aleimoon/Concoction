package net.mcreator.concoction.recipe.oven;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record OvenRecipeInput(NonNullList<ItemStack> items) implements RecipeInput {
    
    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}