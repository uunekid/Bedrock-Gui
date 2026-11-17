package com.bedrockgui.mixin;

import com.bedrockgui.client.BedrockPlayerWidget;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
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
        return Component.translatable(key).withStyle(style -> style.withColor(TextColor.fromRgb(0x303030)));
    }

    private static void bedrockgui$part(GuiGraphics g, ResourceLocation tex, int x, int y,
                                        int su, int sv, int sw, int sh, int unit) {
        g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, (float) su, (float) sv,
                sw * unit, sh * unit, sw, sh, 64, 64);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void bedrockgui$init(CallbackInfo ci) {
        List<AbstractWidget> icons = new ArrayList<>();
        for (GuiEventListener child : this.children()) {
            if (child instanceof AbstractWidget widget) {
                if (widget.getHeight() == 20 && widget.getWidth() >= 98) {
                    widget.visible = false;
                    widget.active = false;
                } else if (widget.getHeight() == 20 && widget.getWidth() == 20) {
                    icons.add(widget);
                }
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

        int cx = (int) (this.width * 0.82);
        int feet = (int) (this.height * 0.75);
        int qw = Math.max(80, (int) (this.width * 0.16));
        int qy = feet + 4;

        this.addRenderableWidget(Button.builder(bedrockgui$label("bedrockgui.menu.quit"),
                b -> mc.stop()).bounds(cx - qw / 2, qy, qw, 22).build());

        icons.sort(Comparator.comparingInt(AbstractWidget::getX));
        int iy = qy + 22 + 3;
        for (int i = 0; i < icons.size(); i++) {
            icons.get(i).setPosition(cx - 22 + i * 24, iy);
        }

        this.bedrockgui$player = null;
        try {
            int ph = (int) (this.height * 0.30);
            int pw = ph / 2;
            BedrockPlayerWidget model = new BedrockPlayerWidget(pw, ph, mc.getEntityModels(),
                    () -> mc.getSkinManager().getInsecureSkin(mc.getGameProfile()));
            model.setPosition(cx - pw / 2, feet - ph);
            this.addRenderableWidget(model);
            this.bedrockgui$player = model;
        } catch (Throwable t) {
            t.printStackTrace();
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

        int labelY = (this.bedrockgui$player != null ? this.bedrockgui$player.getY() : top) - 12;
        g.drawCenteredString(mc.font, mc.getUser().getName(), cx, labelY, 0xFFFFFFFF);
    }
}
