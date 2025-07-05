package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.mcreator.concoction.item.food.types.FoodEffectComponent.*;
import static net.mcreator.concoction.item.food.types.FoodEffectType.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModDataComponents {

    // In another class
    // The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ConcoctionMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT = REGISTRY.registerComponentType(
            "food_effect",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_2 = REGISTRY.registerComponentType(
            "food_effect_2",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_3 = REGISTRY.registerComponentType(
            "food_effect_3",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_4 = REGISTRY.registerComponentType(
            "food_effect_4",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );
    
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_5 = REGISTRY.registerComponentType(
            "food_effect_5",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );

    // Listened to on the mod event bus
    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.CHORUS_FRUIT, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(INSTABILITY, 1, 10, false))
        );
        event.modify(Items.GLOW_BERRIES, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(GLOW, 1, 3, true))
        );

        event.modify(Items.MELON_SLICE, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 1, 30, true))
        );
        event.modify(Items.SWEET_BERRIES, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 1, 15, true))
        );
        event.modify(Items.PUMPKIN_PIE, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 2, 30, true))
        );
        event.modify(Items.COOKIE, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 1, 15, true))
        );
        event.modify(Items.HONEY_BOTTLE, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 2, 30, true))
        );
        event.modify(Items.APPLE, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 1, 15, true))
        );
        event.modify(Items.GOLDEN_APPLE, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 2, 15, true))
        );
        event.modify(Items.ENCHANTED_GOLDEN_APPLE, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SWEET, 5, 15, true))
        );
        event.modify(Items.PUFFERFISH, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(BITTER, 2, 90, true))
        );
        event.modify(Items.DRIED_KELP, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(SALTY, 1, 30, true))
        );
        event.modify(Items.POTATO, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(BITTER, 1, 90, true))
        );
        event.modify(Items.POISONOUS_POTATO, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(BITTER, 2, 180, true))
        );
        event.modify(Items.SUSPICIOUS_STEW, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent(BITTER, 1, 180, true))
        );
    }
}
