package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;

/** Client-side time of day. Only changes what you see; the server's time is untouched. */
public class TimeModule extends Module {
    private final long ticks;
    public TimeModule(String name, long ticks) {
        super(name, "Locks the sky to this time of day on your screen only.", Category.VISUAL);
        this.ticks = ticks; group("time");
    }
    @Override public void onTick() { var l = mc().level; if (l != null) l.setDayTime(ticks); }
}
