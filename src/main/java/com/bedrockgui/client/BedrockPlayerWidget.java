package com.bedrockgui.client;

import java.util.function.Supplier;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.resources.PlayerSkin;

public class BedrockPlayerWidget extends PlayerSkinWidget {

    public BedrockPlayerWidget(int width, int height, EntityModelSet models, Supplier<PlayerSkin> skin) {
        super(width, height, models, skin);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        super.onDrag(mouseX, mouseY, dragX, 0.0);
    }
}
