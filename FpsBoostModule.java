package com.solstice.client.module.impl;
import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Options;
import net.minecraft.client.ParticleStatus;

/**
 * FPS Boost: applies a set of performance-friendly video settings while enabled and restores
 * your own settings when disabled. Also thins out particles (see ParticleEngineMixin).
 * Settings are applied on the first tick after enabling, because Options may not exist yet at startup.
 */
public class FpsBoostModule extends Module {
    /** Render distance / simulation distance caps while boosting. */
    public static final int MAX_RENDER = 8, MAX_SIM = 6;

    private boolean applied;
    private GraphicsStatus gfx; private ParticleStatus particles; private CloudStatus clouds;
    private boolean shadows, vsync, bob; private int blend, fps, render, sim; private double entityDist;

    public FpsBoostModule() {
        super("FPS Boost", "Fast graphics, fewer particles, no clouds/shadows, capped distances, uncapped FPS.", Category.PERFORMANCE);
    }

    public boolean active() { return isEnabled() && applied; }

    @Override protected void onEnable() { applied = false; }

    @Override public void onTick() {
        if (applied) return;
        Options o = mc().options;
        if (o == null) return;
        gfx = o.graphicsMode().get(); particles = o.particles().get(); clouds = o.cloudStatus().get();
        shadows = o.entityShadows().get(); vsync = o.enableVsync().get(); bob = o.bobView().get();
        blend = o.biomeBlendRadius().get(); fps = o.framerateLimit().get(); entityDist = o.entityDistanceScaling().get();
        render = o.renderDistance().get(); sim = o.simulationDistance().get();

        o.graphicsMode().set(GraphicsStatus.FAST);
        o.particles().set(ParticleStatus.MINIMAL);
        o.cloudStatus().set(CloudStatus.OFF);
        o.entityShadows().set(false);
        o.enableVsync().set(false);
        o.framerateLimit().set(260);               // 260 = "Unlimited"
        o.biomeBlendRadius().set(0);
        o.entityDistanceScaling().set(Math.min(entityDist, 0.5));
        o.renderDistance().set(Math.min(render, MAX_RENDER));
        o.simulationDistance().set(Math.min(sim, MAX_SIM));
        applied = true;
    }

    @Override public void onShutdown() { onDisable(); }

    @Override protected void onDisable() {
        if (!applied) return;
        Options o = mc().options;
        if (o != null) {
            o.graphicsMode().set(gfx); o.particles().set(particles); o.cloudStatus().set(clouds);
            o.entityShadows().set(shadows); o.enableVsync().set(vsync); o.bobView().set(bob);
            o.biomeBlendRadius().set(blend); o.framerateLimit().set(fps); o.entityDistanceScaling().set(entityDist);
            o.renderDistance().set(render); o.simulationDistance().set(sim);
        }
        applied = false;
    }
}
