package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "setTicksFrozen", at = @At("HEAD"), cancellable = true)
    private void concoction$modifyTicksFrozen(int ticks, CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity self && self.hasEffect(ConcoctionModMobEffects.WARMING)) {
            int amplifier = self.getEffect(ConcoctionModMobEffects.WARMING).getAmplifier();
            int current = self.getTicksFrozen();

            if (amplifier >= 1) {
                // Level 1 or higher: не разрешаем увеличение заморозки (заморозка не происходит)
                if (ticks > current) {
                    ci.cancel();
                }
                // Но разрешаем уменьшение ticksFrozen (разморозка) без ограничений
            } else {
                // Level 0: заморозка в 4 раза медленнее (только 1 из 4 увеличений разрешаем)
                if (ticks > current) {
                    if (new Random().nextInt(4) != 0) {
                        ci.cancel();
                    }
                }
                // Разморозка (ticks < current) будет происходить без изменений
            }
        }
    }
}
