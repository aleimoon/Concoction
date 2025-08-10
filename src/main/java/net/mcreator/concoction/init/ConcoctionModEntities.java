
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.entity.SunstruckEntity;
import net.mcreator.concoction.entity.CordycepsSpiderEntity;
import net.mcreator.concoction.entity.CordycepsCaveSpiderEntity;
import net.mcreator.concoction.ConcoctionMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, ConcoctionMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<SunstruckEntity>> SUNSTRUCK = register("sunstruck",
			EntityType.Builder.<SunstruckEntity>of(SunstruckEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.8f));
	public static final DeferredHolder<EntityType<?>, EntityType<CordycepsSpiderEntity>> CORDYCEPS_SPIDER = register("cordyceps_spider",
			EntityType.Builder.<CordycepsSpiderEntity>of(CordycepsSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(1.4f, 0.9f));
	public static final DeferredHolder<EntityType<?>, EntityType<CordycepsCaveSpiderEntity>> CORDYCEPS_CAVE_SPIDER = register("cordyceps_cave_spider",
			EntityType.Builder.<CordycepsCaveSpiderEntity>of(CordycepsCaveSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.7f, 0.5f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(RegisterSpawnPlacementsEvent event) {
		SunstruckEntity.init(event);
		CordycepsSpiderEntity.init(event);
		CordycepsCaveSpiderEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(SUNSTRUCK.get(), SunstruckEntity.createAttributes().build());
		event.put(CORDYCEPS_SPIDER.get(), CordycepsSpiderEntity.createAttributes().build());
		event.put(CORDYCEPS_CAVE_SPIDER.get(), CordycepsCaveSpiderEntity.createAttributes().build());
	}
}
