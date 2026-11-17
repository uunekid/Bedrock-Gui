package com.bedrockgui.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public class BedrockSettingsScreen extends Screen {

    public static boolean allowVanilla = false;

    private static int lastTab = 0;

    private static final String[] TAB_KEYS = {"general", "controls", "video", "audio", "chat", "access"};
    private static final int M = 8;

    private static final class Item {
        final AbstractWidget widget;
        final Component header;
        int y;

        Item(AbstractWidget widget, Component header) {
            this.widget = widget;
            this.header = header;
        }
    }

    private final Screen parent;
    private final Options options;
    private int tab;

    private final List<Item> items = new ArrayList<>();
    private final List<AbstractWidget> tabButtons = new ArrayList<>();

    private int sideLeft, sideRight, panelTop, panelBottom;
    private int viewLeft, viewRight, viewTop, viewBottom;
    private int rowW;
    private int scroll;
    private int contentHeight;

    private AbstractWidget activeWidget;
    private int dragMode;

    public BedrockSettingsScreen(Screen parent) {
        super(Component.translatable("bedrockgui.settings.title"));
        this.parent = parent;
        this.options = Minecraft.getInstance().options;
        this.tab = lastTab;
    }

    @Override
    protected void init() {
        this.items.clear();
        this.tabButtons.clear();
        this.activeWidget = null;
        this.dragMode = 0;

        int sideW = Math.max(96, Math.min(150, this.width / 4));
        this.sideLeft = M;
        this.sideRight = M + sideW;
        this.panelTop = 30;
        this.panelBottom = this.height - M;
        this.viewLeft = this.sideRight + 6;
        this.viewRight = this.width - M;
        this.viewTop = this.panelTop + 2;
        this.viewBottom = this.panelBottom - 2;
        this.rowW = Math.max(100, Math.min(320, this.viewRight - this.viewLeft - 24));

        int doneH = 22;
        int avail = this.panelBottom - this.panelTop - doneH - 20;
        int tabH = Math.max(18, Math.min(24, (avail - (TAB_KEYS.length - 1) * 4) / TAB_KEYS.length));
        int y = this.panelTop + 6;
        for (int i = 0; i < TAB_KEYS.length; i++) {
            final int index = i;
            Button button = Button.builder(Component.translatable("bedrockgui.settings.tab." + TAB_KEYS[i]),
                    b -> this.selectTab(index)).bounds(this.sideLeft + 6, y, sideW - 12, tabH).build();
            this.addRenderableWidget(button);
            this.tabButtons.add(button);
            y += tabH + 4;
        }

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> this.onClose())
                .bounds(this.sideLeft + 6, this.panelBottom - 6 - doneH, sideW - 12, doneH).build());

        this.buildContent();
        this.layout();
    }

    private void selectTab(int index) {
        this.tab = index;
        lastTab = index;
        this.scroll = 0;
        this.buildContent();
        this.layout();
    }

    private void layout() {
        this.scroll = Math.max(0, Math.min(this.scroll, this.maxScroll()));
        int start = this.viewTop + 6 - this.scroll;
        int y = start;
        int x = this.viewLeft + 8;
        for (Item item : this.items) {
            if (item.widget == null) {
                item.y = y + 4;
                y += 18;
            } else {
                item.widget.setX(x);
                item.widget.setY(y);
                y += 24;
            }
        }
        this.contentHeight = (y - start) + 8;
        int max = this.maxScroll();
        if (this.scroll > max) {
            this.scroll = max;
        }
    }

    private int maxScroll() {
        return Math.max(0, this.contentHeight - (this.viewBottom - this.viewTop));
    }

    private void scrollBy(int delta) {
        this.scroll = Math.max(0, Math.min(this.scroll + delta, this.maxScroll()));
        this.layout();
    }

    private void header(String key) {
        this.items.add(new Item(null, Component.translatable("bedrockgui.settings.h." + key)));
    }

    private void opt(OptionInstance<?> option) {
        this.items.add(new Item(option.createButton(this.options, 0, 0, this.rowW), null));
    }

    private void moreSettings() {
        Button button = Button.builder(Component.translatable("bedrockgui.settings.more"), b -> {
            Minecraft mc = Minecraft.getInstance();
            allowVanilla = true;
            try {
                mc.setScreen(new OptionsScreen(this, mc.options));
            } finally {
                allowVanilla = false;
            }
        }).bounds(0, 0, this.rowW, 20).build();
        this.items.add(new Item(button, null));
    }

    private void buildContent() {
        this.items.clear();
        Options o = this.options;
        switch (this.tab) {
            case 0 -> {
                header("game");
                opt(o.autoJump());
                opt(o.attackIndicator());
                opt(o.mainHand());
                opt(o.showSubtitles());
                opt(o.narrator());
                opt(o.reducedDebugInfo());
                opt(o.showAutosaveIndicator());
                opt(o.hideSplashTexts());
                header("other");
                moreSettings();
            }
            case 1 -> {
                header("mouse");
                opt(o.sensitivity());
                opt(o.invertYMouse());
                opt(o.mouseWheelSensitivity());
                opt(o.discreteMouseScroll());
                opt(o.rawMouseInput());
                opt(o.touchscreen());
                header("movement");
                opt(o.toggleCrouch());
                opt(o.toggleSprint());
                opt(o.autoJump());
                header("other");
                moreSettings();
            }
            case 2 -> {
                header("graphics");
                opt(o.graphicsMode());
                opt(o.renderDistance());
                opt(o.simulationDistance());
                opt(o.cloudStatus());
                opt(o.particles());
                opt(o.ambientOcclusion());
                opt(o.biomeBlendRadius());
                opt(o.entityShadows());
                opt(o.mipmapLevels());
                opt(o.entityDistanceScaling());
                header("performance");
                opt(o.framerateLimit());
                opt(o.enableVsync());
                opt(o.fullscreen());
                opt(o.inactivityFpsLimit());
                header("camera");
                opt(o.fov());
                opt(o.gamma());
                opt(o.guiScale());
                opt(o.bobView());
                opt(o.menuBackgroundBlurriness());
            }
            case 3 -> {
                header("volume");
                for (SoundSource source : SoundSource.values()) {
                    opt(o.getSoundSourceOptionInstance(source));
                }
                header("music");
                opt(o.musicFrequency());
                opt(o.showNowPlayingToast());
                opt(o.directionalAudio());
                opt(o.showSubtitles());
            }
            case 4 -> {
                header("chat");
                opt(o.chatVisibility());
                opt(o.chatColors());
                opt(o.chatLinks());
                opt(o.chatLinksPrompt());
                opt(o.autoSuggestions());
                opt(o.hideMatchedNames());
                opt(o.onlyShowSecureChat());
                header("chatwindow");
                opt(o.chatOpacity());
                opt(o.textBackgroundOpacity());
                opt(o.backgroundForChatOnly());
                opt(o.chatScale());
                opt(o.chatWidth());
                opt(o.chatHeightFocused());
                opt(o.chatHeightUnfocused());
                opt(o.chatLineSpacing());
                opt(o.chatDelay());
            }
            default -> {
                header("visual");
                opt(o.highContrast());
                opt(o.highContrastBlockOutline());
                opt(o.screenEffectScale());
                opt(o.fovEffectScale());
                opt(o.darknessEffectScale());
                opt(o.damageTiltStrength());
                opt(o.glintSpeed());
                opt(o.glintStrength());
                opt(o.hideLightningFlash());
                opt(o.panoramaSpeed());
                header("other");
                opt(o.notificationDisplayTime());
                opt(o.narrator());
                opt(o.rotateWithMinecart());
            }
        }
    }

    private static void panel(GuiGraphics g, int x1, int y1, int x2, int y2, int fill) {
        g.fill(x1, y1, x2, y2, 0xFF1E1E1F);
        g.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fill);
        g.fill(x1 + 1, y1 + 1, x2 - 1, y1 + 2, 0xFF606162);
    }

    private static void outline(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 2, color);
        g.fill(x, y + h - 2, x + w, y + h, color);
        g.fill(x, y, x + 2, y + h, color);
        g.fill(x + w - 2, y, x + w, y + h, color);
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(g, mouseX, mouseY, partialTick);
        panel(g, this.sideLeft, this.panelTop, this.sideRight, this.panelBottom, 0xFF353637);
        panel(g, this.viewLeft - 2, this.panelTop, this.viewRight, this.panelBottom, 0xFF48494A);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        g.drawString(this.font, this.title, M + 2, 12, 0xFFFFFFFF);

        if (this.tab >= 0 && this.tab < this.tabButtons.size()) {
            AbstractWidget selected = this.tabButtons.get(this.tab);
            outline(g, selected.getX() - 2, selected.getY() - 2, selected.getWidth() + 4,
                    selected.getHeight() + 4, 0xFF35B72A);
        }

        g.enableScissor(this.viewLeft, this.viewTop, this.viewRight, this.viewBottom);
        for (Item item : this.items) {
            if (item.widget == null) {
                g.drawString(this.font, item.header, this.viewLeft + 8, item.y, 0xFFE8E8E8);
            } else {
                item.widget.render(g, mouseX, mouseY, partialTick);
            }
        }
        g.disableScissor();

        this.renderScrollbar(g);
    }

    private int handleHeight() {
        int trackH = this.viewBottom - this.viewTop;
        return Math.max(20, trackH * trackH / Math.max(trackH, this.contentHeight));
    }

    private void renderScrollbar(GuiGraphics g) {
        int max = this.maxScroll();
        if (max <= 0) {
            return;
        }
        int x1 = this.viewRight - 8;
        int x2 = this.viewRight - 2;
        int trackH = this.viewBottom - this.viewTop;
        int handleH = this.handleHeight();
        int handleY = this.viewTop + (int) ((long) (trackH - handleH) * this.scroll / max);
        g.fill(x1, this.viewTop, x2, this.viewBottom, 0xFF2B2C2D);
        g.fill(x1, handleY, x2, handleY + handleH, 0xFFC6C6C6);
        g.fill(x1, handleY, x2, handleY + 1, 0xFFE6E6E6);
    }

    private void scrollFromBar(double mouseY) {
        int max = this.maxScroll();
        if (max <= 0) {
            return;
        }
        int trackH = this.viewBottom - this.viewTop;
        int handleH = this.handleHeight();
        double fraction = (mouseY - this.viewTop - handleH / 2.0) / Math.max(1, trackH - handleH);
        this.scroll = Math.max(0, Math.min((int) (fraction * max), max));
        this.layout();
    }

    private boolean inView(double x, double y) {
        return x >= this.viewLeft && x < this.viewRight && y >= this.viewTop && y < this.viewBottom;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.inView(mouseX, mouseY)) {
            if (this.maxScroll() > 0 && mouseX >= this.viewRight - 10) {
                this.dragMode = 2;
                this.scrollFromBar(mouseY);
                return true;
            }
            for (Item item : this.items) {
                if (item.widget != null && item.widget.mouseClicked(mouseX, mouseY, button)) {
                    this.activeWidget = item.widget;
                    this.dragMode = 1;
                    return true;
                }
            }
            this.dragMode = 3;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.dragMode == 1 && this.activeWidget != null) {
            return this.activeWidget.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        if (this.dragMode == 2) {
            this.scrollFromBar(mouseY);
            return true;
        }
        if (this.dragMode == 3) {
            this.scrollBy((int) -dragY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean result = false;
        if (this.dragMode == 1 && this.activeWidget != null) {
            result = this.activeWidget.mouseReleased(mouseX, mouseY, button);
        }
        boolean wasDragging = this.dragMode != 0;
        this.activeWidget = null;
        this.dragMode = 0;
        return result || wasDragging || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.inView(mouseX, mouseY)) {
            this.scrollBy((int) (-scrollY * 18));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void onClose() {
        this.options.save();
        Minecraft.getInstance().setScreen(this.parent);
    }

    @Override
    public void removed() {
        this.options.save();
    }
          }
