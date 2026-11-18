package com.bedrockgui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class BedrockToggle extends AbstractWidget {

    private final OptionInstance<Boolean> option;

    public BedrockToggle(OptionInstance<Boolean> option) {
        super(0, 0, 48, 22, Component.empty());
        this.option = option;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.option.set(!this.option.get());
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int x = this.getX();
        int y = this.getY();
        int w = this.getWidth();
        int h = this.getHeight();
        boolean on = this.option.get();

        g.fill(x, y, x + w, y + h, this.isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF1E1E1F);

        int half = (w - 4) / 2;
        int leftX = x + 2;
        int rightX = x + 2 + half;
        int top = y + 2;
        int bottom = y + h - 2;
        int cy = y + h / 2 - 4;

        int handleX = on ? rightX : leftX;
        int slotX = on ? leftX : rightX;

        if (on) {
            g.fill(slotX, top, slotX + half, bottom, 0xFF2E8B20);
            g.fill(slotX, top, slotX + half, top + 1, 0xFF6FD45E);
            g.drawCenteredString(Minecraft.getInstance().font, "I", slotX + half / 2, cy, 0xFFFFFFFF);
        } else {
            g.fill(slotX, top, slotX + half, bottom, 0xFF6E6F70);
            g.drawCenteredString(Minecraft.getInstance().font, "O", slotX + half / 2, cy, 0xFF303030);
        }
        g.fill(handleX, top, handleX + half, bottom, 0xFFC6C6C6);
        g.fill(handleX, top, handleX + half, top + 1, 0xFFEBEBEB);
        g.fill(handleX, bottom - 2, handleX + half, bottom, 0xFF8B8B8B);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}
