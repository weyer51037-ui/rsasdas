package com.solstice.client.module;

import com.google.gson.*;
import com.solstice.client.module.impl.*;
import com.solstice.client.stats.EntityStats;
import com.solstice.client.stats.PvpStats;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    private static final Set<String> NAMES = new HashSet<>();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("solstice.json");

    public static FreelookModule freelook;
    public static ZoomModule zoom;
    public static CpsModule cps;
    public static SkyColorModule sky;
    public static CrystalOptimizerModule crystal;
    public static ExplosionOptimizerModule explosions;
    public static FpsBoostModule boost;
    public static TntTimerModule tnt;
    public static NoHurtCamModule noHurtCam;

    public static void init() {
        freelook = new FreelookModule();
        zoom = new ZoomModule();
        cps = new CpsModule();
        sky = new SkyColorModule();
        crystal = new CrystalOptimizerModule();
        explosions = new ExplosionOptimizerModule();
        boost = new FpsBoostModule();
        tnt = new TntTimerModule();
        noHurtCam = new NoHurtCamModule();

        // Original modules
        register(List.of(
            new FpsModule(), cps, new KeystrokesModule(), new CoordinatesModule(), new ClockModule(),
            new ArmorStatusModule(), new ToggleSprintModule(), zoom, freelook, sky, new PvpPerformanceModule(), crystal, explosions, boost
        ));
        // New in 1.2.0
        register(List.of(tnt, noHurtCam, new HitboxesModule(), new FullbrightModule(), new ClearWeatherModule(),
            new TargetHudModule(), new PotionStatusModule()));
        register(InfoModules.create());
        register(ItemModules.create());
        register(effectTimers());
        register(OptionModules.create());
        load();
    }

    private static List<Module> effectTimers() {
        String ids = "speed slowness haste mining_fatigue strength instant_health instant_damage jump_boost nausea regeneration "
            + "resistance fire_resistance water_breathing invisibility blindness night_vision hunger weakness poison wither "
            + "health_boost absorption saturation glowing levitation luck unluck slow_falling conduit_power dolphins_grace "
            + "bad_omen hero_of_the_village darkness trial_omen raid_omen wind_charged weaving oozing infested";
        List<Module> out = new ArrayList<>();
        for (String id : ids.split(" ")) out.add(new EffectTimerModule(id));
        return out;
    }

    private static void register(List<? extends Module> list) {
        for (Module m : list) if (NAMES.add(m.name)) MODULES.add(m);
    }

    public static List<Module> all() { return MODULES; }

    public static void tick() {
        EntityStats.tick();
        PvpStats.tick();
        for (Module m : MODULES) if (m.isEnabled()) m.onTick();
    }

    /** Game is closing: put every option a module changed back, without touching enabled flags. */
    public static void shutdown() {
        for (Module m : MODULES) if (m.isEnabled()) m.onShutdown();
        var o = net.minecraft.client.Minecraft.getInstance().options;
        if (o != null) o.save();
    }

    public static int enabledCount() { int n = 0; for (Module m : MODULES) if (m.isEnabled()) n++; return n; }

    public static void load() {
        try {
            if (!Files.exists(FILE)) { defaults(); return; }
            JsonObject o = JsonParser.parseString(Files.readString(FILE)).getAsJsonObject();
            for (Module m : MODULES) if (o.has(m.name) && o.get(m.name).getAsBoolean()) m.setEnabled(true);
        } catch (Exception e) { defaults(); }
    }
    private static void defaults() {
        for (Module m : MODULES) if (m instanceof FpsModule || m instanceof CoordinatesModule) m.setEnabled(true);
    }

    /** Only enabled modules are written, so the file stays small even with hundreds of modules. */
    public static void save() {
        JsonObject o = new JsonObject();
        for (Module m : MODULES) if (m.isEnabled()) o.addProperty(m.name, true);
        try { Files.writeString(FILE, new GsonBuilder().setPrettyPrinting().create().toJson(o)); } catch (IOException ignored) {}
    }
}
