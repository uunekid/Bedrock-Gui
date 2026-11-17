package com.bedrockgui.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {

    @Inject(method = "getMessage", at = @At("RETURN"), cancellable = true, require = 0)
    private void bedrockgui$message(CallbackInfoReturnable<Component> cir) {
        Component original = cir.getReturnValue();
        if (original == null) {
            return;
        }
        AbstractWidget self = (AbstractWidget) (Object) this;
        int rgb = self.active ? 0x303030 : 0x606060;
        cir.setReturnValue(original.copy().withStyle(style -> {
            Style s = style.withShadowColor(0);
            return s.getColor() == null ? s.withColor(TextColor.fromRgb(rgb)) : s;
        }));
    }
}
