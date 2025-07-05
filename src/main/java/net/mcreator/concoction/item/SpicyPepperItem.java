
package net.mcreator.concoction.item;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;


import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;

public class SpicyPepperItem extends Item {
	public SpicyPepperItem() {
		super(new Item.Properties().stacksTo(64).
				component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.SPICY, 1, 15, true)).
//				component(FOOD_EFFECT_2.value(), new FoodEffectComponent(FoodEffectType.SWEET, 2, 30, true)).
//				component(FOOD_EFFECT_3.value(), new FoodEffectComponent(FoodEffectType.MINTY, 1, 16, true)).
				rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.3f).build()));
	}
}
