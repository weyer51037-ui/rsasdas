package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;

/** Same as vanilla F3+B: draws entity hitboxes. */
public class HitboxesModule extends Module {
    public HitboxesModule() { super("Hitboxes", "Shows entity hitboxes (like F3+B).", Category.PVP); }
    @Override public void onTick() { mc().getEntityRenderDispatcher().setRenderHitBoxes(true); }
    @Override protected void onDisable() { mc().getEntityRenderDispatcher().setRenderHitBoxes(false); }
}
