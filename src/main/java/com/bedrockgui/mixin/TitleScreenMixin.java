package com.bedrockgui.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlayerSkinWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private AbstractWidget bedrockgui$player;

    private static Component bedrockgui$label(String key) {
        return Component.translatable(key).withStyle(style -> style.withColor(TextColor.fromRgb(0x505050)));
    }

    private static void bedrockgui$part(GuiGraphics g, ResourceLocation tex, int x, int y,
                                        int su, int sv, int sw, int sh, int unit) {
        g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, (float) su, (float) sv,
                sw * unit, sh * unit, sw, sh, 64, 64);
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

        this.bedrockgui$player = null;
        try {
            int ph = (int) (this.height * 0.42);
            int pw = ph / 2;
            int px = (int) (this.width * 0.82) - pw / 2;
            int py = (int) (this.height * 0.75) - ph;
            PlayerSkinWidget model = new PlayerSkinWidget(pw, ph, mc.getEntityModels(),
                    () -> mc.getSkinManager().getInsecureSkin(mc.getGameProfile()));
            model.setPosition(px, py);
            this.addRenderableWidget(model);
            this.bedrockgui$player = model;
        } catch (Throwable t) {
            this.bedrockgui$player = null;
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void bedrockgui$renderPlayer(GuiGraphics g, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        int u = Math.max(2, this.height / 80);
        int cx = (int) (this.width * 0.82);
        int top = (int) (this.height * 0.75) - 32 * u;

        if (this.bedrockgui$player == null) {
            PlayerSkin skin = mc.getSkinManager().getInsecureSkin(mc.getGameProfile());
            ResourceLocation tex = skin.texture();

            bedrockgui$part(g, tex, cx - 4 * u, top + 20 * u, 4, 20, 4, 12, u);
            bedrockgui$part(g, tex, cx, top + 20 * u, 20, 52, 4, 12, u);
            bedrockgui$part(g, tex, cx - 4 * u, top + 8 * u, 20, 20, 8, 12, u);
            bedrockgui$part(g, tex, cx - 8 * u, top + 8 * u, 44, 20, 4, 12, u);
            bedrockgui$part(g, tex, cx + 4 * u, top + 8 * u, 36, 52, 4, 12, u);
            bedrockgui$part(g, tex, cx - 4 * u, top, 8, 8, 8, 8, u);

            bedrockgui$part(g, tex, cx - 4 * u, top + 20 * u, 4, 36, 4, 12, u);
            bedrockgui$part(g, tex, cx, top + 20 * u, 4, 52, 4, 12, u);
            bedrockgui$part(g, tex, cx - 4 * u, top + 8 * u, 20, 36, 8, 12, u);
            bedrockgui$part(g, tex, cx - 8 * u, top + 8 * u, 44, 36, 4, 12, u);
            bedrockgui$part(g, tex, cx + 4 * u, top + 8 * u, 52, 52, 4, 12, u);
            bedrockgui$part(g, tex, cx - 4 * u, top, 40, 8, 8, 8, u);
        }

        g.drawCenteredString(mc.font, mc.getUser().getName(), cx, top - 12, 0xFFFFFFFF);
    }
        }
