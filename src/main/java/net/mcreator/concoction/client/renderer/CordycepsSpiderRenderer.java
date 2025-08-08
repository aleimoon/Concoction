
package net.mcreator.concoction.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.SpiderModel;

import net.mcreator.concoction.entity.CordycepsSpiderEntity;

public class CordycepsSpiderRenderer extends MobRenderer<CordycepsSpiderEntity, SpiderModel<CordycepsSpiderEntity>> {
	public CordycepsSpiderRenderer(EntityRendererProvider.Context context) {
		super(context, new SpiderModel<CordycepsSpiderEntity>(context.bakeLayer(ModelLayers.SPIDER)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(CordycepsSpiderEntity entity) {
		return ResourceLocation.parse("concoction:textures/entities/cordyceps_spider.png");
	}

	@Override
	protected boolean isShaking(CordycepsSpiderEntity entity) {
		return true;
	}
}
