package com.solstice.client.ui;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.Component;
import java.util.*;

/**
 * The Solstice hub: one screen reachable from the title screen, pause menu and options.
 * Left rail = categories + Settings, right = module list with toggle switches.
 */
public class SolsticeScreen extends Screen {
    // Palette
    private static final int AMBER = 0xFFF5A623, AMBER_DIM = 0xFF9A6A18;
    private static final int PANEL = 0xF0121620, RAIL = 0xF00C0F16, ROW = 0xFF1A1F2B, ROW_HOVER = 0xFF232A39;
    private static final int TEXT = 0xFFEAF1FF, MUTED = 0xFF8B97AD, OFF = 0xFF3A4152, BORDER = 0xFF2A3142;

    private enum Tab {
        HUD("HUD", Category.HUD), PVP("PvP", Category.PVP), VISUAL("Visual", Category.VISUAL),
        UTILITY("Utility", Category.UTILITY), PERFORMANCE("Performance", Category.PERFORMANCE),
        ITEMS("Items", Category.ITEMS), EFFECTS("Effects", Category.EFFECTS), AUDIO("Audio", Category.AUDIO), SETTINGS("Settings", null);
        final String label; final Category category;
        Tab(String label, Category category) { this.label = label; this.category = category; }
    }

    private final Screen parent;
    private Tab tab = Tab.HUD;
    private int scroll;
    private int px, py, pw, ph;           // panel bounds
    private static final int RAIL_W = 86, HEADER_H = 34, ROW_H = 28, ROW_GAP = 4;
    private int tabH = 19, tabGap = 2;    // shrunk in init() when the window is short
    private EditBox search;

    public SolsticeScreen(Screen parent) { super(Component.literal("Solstice")); this.parent = parent; }

    @Override protected void init() {
        pw = Math.min(width - 24, 480);
        ph = Math.min(height - 24, 300);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        scroll = 0;
        int step = Math.min(24, (ph - HEADER_H - 34) / Tab.values().length);
        tabGap = 2; tabH = Math.max(12, step - tabGap);
        String old = search == null ? "" : search.getValue();
        search = new EditBox(font, px + 100, py + 8, Math.max(60, pw - 100 - 70), 18, Component.literal("Search"));
        search.setHint(Component.literal("Search modules..."));
        search.setMaxLength(40);
        search.setValue(old);
        search.setResponder(v -> scroll = 0);
        addRenderableWidget(search);
    }

    private boolean searching() { return search != null && !search.getValue().isBlank(); }
    private boolean showSettings() { return tab == Tab.SETTINGS && !searching(); }

    // ---- data ----
    private List<Module> modules() {
        List<Module> out = new ArrayList<>();
        if (searching()) {
            String q = search.getValue().trim().toLowerCase(Locale.ROOT);
            for (Module m : ModuleManager.all())
                if (m.name.toLowerCase(Locale.ROOT).contains(q) || m.description.toLowerCase(Locale.ROOT).contains(q)) out.add(m);
            return out;
        }
        if (tab.category == null) return out;
        for (Module m : ModuleManager.all()) if (m.category == tab.category) out.add(m);
        return out;
    }
    private int listLeft() { return px + RAIL_W + 10; }
    private int listRight() { return px + pw - 10; }
    private int listTop() { return py + HEADER_H + 8; }
    private int listBottom() { return py + ph - 10; }
    private int rowCount() { return showSettings() ? 3 : modules().size(); }
    private int maxScroll() { return Math.max(0, rowCount() * (ROW_H + ROW_GAP) - (listBottom() - listTop())); }

    // ---- render ----
    /** Vanilla 1.21 blurs the menu background; use a plain dim so the world and text stay sharp. */
    @Override public void renderBackground(GuiGraphics g, int mx, int my, float delta) {
        g.fillGradient(0, 0, width, height, 0xA0000000, 0xB8000000);
    }

    @Override public void render(GuiGraphics g, int mx, int my, float delta) {
        renderBackground(g, mx, my, delta);
        g.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, BORDER);
        g.fill(px, py, px + pw, py + ph, PANEL);
        g.fill(px, py, px + RAIL_W, py + ph, RAIL);
        g.fill(px, py + HEADER_H, px + pw, py + HEADER_H + 1, BORDER);
        g.fill(px, py, px + pw, py + 2, AMBER);

        drawSun(g, px + 12, py + 11);
        g.drawString(font, Component.literal("SOLSTICE").withStyle(ChatFormatting.BOLD), px + 32, py + 13, TEXT, false);
        g.drawString(font, "v" + SolsticeInfo.version(), px + pw - 10 - font.width("v" + SolsticeInfo.version()), py + 14, MUTED, false);

        // category rail
        int ty = py + HEADER_H + 10;
        for (Tab t : Tab.values()) {
            boolean sel = t == tab && !searching(), hov = inside(mx, my, px + 6, ty, RAIL_W - 12, tabH);
            if (sel) { g.fill(px + 6, ty, px + RAIL_W - 6, ty + tabH, ROW_HOVER); g.fill(px + 6, ty, px + 8, ty + tabH, AMBER); }
            else if (hov) g.fill(px + 6, ty, px + RAIL_W - 6, ty + tabH, ROW);
            int textY = ty + (tabH - 8) / 2;
            g.drawString(font, t.label, px + 16, textY, sel ? TEXT : MUTED, false);
            if (t.category != null) {
                String n = String.valueOf(enabledIn(t.category));
                g.drawString(font, n, px + RAIL_W - 14 - font.width(n), textY, sel ? AMBER : OFF, false);
            }
            ty += tabH + tabGap;
        }
        g.drawString(font, ModuleManager.enabledCount() + " / " + ModuleManager.all().size() + " on", px + 10, py + ph - 14, MUTED, false);

        // list
        scroll = Math.max(0, Math.min(scroll, maxScroll()));
        g.enableScissor(listLeft(), listTop(), listRight(), listBottom());
        if (showSettings()) renderSettings(g, mx, my); else renderModules(g, mx, my);
        g.disableScissor();
        if (maxScroll() > 0) {
            int trackH = listBottom() - listTop();
            int barH = Math.max(14, trackH * trackH / (trackH + maxScroll()));
            int barY = listTop() + (int) ((long) (trackH - barH) * scroll / maxScroll());
            g.fill(listRight() + 3, barY, listRight() + 5, barY + barH, AMBER_DIM);
        }
        super.render(g, mx, my, delta);
    }

    private int enabledIn(Category c) {
        int n = 0;
        for (Module m : ModuleManager.all()) if (m.category == c && m.isEnabled()) n++;
        return n;
    }

    private void renderModules(GuiGraphics g, int mx, int my) {
        int y = listTop() - scroll;
        for (Module m : modules()) {
            drawRow(g, y, m.name, m.description, m.isEnabled(), true, mx, my);
            if (searching()) {
                String c = m.category.name();
                g.drawString(font, c, listRight() - 34 - 10 - font.width(c), y + 6, AMBER_DIM, false);
            }
            y += ROW_H + ROW_GAP;
        }
        if (modules().isEmpty()) g.drawString(font, searching() ? "No modules match." : "Nothing here yet.", listLeft() + 4, listTop() + 4, MUTED, false);
    }

    private void renderSettings(GuiGraphics g, int mx, int my) {
        int y = listTop() - scroll;
        drawRow(g, y, "Menu buttons", "Show a Solstice button on the title, pause and options screens.", SolsticeConfig.menuButtons, true, mx, my);
        y += ROW_H + ROW_GAP;
        drawRow(g, y, "Controls", "Open key bindings (menu, zoom and freelook keys).", false, false, mx, my);
        y += ROW_H + ROW_GAP;
        drawRow(g, y, "Disable all modules", "Turn every Solstice module off.", false, false, mx, my);
    }

    private void drawRow(GuiGraphics g, int y, String name, String desc, boolean on, boolean toggle, int mx, int my) {
        int x = listLeft(), w = listRight() - x;
        boolean hov = inside(mx, my, x, y, w, ROW_H) && my >= listTop() && my <= listBottom();
        g.fill(x, y, x + w, y + ROW_H, hov ? ROW_HOVER : ROW);
        if (on && toggle) g.fill(x, y, x + 2, y + ROW_H, AMBER);
        g.drawString(font, name, x + 9, y + 6, TEXT, false);
        g.drawString(font, font.plainSubstrByWidth(desc, w - 70), x + 9, y + 17, MUTED, false);
        if (toggle) {
            int sx = x + w - 34, sy = y + 8;
            g.fill(sx, sy, sx + 26, sy + 12, on ? AMBER : OFF);
            int kx = on ? sx + 15 : sx + 1;
            g.fill(kx, sy + 1, kx + 10, sy + 11, 0xFFFFFFFF);
        } else {
            String go = "Open";
            int bw = font.width(go) + 14, bx = x + w - bw - 8;
            g.fill(bx, y + 6, bx + bw, y + 22, hov ? AMBER_DIM : OFF);
            g.drawString(font, go, bx + 7, y + 11, TEXT, false);
        }
    }

    /** Tiny pixel-art sun used as the Solstice mark. */
    static void drawSun(GuiGraphics g, int x, int y) {
        g.fill(x + 4, y + 4, x + 12, y + 12, AMBER);
        g.fill(x + 7, y, x + 9, y + 3, AMBER);
        g.fill(x + 7, y + 13, x + 9, y + 16, AMBER);
        g.fill(x, y + 7, x + 3, y + 9, AMBER);
        g.fill(x + 13, y + 7, x + 16, y + 9, AMBER);
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    // ---- input ----
    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) return super.mouseClicked(mx, my, button);
        if (search.isMouseOver(mx, my)) return super.mouseClicked(mx, my, button);
        search.setFocused(false);
        int ty = py + HEADER_H + 10;
        for (Tab t : Tab.values()) {
            if (inside(mx, my, px + 6, ty, RAIL_W - 12, tabH)) { tab = t; scroll = 0; search.setValue(""); return true; }
            ty += tabH + tabGap;
        }
        if (my < listTop() || my > listBottom()) return super.mouseClicked(mx, my, button);
        int idx = (int) ((my - listTop() + scroll) / (ROW_H + ROW_GAP));
        int within = (int) ((my - listTop() + scroll) % (ROW_H + ROW_GAP));
        if (within >= ROW_H || mx < listLeft() || mx > listRight() || idx < 0 || idx >= rowCount())
            return super.mouseClicked(mx, my, button);
        if (showSettings()) {
            switch (idx) {
                case 0 -> { SolsticeConfig.menuButtons = !SolsticeConfig.menuButtons; SolsticeConfig.save(); }
                case 1 -> { ModuleManager.save(); minecraft.setScreen(new KeyBindsScreen(this, minecraft.options)); }
                case 2 -> { for (Module m : ModuleManager.all()) m.setEnabled(false); ModuleManager.save(); }
            }
        } else {
            modules().get(idx).toggle();
            ModuleManager.save();
        }
        return true;
    }

    @Override public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        scroll -= (int) (scrollY * 24);
        scroll = Math.max(0, Math.min(scroll, maxScroll()));
        return true;
    }

    @Override public boolean keyPressed(int key, int scan, int mods) {
        if (key == 256) { onClose(); return true; } // ESC
        return super.keyPressed(key, scan, mods);
    }

    @Override public boolean isPauseScreen() { return false; }
    @Override public void onClose() { ModuleManager.save(); SolsticeConfig.save(); minecraft.setScreen(parent); }
}
