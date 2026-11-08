package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.ParticleStatus;
import net.minecraft.sounds.SoundSource;
import java.util.*;

/** Presets that set one vanilla option while enabled and restore yours when turned off. */
public final class OptionModules {
    private OptionModules() {}

    public static List<Module> create() {
        List<Module> l = new ArrayList<>();

        // ---- Performance presets ----
        for (int d : new int[]{2, 4, 6, 8, 10, 12, 16, 20, 24, 32})
            l.add(new OptionModule<>("Render Distance " + d, "Sets render distance to " + d + " chunks.", Category.PERFORMANCE, o -> o.renderDistance(), d).group("renderDistance"));
        for (int d : new int[]{5, 6, 8, 10, 12, 16, 24, 32})
            l.add(new OptionModule<>("Simulation Distance " + d, "Sets simulation distance to " + d + " chunks.", Category.PERFORMANCE, o -> o.simulationDistance(), d).group("simDistance"));
        for (int f : new int[]{30, 60, 75, 90, 120, 144, 165, 240})
            l.add(new OptionModule<>("Max FPS " + f, "Caps your frame rate at " + f + ".", Category.PERFORMANCE, o -> o.framerateLimit(), f).group("maxFps"));
        l.add(new OptionModule<>("Max FPS Unlimited", "Removes the frame rate cap.", Category.PERFORMANCE, o -> o.framerateLimit(), 260).group("maxFps"));
        for (int b : new int[]{0, 1, 2, 3, 5})
            l.add(new OptionModule<>("Biome Blend " + b, "Sets biome blend radius to " + b + ".", Category.PERFORMANCE, o -> o.biomeBlendRadius(), b).group("biomeBlend"));
        for (double e : new double[]{0.5, 0.75, 1.0, 1.5, 2.0, 3.0})
            l.add(new OptionModule<>("Entity Distance " + (int) (e * 100) + "%", "Sets how far away entities are drawn.", Category.PERFORMANCE, o -> o.entityDistanceScaling(), e).group("entityDistance"));
        l.add(new OptionModule<>("Fast Graphics", "Switches to Fast graphics.", Category.PERFORMANCE, o -> o.graphicsMode(), GraphicsStatus.FAST));
        l.add(new OptionModule<>("No Clouds", "Turns clouds off.", Category.PERFORMANCE, o -> o.cloudStatus(), CloudStatus.OFF));
        l.add(new OptionModule<>("Particles Minimal", "Only essential particles.", Category.PERFORMANCE, o -> o.particles(), ParticleStatus.MINIMAL).group("particles"));
        l.add(new OptionModule<>("Particles Decreased", "Fewer particles.", Category.PERFORMANCE, o -> o.particles(), ParticleStatus.DECREASED).group("particles"));
        l.add(new OptionModule<>("No Entity Shadows", "Turns entity shadows off.", Category.PERFORMANCE, o -> o.entityShadows(), false));
        l.add(new OptionModule<>("VSync Off", "Turns VSync off.", Category.PERFORMANCE, o -> o.enableVsync(), false));

        // ---- Visual ----
        for (int f : new int[]{30, 60, 70, 80, 90, 100, 110})
            l.add(new OptionModule<>("FOV " + f, "Sets field of view to " + f + ".", Category.VISUAL, o -> o.fov(), f).group("fov"));
        l.add(new OptionModule<>("No View Bobbing", "Turns camera bobbing off.", Category.VISUAL, o -> o.bobView(), false));
        l.add(new OptionModule<>("No FOV Effects", "Sprinting and speed no longer change your FOV.", Category.VISUAL, o -> o.fovEffectScale(), 0.0));
        l.add(new OptionModule<>("No Screen Distortion", "Removes the nausea/portal wobble.", Category.VISUAL, o -> o.screenEffectScale(), 0.0));
        l.add(new OptionModule<>("Attack Indicator Off", "Hides the attack cooldown indicator.", Category.VISUAL, o -> o.attackIndicator(), AttackIndicatorStatus.OFF).group("attackIndicator"));
        l.add(new OptionModule<>("Attack Indicator Crosshair", "Cooldown indicator at the crosshair.", Category.VISUAL, o -> o.attackIndicator(), AttackIndicatorStatus.CROSSHAIR).group("attackIndicator"));
        l.add(new OptionModule<>("Attack Indicator Hotbar", "Cooldown indicator next to the hotbar.", Category.VISUAL, o -> o.attackIndicator(), AttackIndicatorStatus.HOTBAR).group("attackIndicator"));

        // ---- Utility ----
        l.add(new OptionModule<>("Auto Jump", "Jump up single blocks automatically.", Category.UTILITY, o -> o.autoJump(), true));
        l.add(new OptionModule<>("Show Subtitles", "Shows sound subtitles.", Category.UTILITY, o -> o.showSubtitles(), true));
        l.add(new FlagModule("Always Sneak", "Holds sneak for you.", Category.UTILITY, mc -> mc.options.keyShift.setDown(true), mc -> mc.options.keyShift.setDown(false)));
        l.add(new FlagModule("Auto Walk", "Holds forward for you.", Category.UTILITY, mc -> mc.options.keyUp.setDown(true), mc -> mc.options.keyUp.setDown(false)));
        l.add(new FlagModule("Auto Sprint", "Holds the sprint key for you.", Category.UTILITY, mc -> mc.options.keySprint.setDown(true), mc -> mc.options.keySprint.setDown(false)));

        // ---- Audio ----
        for (SoundSource s : SoundSource.values()) {
            String n = cap(s.getName());
            l.add(new OptionModule<>("Mute " + n, "Sets " + n + " volume to 0.", Category.AUDIO, o -> o.getSoundSourceOptionInstance(s), 0.0).group("sound-" + s.getName()));
            l.add(new OptionModule<>(n + " 25%", "Sets " + n + " volume to 25%.", Category.AUDIO, o -> o.getSoundSourceOptionInstance(s), 0.25).group("sound-" + s.getName()));
            l.add(new OptionModule<>(n + " 50%", "Sets " + n + " volume to 50%.", Category.AUDIO, o -> o.getSoundSourceOptionInstance(s), 0.5).group("sound-" + s.getName()));
            l.add(new OptionModule<>(n + " 100%", "Sets " + n + " volume to 100%.", Category.AUDIO, o -> o.getSoundSourceOptionInstance(s), 1.0).group("sound-" + s.getName()));
        }

        // ---- Time of day (client-side) ----
        l.add(new TimeModule("Time Sunrise", 23000));
        l.add(new TimeModule("Time Day", 1000));
        l.add(new TimeModule("Time Noon", 6000));
        l.add(new TimeModule("Time Sunset", 12000));
        l.add(new TimeModule("Time Night", 13000));
        l.add(new TimeModule("Time Midnight", 18000));
        return l;
    }

    private static String cap(String s) { return Character.toUpperCase(s.charAt(0)) + s.substring(1); }
}
