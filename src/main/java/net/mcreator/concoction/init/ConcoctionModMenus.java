
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.world.inventory.OvenGUIMenu;
import net.mcreator.concoction.world.inventory.KitchenCabinetInterfaceMenu;
import net.mcreator.concoction.world.inventory.BoilingCauldronInterfaceMenu;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, ConcoctionMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<BoilingCauldronInterfaceMenu>> BOILING_CAULDRON_INTERFACE = REGISTRY.register("boiling_cauldron_interface", () -> IMenuTypeExtension.create(BoilingCauldronInterfaceMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<KitchenCabinetInterfaceMenu>> KITCHEN_CABINET_INTERFACE = REGISTRY.register("kitchen_cabinet_interface", () -> IMenuTypeExtension.create(KitchenCabinetInterfaceMenu::new));
	public static final DeferredHolder<MenuType<?>, MenuType<OvenGUIMenu>> OVEN_GUI = REGISTRY.register("oven_gui", () -> IMenuTypeExtension.create(OvenGUIMenu::new));
}
