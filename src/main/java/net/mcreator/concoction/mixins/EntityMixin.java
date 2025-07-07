package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "setTicksFrozen", at = @At("HEAD"), cancellable = true)
    private void concoction$modifyTicksFrozen(int ticks, CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity self && self.hasEffect(ConcoctionModMobEffects.WARMING)) {
            int amplifier = self.getEffect(ConcoctionModMobEffects.WARMING).getAmplifier();
            if (amplifier >= 1) {
                ci.cancel();
            } else {
                Random random = new Random();
                if (random.nextInt(100) <= 25) {
                    ci.cancel();
                }
            }
        }
    }


}
