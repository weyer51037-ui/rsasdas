package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;

/** Cancels the camera shake when you take damage (see GameRendererMixin). */
public class NoHurtCamModule extends Module {
    public NoHurtCamModule() { super("No Hurt Cam", "Removes the screen shake when you take damage.", Category.VISUAL); }
}
