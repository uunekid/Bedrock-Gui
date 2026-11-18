package com.bedrockgui.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class BedrockSettingsScreen extends Screen {

    public static boolean allowVanilla = false;

    public static final Set<AbstractWidget> VALUE_ONLY = Collections.newSetFromMap(new WeakHashMap<>());

    private static int lastTab = 0;

    private static final String[] TAB_KEYS = {"general", "controls", "video", "audio", "chat", "access"};

    private static final String[] SIDE_KEYS = {"controls", "controls", "general", "general", "video", "audio", "chat", "access"};
    private static final int[] SIDE_TABS = {-1, 1, -1, 0, 2, 3, 4, 5};

    private static final int KIND_HEADER = 0;
    private static final int KIND_TOGGLE = 1;
    private static final int KIND_SLIDER = 2;
    private static final int KIND_CYCLE = 3;
    private static final int KIND_ACTION = 4;

    private static final int HEADER_ROW_H = 18;

    private static final class Item {
        final int kind;
        final String label;
        final AbstractWidget widget;
        int y;
        int h;

        Item(int kind, String label, AbstractWidget widget) {
            this.kind = kind;
            this.label = label;
            this.widget = widget;
            this.h = switch (kind) {
                case KIND_HEADER -> 28;
                case KIND_SLIDER -> 50;
                default -> 34;
            };
        }
    }

    private final Screen parent;
    private final Options options;
    private int tab;

    private final List<Item> items = new ArrayList<>();

    private int headerH, sideRight, tabRowH;
    private int viewLeft, viewRight, viewTop, viewBottom;
    private int contentX, contentRight, rowW;
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
        this.activeWidget = null;
        this.dragMode = 0;

        this.headerH = 24;
        this.sideRight = Math.max(110, (int) (this.width * 0.30));
        this.viewLeft = this.sideRight + 1;
        this.viewRight = this.width;
        this.viewTop = this.headerH + 2;
        this.viewBottom = this.height;
        this.contentX = this.viewLeft + 14;
        this.contentRight = this.viewRight - 14;
        this.rowW = Math.max(100, this.contentRight - this.contentX);

        int groups = 0;
        int tabs = 0;
        for (int t : SIDE_TABS) {
            if (t < 0) {
                groups++;
            } else {
                tabs++;
            }
        }
        int avail = this.height - this.headerH - 8 - groups * HEADER_ROW_H;
        this.tabRowH = Math.max(18, Math.min(28, avail / Math.max(1, tabs)));

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
        int start = this.viewTop - this.scroll;
        int y = start;
        for (Item item : this.items) {
            item.y = y;
            if (item.widget != null) {
                switch (item.kind) {
                    case KIND_TOGGLE -> {
                        item.widget.setX(this.contentRight - item.widget.getWidth());
                        item.widget.setY(y + (item.h - item.widget.getHeight()) / 2);
                    }
                    case KIND_SLIDER -> {
                        item.widget.setX(this.contentX);
                        item.widget.setY(y + item.h - 26);
                    }
                    case KIND_CYCLE -> {
                        item.widget.setX(this.contentRight - item.widget.getWidth());
                        item.widget.setY(y + (item.h - item.widget.getHeight()) / 2);
                    }
                    default -> {
                        item.widget.setX(this.contentX);
                        item.widget.setY(y + (item.h - item.widget.getHeight()) / 2);
                    }
                }
            }
            y += item.h;
        }
        this.contentHeight = y - start + 8;
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
        String text = Component.translatable("bedrockgui.settings.h." + key).getString().toUpperCase();
        this.items.add(new Item(KIND_HEADER, text, null));
    }

    private void opt(OptionInstance<?> option) {
        Object current = option.get();
        AbstractWidget probe = option.createButton(this.options, 0, 0, this.rowW);
        String raw = probe.getMessage().getString();
        int idx = raw.indexOf(": ");
        String caption = idx >= 0 ? raw.substring(0, idx) : raw;

        if (current instanceof Boolean) {
            @SuppressWarnings("unchecked")
            OptionInstance<Boolean> bool = (OptionInstance<Boolean>) option;
            this.items.add(new Item(KIND_TOGGLE, caption, new BedrockToggle(bool)));
        } else if (probe instanceof AbstractSliderButton) {
            VALUE_ONLY.add(probe);
            this.items.add(new Item(KIND_SLIDER, caption, probe));
        } else {
            probe.setWidth(110);
            VALUE_ONLY.add(probe);
            this.items.add(new Item(KIND_CYCLE, caption, probe));
        }
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
        this.items.add(new Item(KIND_ACTION, "", button));
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

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(g, mouseX, mouseY, partialTick);
        g.fill(0, this.headerH, this.sideRight, this.height, 0xFF313233);
        g.fill(this.sideRight, this.headerH, this.width, this.height, 0xFF48494A);
        g.fill(this.sideRight, this.headerH, this.sideRight + 1, this.height, 0xFF1E1E1F);
        g.fill(0, 0, this.width, this.headerH, 0xFFE6E6E8);
        g.fill(0, this.headerH, this.width, this.headerH + 2, 0xFF8E8F90);
    }

    private void renderSidebar(GuiGraphics g, int mouseX, int mouseY) {
        int y = this.headerH + 4;
        for (int i = 0; i < SIDE_TABS.length; i++) {
            int tabIndex = SIDE_TABS[i];
            Component name = Component.translatable("bedrockgui.settings.tab." + SIDE_KEYS[i]);
            if (tabIndex < 0) {
                g.drawString(this.font, name, 10, y + 5, 0xFFA8A8A8, false);
                g.fill(0, y + HEADER_ROW_H - 2, this.sideRight, y + HEADER_ROW_H - 1, 0xFF1E1E1F);
                y += HEADER_ROW_H;
            } else {
                boolean hovered = mouseX < this.sideRight && mouseY >= y && mouseY < y + this.tabRowH;
                if (tabIndex == this.tab) {
                    g.fill(0, y, this.sideRight, y + this.tabRowH, 0xFF4A4B4C);
                } else if (hovered) {
                    g.fill(0, y, this.sideRight, y + this.tabRowH, 0xFF3C3D3E);
                }
                ResourceLocation icon = ResourceLocation.fromNamespaceAndPath("bedrockgui",
                        "textures/gui/icon_" + TAB_KEYS[tabIndex] + ".png");
                g.blit(RenderPipelines.GUI_TEXTURED, icon, 8, y + (this.tabRowH - 16) / 2, 0.0F, 0.0F, 16, 16, 16, 16);
                g.drawString(this.font, name, 30, y + (this.tabRowH - 8) / 2, 0xFFFFFFFF, false);
                y += this.tabRowH;
            }
        }
    }

    private int tabAt(double mouseY) {
        int y = this.headerH + 4;
        for (int i = 0; i < SIDE_TABS.length; i++) {
            if (SIDE_TABS[i] < 0) {
                y += HEADER_ROW_H;
            } else {
                if (mouseY >= y && mouseY < y + this.tabRowH) {
                    return SIDE_TABS[i];
                }
                y += this.tabRowH;
            }
        }
        return -1;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        g.drawString(this.font, Component.literal("<").withStyle(s -> s.withBold(true)), 12, (this.headerH - 8) / 2,
                0xFF303030, false);
        Component title = Component.literal(this.title.getString().toUpperCase()).withStyle(s -> s.withBold(true));
        int titleW = this.font.width(title);
        g.drawString(this.font, title, (this.width - titleW) / 2, (this.headerH - 8) / 2, 0xFF303030, false);

        this.renderSidebar(g, mouseX, mouseY);

        g.enableScissor(this.viewLeft, this.viewTop, this.viewRight, this.viewBottom);
        for (Item item : this.items) {
            if (item.kind == KIND_HEADER) {
                Component text = Component.literal(item.label).withStyle(s -> s.withBold(true));
                g.drawString(this.font, text, this.contentX, item.y + 12, 0xFFFFFFFF, false);
            } else if (!item.label.isEmpty()) {
                int labelY = item.kind == KIND_SLIDER ? item.y + 8 : item.y + (item.h - 8) / 2;
                g.drawString(this.font, item.label, this.contentX, labelY, 0xFFFFFFFF, false);
            }
            if (item.widget != null) {
                item.widget.render(g, mouseX, mouseY, partialTick);
            }
            g.fill(this.contentX, item.y + item.h - 1, this.contentRight, item.y + item.h, 0xFF3A3B3C);
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
        int x1 = this.viewRight - 6;
        int x2 = this.viewRight - 2;
        int trackH = this.viewBottom - this.viewTop;
        int handleH = this.handleHeight();
        int handleY = this.viewTop + (int) ((long) (trackH - handleH) * this.scroll / max);
        g.fill(x1, handleY, x2, handleY + handleH, 0xFFE8E8E8);
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
        if (button == 0) {
            if (mouseY < this.headerH) {
                if (mouseX < 40) {
                    this.onClose();
                }
                return true;
            }
            if (mouseX < this.sideRight) {
                int t = this.tabAt(mouseY);
                if (t >= 0) {
                    this.selectTab(t);
                }
                return true;
            }
            if (this.inView(mouseX, mouseY)) {
                if (this.maxScroll() > 0 && mouseX >= this.viewRight - 12) {
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
