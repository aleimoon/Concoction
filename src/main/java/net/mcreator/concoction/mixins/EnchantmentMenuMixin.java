package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {
    @Unique
    private int concoction$cachedCost = 0;
    @Unique
    private int concoction$cachedId = -1;

    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    private void concoction$cacheCost(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        // Кэшируем id и стоимость до выполнения vanilla-логики
        concoction$cachedId = id;
        if (id >= 0 && id <= 2) {
            concoction$cachedCost = ((EnchantmentMenu)(Object)this).costs[id];
        } else {
            concoction$cachedCost = 0;
        }
    }

    @Inject(method = "clickMenuButton", at = @At("RETURN"))
    private void concoction$onEnchant(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        Boolean result = cir.getReturnValue();
        if (result != null && result && concoction$cachedId >= 0 && concoction$cachedId <= 2 && concoction$cachedCost > 0) {
            if (player.hasEffect(ConcoctionModMobEffects.BITTERNESS)) {
                MobEffectInstance bitterness = player.getEffect(ConcoctionModMobEffects.BITTERNESS);
                int amplifier = bitterness.getAmplifier();
                int cashback = (int)(concoction$cachedCost * (0.2 + 0.1 * amplifier));
                if (cashback > 0) {
                    player.giveExperienceLevels(cashback);
                }
            }
        }
        // Очищаем кэш
        concoction$cachedId = -1;
        concoction$cachedCost = 0;
    }
} 