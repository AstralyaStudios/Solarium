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
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Optional;

public class PhotosmelterScreen extends AbstractContainerScreen<PhotosmelterMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SolariumMod.MODID, "textures/gui/photosmelter/photosmelter_gui.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SolariumMod.MODID, "textures/gui/photosmelter/arrow_progress.png");

    private EnergyDisplayTooltipArea energyInfoArea;

    public PhotosmelterScreen(PhotosmelterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 1000;
        this.titleLabelY = 1000;
        assignEnergyInfoArea();
    }

    private void assignEnergyInfoArea() {
        energyInfoArea = new EnergyDisplayTooltipArea(
                ((width - imageWidth) / 2) + 156,
                ((height - imageHeight) / 2) + 11,
                energyMirror()
        );
    }

    private IEnergyStorage energyMirror() {
        return new IEnergyStorage() {
            @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
            @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
            @Override public int getEnergyStored() { return menu.getEnergy(); }
            @Override public int getMaxEnergyStored() { return menu.getCapacity(); }
            @Override public boolean canExtract() { return false; }
            @Override public boolean canReceive() { return false; }
        };
    }

    private void renderEnergyAreaTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        if (isMouseAboveArea(mouseX, mouseY, x, y, 156, 11, 8, 64)) {
            guiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(), Optional.empty(),
                    mouseX - x, mouseY - y);
        }
        ;
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
        renderProgressArrow(guiGraphics, x, y);
        energyInfoArea.render(guiGraphics);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            int w = menu.getScaledArrowProgress();
            if (w > 0) {
                guiGraphics.blit(ARROW_TEXTURE, x + 79, y + 34, 0, 0, w, 16, 24, 16);
            } else if (menu.getProgress() > 0) {
                guiGraphics.blit(ARROW_TEXTURE, x + 79, y + 34, 0, 0, 1, 16, 24, 16);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public static boolean isMouseAboveArea(int mouseX, int mouseY, int x, int y,
                                           int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(mouseX, mouseY, x + offsetX, y + offsetY, width, height);
    }
}