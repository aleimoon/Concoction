
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.GameRules;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModGameRules {
	public static GameRules.Key<GameRules.IntegerValue> MILKING_INTERVAL;

	@SubscribeEvent
	public static void registerGameRules(FMLCommonSetupEvent event) {
		MILKING_INTERVAL = GameRules.register("milkingInterval", GameRules.Category.MOBS, GameRules.IntegerValue.create(3000));
	}
}
