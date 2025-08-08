
package net.mcreator.concoction.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.SpiderModel;

import net.mcreator.concoction.entity.CordycepsCaveSpiderEntity;

public class CordycepsCaveSpiderRenderer extends MobRenderer<CordycepsCaveSpiderEntity, SpiderModel<CordycepsCaveSpiderEntity>> {
	public CordycepsCaveSpiderRenderer(EntityRendererProvider.Context context) {
		super(context, new SpiderModel<CordycepsCaveSpiderEntity>(context.bakeLayer(ModelLayers.SPIDER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(CordycepsCaveSpiderEntity entity) {
		return ResourceLocation.parse("concoction:textures/entities/cordyceps_cave_spider.png");
	}

	@Override
	protected boolean isShaking(CordycepsCaveSpiderEntity entity) {
		return true;
	}
}
