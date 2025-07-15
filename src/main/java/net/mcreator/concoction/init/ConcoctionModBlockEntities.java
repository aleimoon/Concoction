
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.concoction.block.entity.WarpedKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.SpruceKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.OakKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.MangroveKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.JungleKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.DarkOakKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.CropMintBlockEntity;
import net.mcreator.concoction.block.entity.CrimsonKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.CookingCauldronEntity;
import net.mcreator.concoction.block.entity.CherryKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.ButterChurnEntity;
import net.mcreator.concoction.block.entity.BirchKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.BambooKitchenCabinetBlockEntity;
import net.mcreator.concoction.block.entity.AcaciaKitchenCabinetBlockEntity;
import net.mcreator.concoction.ConcoctionMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ConcoctionMod.MODID);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CROP_MINT = register("crop_mint", ConcoctionModBlocks.CROP_MINT, CropMintBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> OAK_KITCHEN_CABINET = register("oak_kitchen_cabinet", ConcoctionModBlocks.OAK_KITCHEN_CABINET, OakKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> SPRUCE_KITCHEN_CABINET = register("spruce_kitchen_cabinet", ConcoctionModBlocks.SPRUCE_KITCHEN_CABINET, SpruceKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BIRCH_KITCHEN_CABINET = register("birch_kitchen_cabinet", ConcoctionModBlocks.BIRCH_KITCHEN_CABINET, BirchKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ACACIA_KITCHEN_CABINET = register("acacia_kitchen_cabinet", ConcoctionModBlocks.ACACIA_KITCHEN_CABINET, AcaciaKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> JUNGLE_KITCHEN_CABINET = register("jungle_kitchen_cabinet", ConcoctionModBlocks.JUNGLE_KITCHEN_CABINET, JungleKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> DARK_OAK_KITCHEN_CABINET = register("dark_oak_kitchen_cabinet", ConcoctionModBlocks.DARK_OAK_KITCHEN_CABINET, DarkOakKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> MANGROVE_KITCHEN_CABINET = register("mangrove_kitchen_cabinet", ConcoctionModBlocks.MANGROVE_KITCHEN_CABINET, MangroveKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CHERRY_KITCHEN_CABINET = register("cherry_kitchen_cabinet", ConcoctionModBlocks.CHERRY_KITCHEN_CABINET, CherryKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BAMBOO_KITCHEN_CABINET = register("bamboo_kitchen_cabinet", ConcoctionModBlocks.BAMBOO_KITCHEN_CABINET, BambooKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CRIMSON_KITCHEN_CABINET = register("crimson_kitchen_cabinet", ConcoctionModBlocks.CRIMSON_KITCHEN_CABINET, CrimsonKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> WARPED_KITCHEN_CABINET = register("warped_kitchen_cabinet", ConcoctionModBlocks.WARPED_KITCHEN_CABINET, WarpedKitchenCabinetBlockEntity::new);
	// Start of user code block custom block entities
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> COOKING_CAULDRON = register("cooking_cauldron", Blocks.WATER_CAULDRON, CookingCauldronEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BUTTER_CHURN = register("butter_churn", ConcoctionModBlocks.BUTTER_CHURN, ButterChurnEntity::new);

	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, Block block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block).build(null));
	}

	// End of user code block custom block entities
	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CROP_MINT.get(), (blockEntity, side) -> ((CropMintBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, OAK_KITCHEN_CABINET.get(), (blockEntity, side) -> ((OakKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, SPRUCE_KITCHEN_CABINET.get(), (blockEntity, side) -> ((SpruceKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BIRCH_KITCHEN_CABINET.get(), (blockEntity, side) -> ((BirchKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ACACIA_KITCHEN_CABINET.get(), (blockEntity, side) -> ((AcaciaKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, JUNGLE_KITCHEN_CABINET.get(), (blockEntity, side) -> ((JungleKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DARK_OAK_KITCHEN_CABINET.get(), (blockEntity, side) -> ((DarkOakKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MANGROVE_KITCHEN_CABINET.get(), (blockEntity, side) -> ((MangroveKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CHERRY_KITCHEN_CABINET.get(), (blockEntity, side) -> ((CherryKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BAMBOO_KITCHEN_CABINET.get(), (blockEntity, side) -> ((BambooKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CRIMSON_KITCHEN_CABINET.get(), (blockEntity, side) -> ((CrimsonKitchenCabinetBlockEntity) blockEntity).getItemHandler());
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, WARPED_KITCHEN_CABINET.get(), (blockEntity, side) -> ((WarpedKitchenCabinetBlockEntity) blockEntity).getItemHandler());
	}
}
