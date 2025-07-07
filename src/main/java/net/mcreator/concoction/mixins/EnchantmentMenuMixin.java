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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public class EnchantmentMenuMixin {

    @Unique
    private int concoction$cachedId = -1;

    @Unique
    private int concoction$cachedXpBefore = 0;

    @Inject(method = "clickMenuButton", at = @At("HEAD"))
    private void concoction$cacheXp(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        concoction$cachedId = id;
        concoction$cachedXpBefore = getTotalXp(player);
    }

    @Inject(method = "clickMenuButton", at = @At("RETURN"))
    private void concoction$onEnchant(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        Boolean result = cir.getReturnValue();
        if (result != null && result && concoction$cachedId >= 0 && concoction$cachedId <= 2) {
            int xpAfter = getTotalXp(player);
            int xpSpent = concoction$cachedXpBefore - xpAfter;

            if (xpSpent > 0 && player.hasEffect(ConcoctionModMobEffects.BITTERNESS)) {
                MobEffectInstance bitterness = player.getEffect(ConcoctionModMobEffects.BITTERNESS);
                int amplifier = bitterness.getAmplifier();
                double multiplier = 0.2 + 0.1 * amplifier;
                int cashback = (int) (xpSpent * multiplier);

                if (cashback > 0) {
                    player.giveExperiencePoints(cashback);
                }
            }
        }

        // Сброс кеша
        concoction$cachedId = -1;
        concoction$cachedXpBefore = 0;
    }

    // Утилита: рассчитывает общее количество опыта игрока
    @Unique
    private static int getTotalXp(Player player) {
        int level = player.experienceLevel;
        float progress = player.experienceProgress;
        int xpForLevel = getXpNeededForLevel(level);
        return getTotalXpAtLevel(level) + Math.round(progress * xpForLevel);
    }

    @Unique
    private static int getXpNeededForLevel(int level) {
        if (level >= 30) {
            return 112 + (level - 30) * 9;
        } else if (level >= 15) {
            return 37 + (level - 15) * 5;
        } else {
            return 7 + level * 2;
        }
    }

    @Unique
    private static int getTotalXpAtLevel(int level) {
        if (level >= 30) {
            return (int)(4.5 * level * level - 162.5 * level + 2220);
        } else if (level >= 15) {
            return (int)(2.5 * level * level - 40.5 * level + 360);
        } else {
            return level * level + 6 * level;
        }
    }
}
