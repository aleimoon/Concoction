package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.block.AnvilBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {
    @Unique
    private int concoction$lastCost = 0;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void concoction$cacheCost(Player player, ItemStack stack, CallbackInfo ci) {
        concoction$lastCost = ((AnvilMenu)(Object)this).getCost();
    }

    @Inject(method = "onTake", at = @At("TAIL"))
    private void concoction$onTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (player.hasEffect(ConcoctionModMobEffects.BITTERNESS)) {
            MobEffectInstance bitterness = player.getEffect(ConcoctionModMobEffects.BITTERNESS);
            int amplifier = bitterness.getAmplifier();
            int cashback = (int)(concoction$lastCost * (0.2 + 0.1 * amplifier));
            if (cashback > 0) {
                player.giveExperienceLevels(cashback);
            }
        }
    }
} 