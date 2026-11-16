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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
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
        int x = this.width / 2 - 100;
        int y = this.height / 4 + 48;

        this.addRenderableWidget(Button.builder(Component.translatable("bedrockgui.menu.play"),
                b -> mc.setScreen(new SelectWorldScreen(this))).bounds(x, y, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("bedrockgui.menu.servers"),
                b -> mc.setScreen(new JoinMultiplayerScreen(this))).bounds(x, y + 24, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("bedrockgui.menu.settings"),
                b -> mc.setScreen(new OptionsScreen(this, mc.options))).bounds(x, y + 48, 200, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("bedrockgui.menu.quit"),
                b -> mc.stop()).bounds(x, y + 72, 200, 20).build());
    }
                                                }
