package net.mcreator.concoction.client.gui;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.concoction.world.inventory.OvenGUIMenu;

import java.util.HashMap;

import com.mojang.blaze3d.systems.RenderSystem;

public class OvenGUIScreen extends AbstractContainerScreen<OvenGUIMenu> {
	private final static HashMap<String, Object> guistate = OvenGUIMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;

	public OvenGUIScreen(OvenGUIMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.world = container.world;
		this.x = container.x;
		this.y = container.y;
		this.z = container.z;
		this.entity = container.entity;
		this.imageWidth = 176;
		this.imageHeight = 166;
		this.inventoryLabelY = 74;
	}

	private static final ResourceLocation texture = ResourceLocation.parse("concoction:textures/gui/hud/oven_gui_playerside.png");

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		guiGraphics.fillGradient(0, 0, this.width, this.height, 0x33000000, 0x33000000);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

		// Прогресс-бар готовки
		if (menu.isCooking() && menu.getMaxProgress() > 0) {
			int progress = menu.getProgress();
			int maxProgress = menu.getMaxProgress();
			// Убеждаемся, что прогресс не превышает максимум
			progress = Math.min(progress, maxProgress);
			int progressSize = maxProgress > 0 ? (24 * progress) / maxProgress : 0;
			if (progressSize > 0) {
				guiGraphics.blit(texture, this.leftPos + 103, this.topPos + 34, 176, 14, progressSize, 16, 256, 256);
			}
		}

		// Иконка огонька (горит когда печь нагрета)
		if (menu.isLit()) {
			guiGraphics.blit(texture, this.leftPos + 61, this.topPos + 60, 176, 0, 14, 14, 256, 256);
		}

		// Показываем иконки слотов только когда они пустые
		// Слот бутылочки (19, 33) - слот 36
		if (!menu.getSlot(36).getItem().isEmpty()) {
			guiGraphics.blit(texture, this.leftPos + 18, this.topPos + 32, 176, 30, 16, 16, 256, 256);
		}
		
//		// Слот миски (104, 13) - слот 43
//		if (menu.getSlot(43).getItem().isEmpty()) {
//			guiGraphics.blit(texture, this.leftPos + 104, this.topPos + 13, 176, 46, 16, 16, 256, 256);
//		}

		RenderSystem.disableBlend();
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if (key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		return super.keyPressed(key, b, c);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		int titleWidth = this.font.width(this.title.getString());
		int titleX = 8;
		guiGraphics.drawString(this.font, this.title, titleX, 6, 4210752, false);
		guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 8, 72, 4210752, false);
	}

	@Override 
	public void init() {
		super.init();
	}
}
