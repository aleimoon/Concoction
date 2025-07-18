
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.concoction.client.gui.OvenGUIScreen;
import net.mcreator.concoction.client.gui.KitchenCabinetInterfaceScreen;
import net.mcreator.concoction.client.gui.BoilingCauldronInterfaceScreen;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConcoctionModScreens {
	@SubscribeEvent
	public static void clientLoad(RegisterMenuScreensEvent event) {
		event.register(ConcoctionModMenus.BOILING_CAULDRON_INTERFACE.get(), BoilingCauldronInterfaceScreen::new);
		event.register(ConcoctionModMenus.KITCHEN_CABINET_INTERFACE.get(), KitchenCabinetInterfaceScreen::new);
		event.register(ConcoctionModMenus.OVEN_GUI.get(), OvenGUIScreen::new);
	}
}
