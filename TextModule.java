package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import net.minecraft.client.gui.GuiGraphics;
import java.util.function.Supplier;

/** One line of HUD text produced by a supplier. Return null/empty from the supplier to hide the line. */
public class TextModule extends Module {
    private final Supplier<String> text;
    public TextModule(String name, String desc, Category cat, Supplier<String> text) {
        super(name, desc, cat); this.text = text;
    }
    @Override public int renderHud(GuiGraphics g, int x, int y) {
        String s;
        try { s = text.get(); } catch (Exception e) { return 0; }
        if (s == null || s.isEmpty()) return 0;
        g.drawString(mc().font, s, x, y, 0xFFFFFFFF, true);
        return 11;
    }
}
