package net.mcreator.concoction.mixins;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Snowball.class)
public abstract class SnowballMixin {

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void concoction$freezeOnSnowballHit(EntityHitResult result, CallbackInfo ci) {
        Snowball snowball = (Snowball)(Object)this;

        if (!snowball.level().isClientSide) {
            Entity hitEntity = result.getEntity();

            if (hitEntity instanceof LivingEntity living) {
                // Пропуск, если моб может ходить по порошковому снегу
                if (living.getType().is(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)) {
                    return;
                }
                DamageSource source;

                    source = snowball.damageSources().freeze();
                

                living.hurt(source, 1.0F);

                // Подсчёт кожаной брони
                int leatherCount = 0;
                if (living.getItemBySlot(EquipmentSlot.HEAD).is(Items.LEATHER_HELMET)) leatherCount++;
                if (living.getItemBySlot(EquipmentSlot.CHEST).is(Items.LEATHER_CHESTPLATE)) leatherCount++;
                if (living.getItemBySlot(EquipmentSlot.LEGS).is(Items.LEATHER_LEGGINGS)) leatherCount++;
                if (living.getItemBySlot(EquipmentSlot.FEET).is(Items.LEATHER_BOOTS)) leatherCount++;

                // Шанс заморозки: -25% за каждую кожаную часть
                float freezeChance = 1.0f - 0.25f * leatherCount;

                if (Math.random() < freezeChance) {
                    int frozen = living.getTicksFrozen();
                    if (frozen >= 200) {
                        living.setTicksFrozen(350);
                    } else {
                        living.setTicksFrozen(frozen + 100);
                    }
                }
            }
        }
    }
}
