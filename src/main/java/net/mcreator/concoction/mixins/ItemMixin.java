package net.mcreator.concoction.mixins;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.mcreator.concoction.utils.FoodTooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;

@Mixin({Item.class})
public class ItemMixin {
    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void addEatEffect(ItemStack itemStack, Level p_41410_, LivingEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (itemStack.get(FOOD_EFFECT.value()) != null && player != null) {
            FoodEffectComponent component = itemStack.get(FOOD_EFFECT.value());
            player.addEffect(FoodEffectType.getEffect(component.type(), component.level(), component.duration(), component.isHidden()));
        }
        if (itemStack.get(FOOD_EFFECT_2.value()) != null && player != null) {
            FoodEffectComponent component = itemStack.get(FOOD_EFFECT_2.value());
            player.addEffect(FoodEffectType.getEffect(component.type(), component.level(), component.duration(), component.isHidden()));
        }
        if (itemStack.get(FOOD_EFFECT_3.value()) != null && player != null) {
            FoodEffectComponent component = itemStack.get(FOOD_EFFECT_3.value());
            player.addEffect(FoodEffectType.getEffect(component.type(), component.level(), component.duration(), component.isHidden()));
        }
        if (itemStack.get(FOOD_EFFECT_4.value()) != null && player != null) {
            FoodEffectComponent component = itemStack.get(FOOD_EFFECT_4.value());
            player.addEffect(FoodEffectType.getEffect(component.type(), component.level(), component.duration(), component.isHidden()));
        }
        if (itemStack.get(FOOD_EFFECT_5.value()) != null && player != null) {
            FoodEffectComponent component = itemStack.get(FOOD_EFFECT_5.value());
            player.addEffect(FoodEffectType.getEffect(component.type(), component.level(), component.duration(), component.isHidden()));
        }
    }
    
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void addFoodEffectTooltip(ItemStack p_41421_, Item.TooltipContext p_339594_, List<Component> p_41423_, TooltipFlag p_41424_, CallbackInfo ci) {
        FoodTooltipHelper.addFoodEffectTooltip(p_41421_, p_41423_);
    }

}
