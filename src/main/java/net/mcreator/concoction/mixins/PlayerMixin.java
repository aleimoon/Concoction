package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.interfaces.IPlayerUnsuccessfulAttempts;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(net.minecraft.world.entity.player.Player.class)
public abstract class PlayerMixin implements IPlayerUnsuccessfulAttempts {



    @Unique
    private int concoction$unsuccessfulAttempts = 0;

    @Unique
    public int concoction$getUnsuccessfulAttempts() {
        return concoction$unsuccessfulAttempts;
    }

    @Unique
    public void concoction$setUnsuccessfulAttempts(int unsuccessfulAttempts) {
        this.concoction$unsuccessfulAttempts = unsuccessfulAttempts;
    }

    @Unique
    public void concoction$incrementUnsuccessfulAttempts() {
        concoction$unsuccessfulAttempts++;
    }
    @Unique
    public void concoction$decrementUnsuccessfulAttempts() {
        concoction$unsuccessfulAttempts--;
    }



    @Inject(method = "eat", at = @At("RETURN"))
    private void concoction$applySaltnessFoodBoost(Level pLevel, ItemStack pFood, FoodProperties pFoodProperties, CallbackInfoReturnable<ItemStack> cir) {
        Player player = (Player) (Object) this;
        MobEffectInstance saltnessEffect = player.getEffect(ConcoctionModMobEffects.SALTNESS);

        if (saltnessEffect != null) {
            TagKey<Item> drinkTag = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/drink"));

            if (pFood.is(drinkTag)) {
                int amplifier = saltnessEffect.getAmplifier();
                int hungerBonus = 2;
                float saturationBonus = 3.0F + (amplifier * 1.5F);

                player.getFoodData().eat(hungerBonus, 0);

                float currentSaturation = player.getFoodData().getSaturationLevel();
                float newSaturation = Math.min(currentSaturation + saturationBonus, 20.0F);
                player.getFoodData().setSaturation(newSaturation);
            }
        }
    }


    @Redirect(method = "eat", at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void onEat(Level instance, Player player, double p_46544_, double p_46545_, double p_46546_, SoundEvent p_46547_, SoundSource p_46548_, float p_46549_, float p_46550_, Level pLevel, ItemStack itemStack, FoodProperties foodProperties) {

        if (itemStack.getItem().equals(ConcoctionModItems.MINT_BREW.get()) || itemStack.getItem().equals(ConcoctionModItems.OBSIDIAN_TEARS_BOTTLE.get()) ||
        itemStack.getItem().equals(ConcoctionModItems.HOT_SAUCE_BOTTLE.get())) {
            instance.playSound(player, p_46544_, p_46545_, p_46546_, Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("concoction:silence"))), p_46548_, p_46549_, p_46550_);
        } else {
            instance.playSound(player, p_46544_, p_46545_, p_46546_, p_46547_, p_46548_, p_46549_, p_46550_);
        }
    }

    @Inject(method = "causeFoodExhaustion(F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"), cancellable = true)
    private void onCauseFoodExhaustion(float exhaustionValue, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
            int dayTime = Math.floorMod(player.level().dayTime(), 24000);
            if (((dayTime >= 0 && dayTime < 13000) || (dayTime >= 23000 && dayTime < 24000)) &&
                    player.level().canSeeSky(player.blockPosition().above())) {
                int effectLevel = Objects.requireNonNull(player.getEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)).getAmplifier();
                player.getFoodData().addExhaustion(Math.max(exhaustionValue - (exhaustionValue * (0.3f + (effectLevel * 0.2f))), 0));
                ci.cancel();
                return;
            }
        }
        if (player.hasEffect(ConcoctionModMobEffects.BITTERNESS)) {
            int effectLevel = player.getEffect(ConcoctionModMobEffects.BITTERNESS).getAmplifier();
            float multiplier = 1.2f + 0.1f * effectLevel;
            player.getFoodData().addExhaustion(exhaustionValue * multiplier);
            ci.cancel();
            return;
        }
    }

    @Inject(method = "canWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private void concoction$warmingPowderSnowImmunity(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player)(Object)this;
        if (self.hasEffect(ConcoctionModMobEffects.WARMING)) {
            int amplifier = self.getEffect(ConcoctionModMobEffects.WARMING).getAmplifier();
            if (amplifier >= 2) { // 3 уровень и выше
                cir.setReturnValue(true);
            }
        }
    }

}
