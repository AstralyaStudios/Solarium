package net.astralya.solarium.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.astralya.solarium.SolariumMod;
import net.astralya.solarium.screen.renderer.EnergyDisplayTooltipArea;
import net.astralya.solarium.util.MouseUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class SunflowerGeneratorScreen extends AbstractContainerScreen<SunflowerGeneratorMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SolariumMod.MODID,"textures/gui/sunflower_generator/sunflower_generator_gui.png");
    private static final ResourceLocation LIT_PROGRESS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft","container/furnace/lit_progress");
    private EnergyDisplayTooltipArea energyInfoArea;

    public SunflowerGeneratorScreen(SunflowerGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 1000;
        this.titleLabelY = 1000;

        assignEnergyInfoArea();
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if(isMouseAboveArea(mouseX, mouseY, x, y, 156, 11, 8, 64)) {
            guiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(),
                    Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(((width - imageWidth) / 2) + 156,
                ((height - imageHeight) / 2) + 11, menu.blockEntity.getEnergyStorage(null));
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderEnergyAreaTooltip(guiGraphics, mouseX, mouseY, x, y);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        energyInfoArea.render(guiGraphics);
        renderFuelBurning(guiGraphics, x, y);
    }

    private void renderFuelBurning(GuiGraphics guiGraphics, int x, int y) {
        if(this.menu.isBurning()) {
            int l = Mth.ceil(this.menu.getFuelProgress() * 13.0F) + 1;
            guiGraphics.blitSprite(LIT_PROGRESS_TEXTURE, 14, 14, 0, 14 - l,
                    x + 80, y + 18 + 14 - l, 14, l);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public static boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, width, height);
    }
}
