package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ConcoctionModCustomTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ConcoctionMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CONCOCTION = REGISTRY.register("concoction",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.concoction.concoction")).icon(() -> new ItemStack(ConcoctionModBlocks.MINT.get())).displayItems((parameters, tabData) -> {
//Растения, семена, плоды, еда
				tabData.accept(ConcoctionModItems.CORN.get());
				tabData.accept(ConcoctionModItems.CORN_SEEDS.get());
				tabData.accept(ConcoctionModItems.POPCORN.get());
				tabData.accept(ConcoctionModItems.COOKED_CORN.get());
				tabData.accept(ConcoctionModItems.GOLDEN_CORN.get());
				tabData.accept(ConcoctionModItems.SPICY_PEPPER.get());
				tabData.accept(ConcoctionModItems.SPICY_PEPPER_SEEDS.get());
				tabData.accept(ConcoctionModItems.ONION.get());
				tabData.accept(ConcoctionModItems.GREEN_ONION.get());
				tabData.accept(ConcoctionModItems.COTTON.get());
				tabData.accept(ConcoctionModItems.SUNFLOWER_SEEDS.get());
				tabData.accept(ConcoctionModItems.ROASTED_SUNFLOWER_SEEDS.get());
				tabData.accept(ConcoctionModItems.CABBAGEHEAD.get());
				tabData.accept(ConcoctionModItems.CABBAGE_LEAF.get());
				tabData.accept(ConcoctionModItems.CABBAGE_SEEDS.get());
				tabData.accept(ConcoctionModBlocks.MINT.get().asItem());
				tabData.accept(ConcoctionModItems.MINT_SEEDS.get());
				tabData.accept(ConcoctionModItems.CHERRY.get());
				tabData.accept(ConcoctionModItems.PINECONE.get());
				tabData.accept(ConcoctionModItems.ROASTED_PINECONE.get());
				tabData.accept(ConcoctionModItems.RICE.get());
				tabData.accept(ConcoctionModItems.TOMATO.get());
				tabData.accept(ConcoctionModItems.TOMATO_SEEDS.get());
				tabData.accept(ConcoctionModItems.REAPPER.get());
				tabData.accept(ConcoctionModItems.REAPPER_SEEDS.get());
				tabData.accept(ConcoctionModItems.PUFFBALL.get());
				tabData.accept(ConcoctionModItems.COOKED_PUFFBALL.get());
				tabData.accept(ConcoctionModItems.PUFFBALL_SPORES.get());

				
// Пищевые Материалы
				tabData.accept(ConcoctionModItems.FABRIC.get());
				tabData.accept(ConcoctionModItems.ANIMAL_FAT.get());
				tabData.accept(ConcoctionModItems.FLOUR.get());
				tabData.accept(ConcoctionModItems.DOUGH.get());
				tabData.accept(ConcoctionModItems.RAW_NOODLES.get());
				tabData.accept(ConcoctionModItems.MILK_BOTTLE.get());
				tabData.accept(ConcoctionModItems.CREAM_BOTTLE.get());
				tabData.accept(ConcoctionModItems.HOT_SAUCE_BOTTLE.get());
				tabData.accept(ConcoctionModItems.SUNFLOWER_OIL.get());
				tabData.accept(ConcoctionModItems.CORN_OIL.get());
				tabData.accept(ConcoctionModItems.COTTON_OIL.get());
				tabData.accept(ConcoctionModItems.BUTTER.get());
				tabData.accept(ConcoctionModItems.CHOCOLATE.get());
				tabData.accept(ConcoctionModItems.ROCK_SALT.get());
				tabData.accept(ConcoctionModItems.SEA_SALT.get());

//Сложная еда
				tabData.accept(ConcoctionModItems.HASHBROWNS.get());
				tabData.accept(ConcoctionModItems.FRIED_PUFFBALLS.get());
				tabData.accept(ConcoctionModItems.BOILED_EGG.get());
				tabData.accept(ConcoctionModItems.FRIED_EGG.get());
				tabData.accept(ConcoctionModItems.BUTTER_SANDWICH.get());
				tabData.accept(ConcoctionModItems.HANAMI_DANGO.get());
				tabData.accept(ConcoctionModItems.MINTY_SLIME_JELLY.get());
				tabData.accept(ConcoctionModItems.SWEET_SLIME_JELLY.get());
				tabData.accept(ConcoctionModItems.COD_SUSHI.get());
				tabData.accept(ConcoctionModItems.SALMON_SUSHI.get());
				tabData.accept(ConcoctionModItems.TROPICAL_SUSHI.get());
				tabData.accept(ConcoctionModBlocks.CHOCOLATE_CAKE.get().asItem());
				tabData.accept(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE.get().asItem());
				tabData.accept(ConcoctionModBlocks.CHERRY_CAKE.get().asItem());
				tabData.accept(ConcoctionModBlocks.LINGONBERRY_CAKE.get().asItem());
				tabData.accept(ConcoctionModBlocks.GLOWBERRY_CAKE.get().asItem());
				tabData.accept(ConcoctionModBlocks.CARROT_CAKE.get().asItem());
				tabData.accept(ConcoctionModItems.CAKE_SLICE.get());
				tabData.accept(ConcoctionModItems.CHOCOLATE_CAKE_SLICE.get());
				tabData.accept(ConcoctionModItems.MINT_CHOCOLATE_CAKE_SLICE.get());
				tabData.accept(ConcoctionModItems.CHERRY_CAKE_SLICE.get());
				tabData.accept(ConcoctionModItems.LINGONBERRY_CAKE_SLICE.get());
				tabData.accept(ConcoctionModItems.GLOWBERRY_CAKE_SLICE.get());
				tabData.accept(ConcoctionModItems.CARROT_CAKE_SLICE.get());
				tabData.accept(ConcoctionModItems.PUMPKIN_PIE_SLICE.get());
				tabData.accept(ConcoctionModItems.MINT_COOKIE.get());
				tabData.accept(ConcoctionModItems.CHERRY_COOKIE.get());

//Супы и блюда
				tabData.accept(ConcoctionModItems.VEGETABLE_SOUP.get());
				tabData.accept(ConcoctionModItems.TOMATO_SOUP.get());
				tabData.accept(ConcoctionModItems.CORN_SOUP.get());
				tabData.accept(ConcoctionModItems.ONION_SOUP.get());
				tabData.accept(ConcoctionModItems.FUNGUS_STEW.get());
				tabData.accept(ConcoctionModItems.MEAT_GOULASH.get());
				tabData.accept(ConcoctionModItems.BAMBOO_PORKCHOP_SOUP.get());
				tabData.accept(ConcoctionModItems.FISH_AND_CHIPS.get());
				tabData.accept(ConcoctionModItems.MASHED_POTATOES.get());
				tabData.accept(ConcoctionModItems.COOKED_RICE.get());
				tabData.accept(ConcoctionModItems.BOILED_NOODLES.get());
				tabData.accept(ConcoctionModItems.CHICKEN_CONFIT.get());
				tabData.accept(ConcoctionModItems.MUSHROOM_CREAM_SOUP.get());
				tabData.accept(ConcoctionModItems.PUFFBALL_SOUP.get());
				tabData.accept(ConcoctionModItems.OMURICE.get());
				tabData.accept(ConcoctionModItems.NOODLES_WITH_MEATBALLS.get());
				tabData.accept(ConcoctionModItems.MINT_BREW.get());

//Другое
				tabData.accept(ConcoctionModItems.OBSIDIAN_TEARS_BOTTLE.get());
				tabData.accept(ConcoctionModItems.WEIGHTED_SOULS_BUCKET.get());
				tabData.accept(ConcoctionModItems.SOUL_ICE.get());
//Дикие Растения
				tabData.accept(ConcoctionModBlocks.WILD_CARROT.get().asItem());
				tabData.accept(ConcoctionModItems.WILD_POTATO.get());
				tabData.accept(ConcoctionModItems.WILD_BEETROOT.get());
				tabData.accept(ConcoctionModItems.WILD_COTTON.get());
				tabData.accept(ConcoctionModBlocks.WILD_ONION.get().asItem());
				tabData.accept(ConcoctionModItems.WILD_SPICY_PEPPER.get());
				tabData.accept(ConcoctionModItems.WILD_CABBAGE.get());
				tabData.accept(ConcoctionModItems.WILD_TOMATO.get());
				tabData.accept(ConcoctionModItems.CORN_BLOCK.get());
				tabData.accept(ConcoctionModItems.SPICY_PEPPER_BLOCK.get());
				tabData.accept(ConcoctionModItems.ONION_BLOCK.get());
				tabData.accept(ConcoctionModItems.GREEN_ONION_BLOCK.get());
				tabData.accept(ConcoctionModItems.COTTON_BLOCK.get());
				tabData.accept(ConcoctionModItems.SUNFLOWER_SEED_BLOCK.get());
				tabData.accept(ConcoctionModItems.CABBAGE_BLOCK.get());
				tabData.accept(ConcoctionModItems.CABBAGE_LEAVES_BLOCK.get());
				tabData.accept(ConcoctionModItems.MINT_BALE.get());
				tabData.accept(ConcoctionModItems.CHERRY_BLOCK.get());
				tabData.accept(ConcoctionModItems.TOMATO_BLOCK.get());
				tabData.accept(ConcoctionModItems.PINECONE_BLOCK.get());
				tabData.accept(ConcoctionModItems.RICE_BLOCK.get());
				tabData.accept(ConcoctionModItems.SOAKED_RICE_BLOCK.get());
				tabData.accept(ConcoctionModItems.REAPEPPER_BLOCK.get());
				tabData.accept(ConcoctionModItems.POTATO_BLOCK.get());
				tabData.accept(ConcoctionModItems.CARROT_BLOCK.get());
				tabData.accept(ConcoctionModItems.BEETROOT_BLOCK.get());
				tabData.accept(ConcoctionModItems.SWEET_BERRIES_BLOCK.get());
				tabData.accept(ConcoctionModItems.GLOW_BERRIES_BLOCK.get());
				tabData.accept(ConcoctionModItems.CHORUS_BLOCK.get());

//Функциональные блоки
				tabData.accept(ConcoctionModItems.OAK_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.SPRUCE_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.BIRCH_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.JUNGLE_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.ACACIA_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.DARK_OAK_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.MANGROVE_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.CHERRY_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.BAMBOO_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.CRIMSON_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.WARPED_KITCHEN_CABINET.get());
				tabData.accept(ConcoctionModItems.OVEN.get());
				tabData.accept(ConcoctionModItems.BUTTER_CHURN.get());
				tabData.accept(ConcoctionModItems.SOULLAND.get());

//Cтроительные блоки
				tabData.accept(ConcoctionModItems.ROCK_SALT_BLOCK.get());
				tabData.accept(ConcoctionModItems.SEA_SALT_BLOCK.get());
				tabData.accept(ConcoctionModItems.PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.LIGHT_GRAY_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.GRAY_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.BLACK_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.BROWN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.RED_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.ORANGE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.YELLOW_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.LIME_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.GREEN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.CYAN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.LIGHT_BLUE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.BLUE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.PURPLE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.MAGENTA_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.PINK_PILLOW_BLOCK.get());

				tabData.accept(ConcoctionModItems.SMALL_WHITE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_LIGHT_GRAY_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_GRAY_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_BLACK_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_BROWN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_RED_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_ORANGE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_YELLOW_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_LIME_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_GREEN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_CYAN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_LIGHT_BLUE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_BLUE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_PURPLE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_MAGENTA_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_PINK_PILLOW_BLOCK.get());

				tabData.accept(ConcoctionModItems.WHITE_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.LIGHT_GRAY_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.GRAY_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.BLACK_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.BROWN_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.RED_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.ORANGE_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.YELLOW_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.LIME_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.GREEN_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.CYAN_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.LIGHT_BLUE_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.BLUE_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.PURPLE_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.MAGENTA_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.PINK_WOVEN_CARPET.get());
				tabData.accept(ConcoctionModItems.WANDERING_TRADER_CARPET.get());
//Деревья
//Зачарования

// Получаем зачарование Butchering из реестра
var butcheringEntry = parameters.holders().holder(ConcoctionModEnchantments.BUTCHERING);
if (butcheringEntry != null && butcheringEntry.isPresent()) {
    Enchantment butchering = butcheringEntry.get().value();
    // Получаем максимальный уровень зачарования из его определения
    int maxLevel = butchering.getMaxLevel();
    // Создаем книги для каждого уровня
    for (int level = 1; level <= maxLevel; level++) {
        tabData.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(Holder.direct(butchering), level)));
    }
}

//Инструменты, оружие, броня
				tabData.accept(ConcoctionModItems.OVERGROWN_SHOVEL.get());
				tabData.accept(ConcoctionModItems.OVERGROWN_PICKAXE.get());
				tabData.accept(ConcoctionModItems.OVERGROWN_AXE.get());
				tabData.accept(ConcoctionModItems.OVERGROWN_HOE.get());
				tabData.accept(ConcoctionModItems.OVERGROWN_SWORD.get());

				tabData.accept(ConcoctionModItems.SUNFLOWER_CROWN_HELMET.get());
				tabData.accept(ConcoctionModItems.SUNSTRUCK_SPAWN_EGG.get());


//Особое
				for (Item toAdd : List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW)) {
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.FLAME), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.FLAME_EXTENDED), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.SNOWFLAKE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.SNOWFLAKE_EXTENDED), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.BLOOMING), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.BLOOMING_EXTENDED), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.BLOOMING_BUFFED), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

				}
				tabData.accept(ConcoctionModItems.MUSIC_DISC_HOT_ICE.get());


			}).build());
}
