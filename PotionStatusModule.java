package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.effect.MobEffectInstance;

/** Lists every active potion effect with level and time left. */
public class PotionStatusModule extends Module {
    public PotionStatusModule() { super("Potion Status", "Lists your active effects with level and time left.", Category.EFFECTS); }
    @Override public int renderHud(GuiGraphics g, int x, int y) {
        var p = mc().player; if (p == null) return 0;
        int used = 0;
        for (MobEffectInstance e : p.getActiveEffects()) {
            String name = e.getEffect().value().getDisplayName().getString();
            String time = e.isInfiniteDuration() ? "inf" : EffectTimerModule.fmt(e.getDuration());
            g.drawString(mc().font, name + " " + (e.getAmplifier() + 1) + "  " + time, x, y + used, 0xFFFFFFFF, true);
            used += 11;
        }
        return used;
    }
}
