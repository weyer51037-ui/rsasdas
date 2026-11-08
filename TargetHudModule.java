package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import com.solstice.client.stats.PvpStats;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

/** Name and health bar of whoever you last hit, for a few seconds. */
public class TargetHudModule extends Module {
    public TargetHudModule() { super("Target HUD", "Shows the name and health of whoever you just hit.", Category.PVP); }
    @Override public int renderHud(GuiGraphics g, int x, int y) {
        var t = PvpStats.target;
        if (t == null || t.isRemoved() || System.currentTimeMillis() - PvpStats.targetTime > 4000) return 0;
        float hp = t.getHealth(), max = Math.max(1f, t.getMaxHealth());
        g.fill(x, y, x + 110, y + 32, 0x88000000);
        g.drawString(mc().font, mc().font.plainSubstrByWidth(t.getName().getString(), 100), x + 5, y + 4, 0xFFFFFFFF, true);
        g.fill(x + 5, y + 18, x + 105, y + 22, 0xFF333333);
        float f = Mth.clamp(hp / max, 0f, 1f);
        int col = f > 0.5f ? 0xFF55FF55 : f > 0.25f ? 0xFFFFAA00 : 0xFFFF5555;
        g.fill(x + 5, y + 18, x + 5 + (int) (100 * f), y + 22, col);
        g.drawString(mc().font, String.format("%.1f / %.0f", hp, max), x + 5, y + 22, 0xFFAAAAAA, false);
        return 34;
    }
}
