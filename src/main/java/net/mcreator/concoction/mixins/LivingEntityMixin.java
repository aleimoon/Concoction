package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyVariable(
            method = "hurt",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private float concoction$applySaltnessDamage(float amount, DamageSource source) {
        if (source.getEntity() instanceof Player player) {
            MobEffectInstance saltnessEffect = player.getEffect(ConcoctionModMobEffects.SALTNESS);
            if (saltnessEffect != null) {
                LivingEntity target = (LivingEntity) (Object) this;
                if (target.getType().getCategory() == MobCategory.MONSTER || target instanceof Slime) {
                    int amplifier = saltnessEffect.getAmplifier();
                    return amount * (1.25f + 0.25f * (amplifier + 1));
                }
            }
        }
        return amount;
    }

    @ModifyVariable(
            method = "hurt",
            at = @At(value = "HEAD"),
            argsOnly = true
    )
    private float concoction$applyWarmingColdDamage(float amount, DamageSource source) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.hasEffect(ConcoctionModMobEffects.WARMING)) {
            Holder<net.minecraft.world.damagesource.DamageType> type = source.typeHolder();
            String id = type.unwrapKey().map(k -> k.location().getPath()).orElse("");
            if (id.contains("freeze") || id.contains("frozen") || id.contains("powder_snow") || id.contains("cold")) {
                int amplifier = self.getEffect(ConcoctionModMobEffects.WARMING).getAmplifier();
                float multiplier = 1.0f - 0.25f * (amplifier + 1);
                if (multiplier < 0) multiplier = 0;
                return amount * multiplier;
            }
        }
        return amount;
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    private void concoction$warmingPowderSnowImmunity(FluidState state, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.hasEffect(ConcoctionModMobEffects.WARMING)) {
            int amplifier = self.getEffect(ConcoctionModMobEffects.WARMING).getAmplifier();
            
            System.out.println(state.getType().toString());
            if (amplifier >= 2 && state.getType().toString().toLowerCase().contains("powder_snow")) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void concoction$warmingPowderSnowImmunity(CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity self && self.hasEffect(ConcoctionModMobEffects.WARMING)) {
            int amplifier = self.getEffect(ConcoctionModMobEffects.WARMING).getAmplifier();
            if (amplifier >= 2) {

                ((EntityAccessor)this).setIsInPowderSnow(false);
            }
        }
    }
} 