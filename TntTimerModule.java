package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import net.minecraft.world.entity.item.PrimedTnt;

/** Shows the remaining fuse above every primed TNT. Drawn by EntityRendererMixin. */
public class TntTimerModule extends Module {
    public TntTimerModule() { super("TNT Timer", "Shows the remaining fuse time above lit TNT.", Category.PVP); }

    public String label(PrimedTnt tnt, float partialTick) {
        float s = Math.max(0f, (tnt.getFuse() - partialTick) / 20f);
        return String.format("%.1fs", s);
    }
    /** Green > 2s, yellow > 1s, red below. */
    public int color(PrimedTnt tnt, float partialTick) {
        float s = (tnt.getFuse() - partialTick) / 20f;
        return s > 2f ? 0xFF55FF55 : s > 1f ? 0xFFFFFF55 : 0xFFFF5555;
    }
}
