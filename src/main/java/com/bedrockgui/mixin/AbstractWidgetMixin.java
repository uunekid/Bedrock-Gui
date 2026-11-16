package com.bedrockgui.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {

    @ModifyVariable(
            method = "renderScrollingString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;II)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1,
            require = 0)
    private int bedrockgui$textColor(int color) {
        int rgb = color & 0xFFFFFF;
        int alpha = color & 0xFF000000;
        if (rgb == 0xFFFFFF) {
            return alpha | 0x505050;
        }
        if (rgb == 0xA0A0A0) {
            return alpha | 0x5E5E5E;
        }
        return color;
    }
}
