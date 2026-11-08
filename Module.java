package com.solstice.client.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Base class for every Solstice module. Add a subclass and register it in ModuleManager. */
public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;
    /** Modules sharing a group are mutually exclusive (e.g. "Render Distance 8" vs "Render Distance 16"). */
    public String group;
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name; this.description = description; this.category = category;
    }
    protected static Minecraft mc() { return Minecraft.getInstance(); }

    public Module group(String g) { this.group = g; return this; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean on) {
        if (on == enabled) return;
        if (on && group != null) {
            for (Module m : ModuleManager.all()) if (m != this && m.enabled && group.equals(m.group)) m.setEnabled(false);
        }
        enabled = on;
        if (on) onEnable(); else onDisable();
    }
    public void toggle() { setEnabled(!enabled); }

    protected void onEnable() {}
    protected void onDisable() {}
    /** Called when the game closes: restore anything you changed in the player's options (enabled flag stays). */
    public void onShutdown() {}
    /** Called every client tick while enabled. */
    public void onTick() {}
    /** HUD modules return the text/line height they draw; others return 0. */
    public int renderHud(GuiGraphics g, int x, int y) { return 0; }
}
