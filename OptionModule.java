package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import java.util.function.Function;

/**
 * Sets one video/sound/control option while enabled and puts your original value back when disabled
 * (and when the game closes). Applied on the first tick because Options may not exist at startup.
 */
public class OptionModule<T> extends Module {
    private final Function<Options, OptionInstance<T>> option;
    private final T value;
    private T old; private boolean applied;

    public OptionModule(String name, String desc, Category cat, Function<Options, OptionInstance<T>> option, T value) {
        super(name, desc, cat); this.option = option; this.value = value;
    }

    @Override protected void onEnable() { applied = false; }

    @Override public void onTick() {
        if (applied) return;
        Options o = mc().options;
        if (o == null) return;
        OptionInstance<T> i = option.apply(o);
        old = i.get();
        i.set(value);
        applied = true;
    }

    private void restore() {
        if (!applied) return;
        Options o = mc().options;
        if (o != null && old != null) option.apply(o).set(old);
        applied = false;
    }
    @Override protected void onDisable() { restore(); }
    @Override public void onShutdown() { restore(); }
}
