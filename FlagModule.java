package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import java.util.function.Consumer;

/** Runs an action every tick while enabled, and an optional action when turned off. */
public class FlagModule extends Module {
    private final Consumer<net.minecraft.client.Minecraft> tick, off;
    public FlagModule(String name, String desc, Category cat, Consumer<net.minecraft.client.Minecraft> tick, Consumer<net.minecraft.client.Minecraft> off) {
        super(name, desc, cat); this.tick = tick; this.off = off;
    }
    @Override public void onTick() { if (mc().player != null && mc().level != null) tick.accept(mc()); }
    @Override protected void onDisable() { if (off != null && mc().player != null) off.accept(mc()); }
}
