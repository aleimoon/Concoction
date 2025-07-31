package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipe;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConcoctionModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ConcoctionMod.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ConcoctionMod.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CauldronBrewingRecipe>> CAULDRON_BREWING_RECIPE_SERIALIZER =
            SERIALIZERS.register("cauldron_brewing", CauldronBrewingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CauldronBrewingRecipe>> CAULDRON_BREWING_RECIPE_TYPE =
            TYPES.register("cauldron_brewing", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "cauldron_brewing";
                }
            });
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ButterChurnRecipe>> BUTTER_CHURN_RECIPE_SERIALIZER =
            SERIALIZERS.register("butter_churn", ButterChurnRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<ButterChurnRecipe>> BUTTER_CHURN_RECIPE_TYPE =
            TYPES.register("butter_churn", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "butter_churn";
                }
            });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<OvenRecipe>> OVEN_RECIPE_SERIALIZER =
            SERIALIZERS.register("oven", OvenRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<OvenRecipe>> OVEN_RECIPE_TYPE =
            TYPES.register("oven", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return "oven";
                }
            });
    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
