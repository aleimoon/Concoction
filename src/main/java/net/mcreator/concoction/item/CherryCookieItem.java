
package net.mcreator.concoction.item;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;


import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;
import static net.mcreator.concoction.item.food.types.FoodEffectType.SWEET;

public class CherryCookieItem extends Item {
	public CherryCookieItem() {
		super(new Item.Properties().stacksTo(64).component(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 1, 30, true)).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.4f).build()));
	}
}
