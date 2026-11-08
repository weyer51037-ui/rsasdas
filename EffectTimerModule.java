package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import com.solstice.client.stats.Ids;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;

/** Time left on one specific effect. Hidden while the effect is not active. */
public class EffectTimerModule extends Module {
    private final String id;
    public EffectTimerModule(String id) {
        super(Ids.pretty(id) + " Timer", "Time left on " + Ids.pretty(id) + " (hidden when inactive).", Category.EFFECTS);
        this.id = id;
    }
    static String fmt(int ticks) { int s = Math.max(0, ticks / 20); return (s / 60) + ":" + String.format("%02d", s % 60); }
    @Override public int renderHud(GuiGraphics g, int x, int y) {
        var p = mc().player; if (p == null) return 0;
        var holder = BuiltInRegistries.MOB_EFFECT.getHolder(Ids.mc(id));
        if (holder.isEmpty()) return 0;
        MobEffectInstance e = p.getEffect(holder.get());
        if (e == null) return 0;
        String time = e.isInfiniteDuration() ? "inf" : fmt(e.getDuration());
        g.drawString(mc().font, Ids.pretty(id) + " " + (e.getAmplifier() + 1) + ": " + time, x, y, 0xFFFFFFFF, true);
        return 11;
    }
}
