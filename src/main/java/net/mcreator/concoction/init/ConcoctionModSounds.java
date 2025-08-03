
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, ConcoctionMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_HOTICE = REGISTRY.register("music_disc_hotice", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "music_disc_hotice")));
	public static final DeferredHolder<SoundEvent, SoundEvent> CAULDRON_COOKING = REGISTRY.register("cauldron_cooking", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "cauldron_cooking")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SILENCE = REGISTRY.register("silence", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "silence")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BUTTER_CHURN_SPIN = REGISTRY.register("butter_churn_spin", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "butter_churn_spin")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BARREL_OVERFILLED = REGISTRY.register("barrel_overfilled", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "barrel_overfilled")));
	public static final DeferredHolder<SoundEvent, SoundEvent> BUTTER_THICKENS = REGISTRY.register("butter_thickens", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "butter_thickens")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SOUL_BUCKET_EMPTY = REGISTRY.register("soul_bucket_empty", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "soul_bucket_empty")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SOUL_BUCKET_FILLED = REGISTRY.register("soul_bucket_filled", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "soul_bucket_filled")));
	public static final DeferredHolder<SoundEvent, SoundEvent> CAULDRON_BOILING = REGISTRY.register("cauldron_boiling", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "cauldron_boiling")));
	public static final DeferredHolder<SoundEvent, SoundEvent> OVEN_OPEN = REGISTRY.register("oven_open", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "oven_open")));
	public static final DeferredHolder<SoundEvent, SoundEvent> OVEN_CLOSE = REGISTRY.register("oven_close", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "oven_close")));
}
