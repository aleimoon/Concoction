
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.concoction.client.renderer.SunstruckRenderer;
import net.mcreator.concoction.client.renderer.CordycepsSpiderRenderer;
import net.mcreator.concoction.client.renderer.CordycepsCaveSpiderRenderer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConcoctionModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ConcoctionModEntities.SUNSTRUCK.get(), SunstruckRenderer::new);
		event.registerEntityRenderer(ConcoctionModEntities.CORDYCEPS_SPIDER.get(), CordycepsSpiderRenderer::new);
		event.registerEntityRenderer(ConcoctionModEntities.CORDYCEPS_CAVE_SPIDER.get(), CordycepsCaveSpiderRenderer::new);
	}
}
