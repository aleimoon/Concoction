
package net.mcreator.concoction.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SweetnessMobEffect extends MobEffect {
	public SweetnessMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -52);
	}

}
