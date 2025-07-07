package net.mcreator.concoction.potion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.NeoForgeMod;

public class SaltnessMobEffect extends MobEffect {
	public SaltnessMobEffect() {
		super(MobEffectCategory.NEUTRAL, -2032643);
		this.addAttributeModifier(
				Attributes.BLOCK_BREAK_SPEED,
				ResourceLocation.parse("f9d21cb5-b0e5-4e1e-9993-b1d4a24285b2"),
				0.25F,
				AttributeModifier.Operation.ADD_MULTIPLIED_BASE
		);
		this.addAttributeModifier(
				Attributes.BLOCK_INTERACTION_RANGE,
				ResourceLocation.parse("f9d21cb5-b0e5-4e1e-9993-b1d4a25287b3"),
				1.0F,
				AttributeModifier.Operation.ADD_VALUE
		);
	}
}
