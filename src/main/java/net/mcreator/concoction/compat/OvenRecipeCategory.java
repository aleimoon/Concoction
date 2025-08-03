package net.mcreator.concoction.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class OvenRecipeCategory implements IRecipeCategory<OvenRecipe> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID,
            "textures/gui/oven_gui_jei.png");

    public static final RecipeType<OvenRecipe> OVEN_RECIPE_TYPE = RecipeType.create(ConcoctionMod.MODID, "oven",
            OvenRecipe.class);



    @Nonnull
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    protected final IDrawableAnimated arrow;




    public OvenRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 170, 76);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ConcoctionModBlocks.OVEN));
        this.arrow = helper.drawableBuilder(TEXTURE, 171, 14, 23, 16)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
        this.slot = helper.drawableBuilder(TEXTURE, 171, 31, 18, 18)
                .build();
    }

    @Override
    public RecipeType<OvenRecipe> getRecipeType() {
        return OVEN_RECIPE_TYPE;
    }
    @Override
    public Component getTitle() {
        return Component.translatable("gui.oven.title");
    }

    @SuppressWarnings("removal")
    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(OvenRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 100, 30);
    }

    public static boolean isCursorInsideBounds(int iconX, int iconY, int iconWidth, int iconHeight, double cursorX, double cursorY) {
        return iconX <= cursorX && cursorX < iconX + iconWidth && iconY <= cursorY && cursorY < iconY + iconHeight;
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, OvenRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (isCursorInsideBounds(100, 31, 24, 16, mouseX, mouseY)) {
            int cookTimeSeconds = recipe.getCookingTime() / 20;
            if (cookTimeSeconds > 0)
                tooltip.add(Component.translatable("gui.cooking_cauldron.time.seconds", cookTimeSeconds));
        }
    }



    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, OvenRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> list = recipe.getCraftingIngredients();

        int i = 0;
        int baseX = 38;
        int baseY = 20;

        while (i < list.size()) {
            builder.addSlot(RecipeIngredientRole.INPUT, baseX, baseY).addIngredients(list.get(i));
            if (baseX < 74) {
                baseX += 18;
            } else {
                baseY += 18;
                baseX = 38;
            }
            i++;
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 136, 30).addItemStack(
                new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(recipe.getResult().get("id"))),
                        Integer.parseInt(recipe.getResult().get("count")) )
        );

        if (!recipe.getBowlIngredient().hasNoItems()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 101, 9)
                    .addIngredients(recipe.getBowlIngredient());
        }

        if (!recipe.getBottleIngredient().hasNoItems()){
            builder.addSlot(RecipeIngredientRole.CATALYST, 16, 29)
                    .setBackground(slot, 0,0)
                    .addIngredients(recipe.getBottleIngredient());
        }
    }
}
