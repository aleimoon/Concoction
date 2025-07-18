package net.mcreator.concoction.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.concoction.world.inventory.BoilingCauldronInterfaceMenu;
import net.mcreator.concoction.world.inventory.OmenInterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class OmenInterfaceScreen extends AbstractContainerScreen<OmenInterfaceMenu> {

    private final static HashMap<String, Object> guistate = OmenInterfaceMenu.guistate;
    private final Level world;
    private final int x, y, z;
    private final Player entity;

    public OmenInterfaceScreen(OmenInterfaceMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;

        // Смещаем инвентарь игрока вниз
        this.inventoryLabelY = 74;
    }

    private static final ResourceLocation texture = ResourceLocation.parse("concoction:textures/gui/hud/cooking_cauldron_gui_playerside.png");

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x33000000, 0x33000000);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        //RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();


        // Отрисовка основного фона
        guiGraphics.blit(texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Отрисовка полосы прогресса, если что-то готовится
        if (menu.isCooking()) {
            int progress = menu.getProgress();
            int maxProgress = menu.getMaxProgress();
            int progressSize = 44 * progress / maxProgress;
            guiGraphics.blit(texture, this.leftPos + 65, this.topPos + 34, 176, 0, progressSize, 24, 256, 256);
        }

        // Отрисовка индикатора LIT (огня), если котел разогрет
        if (menu.isLit()) {
            guiGraphics.blit(texture, this.leftPos + 79, this.topPos + 61, 176, 24, 14, 14, 256, 256);
        }

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
        int titleX = (this.imageWidth - titleWidth) / 2;
        guiGraphics.drawString(this.font, this.title, titleX, 6, 4210752, false);
        // Рисуем заголовок
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 8, 72, 4210752, false);
    }

    @Override
    public void init() {
        super.init();
    }

}
