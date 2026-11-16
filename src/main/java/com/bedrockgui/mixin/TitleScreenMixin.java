
package com.bedrockgui.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
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

        Minecraft mc = Minecraft.getInstance();

        // Hide original Java menu buttons
        this.children().forEach(child -> {
            if (child instanceof Button button) {
                button.visible = false;
                button.active = false;
            }
        });

        // Bedrock-style menu dimensions
        int buttonWidth = 200;
        int buttonHeight = 28;

        int x = this.width / 2 - buttonWidth / 2;
        int y = this.height / 4 + 40;

        // Play
        this.addRenderableWidget(
            Button.builder(
                Component.translatable("bedrockgui.menu.play"),
                b -> mc.setScreen(new SelectWorldScreen(this))
            ).bounds(x, y, buttonWidth, buttonHeight).build()
        );

        // Servers
        this.addRenderableWidget(
            Button.builder(
                Component.translatable("bedrockgui.menu.servers"),
                b -> mc.setScreen(new JoinMultiplayerScreen(this))
            ).bounds(x, y + 34, buttonWidth, buttonHeight).build()
        );

        // Settings
        this.addRenderableWidget(
            Button.builder(
                Component.translatable("bedrockgui.menu.settings"),
                b -> mc.setScreen(new OptionsScreen(this, mc.options))
            ).bounds(x, y + 68, buttonWidth, buttonHeight).build()
        );

        // Quit
        this.addRenderableWidget(
            Button.builder(
                Component.translatable("bedrockgui.menu.quit"),
                b -> mc.stop()
            ).bounds(x, y + 102, buttonWidth, buttonHeight).build()
        );
    }
}
