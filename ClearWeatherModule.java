package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;

/** Hides rain and thunder on your screen only. */
public class ClearWeatherModule extends Module {
    public ClearWeatherModule() { super("Clear Weather", "Hides rain and thunder on your screen.", Category.VISUAL); }
    @Override public void onTick() {
        var l = mc().level; if (l == null) return;
        l.setRainLevel(0f); l.setThunderLevel(0f);
    }
}
