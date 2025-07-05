
package net.mcreator.concoction.item;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

public class ChocolateItem extends Item {
	public ChocolateItem() {
		super(new Item.Properties().stacksTo(64).component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.SWEET, 2, 90, true)).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.6f).build()));
	}
}
