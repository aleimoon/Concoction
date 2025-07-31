package net.mcreator.concoction.recipe.oven;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OvenRecipe implements Recipe<OvenRecipeInput> {
    private final int cookingTime;
    private final List<Ingredient> craftingIngredients; // 6 слотов для крафта
    private final Ingredient bottleIngredient; // слот с иконкой бутылочки
    private final Ingredient bowlIngredient; // слот с иконкой миски
    private final Map<String, String> result;

    public OvenRecipe(int cookingTime, List<Ingredient> craftingIngredients, 
                     Ingredient bottleIngredient, Ingredient bowlIngredient, 
                     Map<String, String> result) {
        this.cookingTime = cookingTime;
        this.craftingIngredients = craftingIngredients;
        this.bottleIngredient = bottleIngredient;
        this.bowlIngredient = bowlIngredient;
        this.result = result;
    }

    public List<Ingredient> getCraftingIngredients() {
        return craftingIngredients;
    }

    public Ingredient getBottleIngredient() {
        return bottleIngredient;
    }

    public Ingredient getBowlIngredient() {
        return bowlIngredient;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    private boolean matchesIngredients(NonNullList<ItemStack> inventory) {
        // Проверяем слоты крафта (1-6)
        NonNullList<ItemStack> craftingSlots = NonNullList.withSize(6, ItemStack.EMPTY);
        for (int i = 1; i <= 6; i++) {
            craftingSlots.set(i-1, inventory.get(i));
        }

        // Проверяем слот бутылочки (0)
        ItemStack bottleSlot = inventory.get(0);
        
        // Проверяем слот миски (7)
        ItemStack bowlSlot = inventory.get(7);

        // Проверяем соответствие ингредиентов крафта (порядок не важен)
        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        for (Ingredient ingredient : craftingIngredients) {
            requiredIngredients.merge(ingredient, 1, Integer::sum);
        }

        // Подсчитываем непустые слоты крафта
        int nonEmptySlots = 0;
        for (ItemStack itemStack : craftingSlots) {
            if (!itemStack.isEmpty()) {
                nonEmptySlots++;
            }
        }

        // Количество непустых слотов должно совпадать с количеством ингредиентов
        if (nonEmptySlots != craftingIngredients.size()) {
            return false;
        }

        // Проверяем каждый слот крафта
        for (ItemStack itemStack : craftingSlots) {
            if (itemStack.isEmpty()) continue;
            
            boolean matched = false;
            for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
                if (entry.getValue() > 0 && entry.getKey().test(itemStack)) {
                    entry.setValue(entry.getValue() - 1);
                    matched = true;
                    break;
                }
            }
            
            if (!matched) {
                return false;
            }
        }

        // Проверяем, что все ингредиенты найдены
        if (!requiredIngredients.values().stream().allMatch(count -> count <= 0)) {
            return false;
        }

        // Проверяем слот бутылочки
        if (!bottleIngredient.isEmpty() && !bottleIngredient.test(bottleSlot)) {
            return false;
        }
        if (bottleIngredient.isEmpty() && !bottleSlot.isEmpty()) {
            return false;
        }

        // Проверяем слот миски
        if (!bowlIngredient.isEmpty() && !bowlIngredient.test(bowlSlot)) {
            return false;
        }
        if (bowlIngredient.isEmpty() && !bowlSlot.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean matches(OvenRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return matchesIngredients(input.items());
    }

    @Override
    public ItemStack assemble(OvenRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Items.FURNACE);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ConcoctionModRecipes.OVEN_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ConcoctionModRecipes.OVEN_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<OvenRecipe> {
        public static final MapCodec<OvenRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.INT.fieldOf("cooking_time").orElse(200).forGetter(OvenRecipe::getCookingTime),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("crafting_ingredients").forGetter(OvenRecipe::getCraftingIngredients),
                Ingredient.CODEC.optionalFieldOf("bottle_ingredient", Ingredient.EMPTY).forGetter(OvenRecipe::getBottleIngredient),
                Ingredient.CODEC.optionalFieldOf("bowl_ingredient", Ingredient.EMPTY).forGetter(OvenRecipe::getBowlIngredient),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("result").forGetter(OvenRecipe::getResult)
        ).apply(inst, OvenRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.INT, OvenRecipe::getCookingTime,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), OvenRecipe::getCraftingIngredients,
                        Ingredient.CONTENTS_STREAM_CODEC, OvenRecipe::getBottleIngredient,
                        Ingredient.CONTENTS_STREAM_CODEC, OvenRecipe::getBowlIngredient,
                        ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), OvenRecipe::getResult,
                        OvenRecipe::new);

        @Override
        public MapCodec<OvenRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}