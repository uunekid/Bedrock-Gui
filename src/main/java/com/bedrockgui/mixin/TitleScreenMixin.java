package com.bedrockgui.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    private static Component bedrockgui$label(String key) {
        return Component.translatable(key).withStyle(style -> style.withColor(TextColor.fromRgb(0x6E6E6E)));
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void bedrockgui$init(CallbackInfo ci) {
        for (GuiEventListener child : this.children()) {
            if (child instanceof AbstractWidget widget
                    && widget.getHeight() == 20
                    && widget.getWidth() >= 98) {
                widget.visible = false;
                widget.active = false;
            }
        }

        Minecraft mc = Minecraft.getInstance();

        int bw = Math.max(120, (int) (this.width * 0.30));
        int x = (this.width - bw) / 2;
        int y = (int) (this.height * 0.46);

        this.addRenderableWidget(Button.builder(bedrockgui$label("bedrockgui.menu.play"),
                b -> mc.setScreen(new SelectWorldScreen(this))).bounds(x, y, bw, 24).build());

        this.addRenderableWidget(Button.builder(bedrockgui$label("bedrockgui.menu.settings"),
                b -> mc.setScreen(new OptionsScreen(this, mc.options))).bounds(x, y + 29, bw, 24).build());

        this.addRenderableWidget(Button.builder(bedrockgui$label("bedrockgui.menu.servers"),
                b -> mc.setScreen(new JoinMultiplayerScreen(this))).bounds(x, y + 58, bw, 28).build());

        this.addRenderableWidget(Button.builder(bedrockgui$label("bedrockgui.menu.quit"),
                b -> mc.stop()).bounds((int) (this.width * 0.012), (int) (this.height * 0.66),
                Math.max(80, (int) (this.width * 0.16)), 24).build());
    }
}
