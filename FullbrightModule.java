package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import com.solstice.client.stats.Ids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;

/** Keeps a client-side night vision effect on so caves and nights are fully bright. */
public class FullbrightModule extends Module {
    public FullbrightModule() { super("Fullbright", "Full brightness everywhere (client-side night vision).", Category.VISUAL); }
    @Override public void onTick() {
        var p = mc().player; if (p == null) return;
        BuiltInRegistries.MOB_EFFECT.getHolder(Ids.mc("night_vision")).ifPresent(h -> {
            MobEffectInstance cur = p.getEffect(h);
            if (cur == null || cur.getDuration() < 400) p.addEffect(new MobEffectInstance(h, 100000, 0, false, false, false));
        });
    }
    @Override protected void onDisable() {
        var p = mc().player; if (p == null) return;
        BuiltInRegistries.MOB_EFFECT.getHolder(Ids.mc("night_vision")).ifPresent(p::removeEffect);
    }
}
