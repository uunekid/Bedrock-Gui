package com.bedrockgui.mixin;

import com.bedrockgui.client.BedrockSettingsScreen;
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

        if (BedrockSettingsScreen.VALUE_ONLY.contains(self)) {
            String raw = original.getString();
            int idx = raw.indexOf(": ");
            original = Component.literal(idx >= 0 ? raw.substring(idx + 2) : raw);
        }

        final int rgb = !self.active ? 0x606060 : (self.isHoveredOrFocused() ? 0xFFFFFF : 0x303030);
        cir.setReturnValue(original.copy().withStyle(style -> {
            Style s = style.withShadowColor(0);
            return s.getColor() == null ? s.withColor(TextColor.fromRgb(rgb)) : s;
        }));
    }
}
