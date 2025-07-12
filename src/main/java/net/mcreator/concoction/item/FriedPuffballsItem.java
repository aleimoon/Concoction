
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;


public class FriedPuffballsItem extends Item {
	public FriedPuffballsItem() {
		super(new Item.Properties().stacksTo(64).
				component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.SALTY, 1, 180, true)).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.6f).build()));
	}
}
