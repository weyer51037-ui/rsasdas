package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import com.solstice.client.stats.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

/** Every one-line HUD readout. Each lambda returns the text to draw, or null to hide the line. */
public final class InfoModules {
    private static final long START = System.currentTimeMillis();
    private InfoModules() {}

    private static Minecraft mc() { return Minecraft.getInstance(); }
    private static LocalPlayer p() { return mc().player; }

    private static String cap(String s) { return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1); }
    private static String mmss(long sec) { return (sec / 60) + ":" + String.format("%02d", sec % 60); }

    public static List<Module> create() {
        List<Module> l = new ArrayList<>();
        // ---------------- Player ----------------
        t(l, "Health", "Your health.", Category.HUD, () -> String.format("Health: %.1f / %.0f", p().getHealth(), p().getMaxHealth()));
        t(l, "Hearts", "Your health in hearts.", Category.HUD, () -> String.format("Hearts: %.1f", p().getHealth() / 2f));
        t(l, "Hunger", "Your food level.", Category.HUD, () -> "Hunger: " + p().getFoodData().getFoodLevel() + " / 20");
        t(l, "Saturation", "Your saturation level.", Category.HUD, () -> String.format("Saturation: %.1f", p().getFoodData().getSaturationLevel()));
        t(l, "Armor Points", "Total armor points.", Category.HUD, () -> "Armor: " + p().getArmorValue());
        t(l, "Absorption", "Absorption hearts (golden apples).", Category.HUD, () -> p().getAbsorptionAmount() > 0 ? String.format("Absorption: %.1f", p().getAbsorptionAmount()) : null);
        t(l, "XP Level", "Your experience level.", Category.HUD, () -> "XP Level: " + p().experienceLevel);
        t(l, "XP Progress", "Progress to the next level.", Category.HUD, () -> "XP: " + (int) (p().experienceProgress * 100) + "%");
        t(l, "XP Total", "Total experience points.", Category.HUD, () -> "XP Total: " + p().totalExperience);
        t(l, "Air Supply", "Seconds of air left (only underwater).", Category.HUD, () -> p().getAirSupply() >= p().getMaxAirSupply() ? null : "Air: " + Math.max(0, p().getAirSupply() / 20) + "s");
        t(l, "Freeze Level", "Powder snow freeze percent.", Category.HUD, () -> p().getPercentFrozen() > 0 ? "Frozen: " + (int) (p().getPercentFrozen() * 100) + "%" : null);
        t(l, "Hurt Time", "Ticks of damage flash left.", Category.HUD, () -> "Hurt: " + p().hurtTime);
        t(l, "Active Effects Count", "How many effects you have.", Category.HUD, () -> "Effects: " + p().getActiveEffects().size());
        t(l, "Pose", "Standing, crouching, swimming, gliding...", Category.HUD, () -> "Pose: " + cap(p().getPose().name().toLowerCase(Locale.ROOT)));
        t(l, "Movement State", "Sprinting / sneaking / flying / gliding.", Category.HUD, () -> {
            var pl = p();
            return "State: " + (pl.isFallFlying() ? "Gliding" : pl.getAbilities().flying ? "Flying" : pl.isSwimming() ? "Swimming"
                : pl.isCrouching() ? "Sneaking" : pl.isSprinting() ? "Sprinting" : "Walking");
        });
        t(l, "On Ground", "Whether you are touching the ground.", Category.HUD, () -> "Ground: " + (p().onGround() ? "yes" : "no"));
        t(l, "In Water", "Whether you are in water.", Category.HUD, () -> p().isInWater() ? "In water" : null);
        t(l, "In Lava", "Whether you are in lava.", Category.HUD, () -> p().isInLava() ? "In lava" : null);
        t(l, "On Fire", "Whether you are burning.", Category.HUD, () -> p().isOnFire() ? "On fire" : null);
        t(l, "Riding", "What you are riding.", Category.HUD, () -> p().getVehicle() == null ? null : "Riding: " + p().getVehicle().getName().getString());
        t(l, "Gamemode", "Your game mode.", Category.HUD, () -> mc().gameMode == null ? null : "Mode: " + cap(mc().gameMode.getPlayerMode().getName()));
        t(l, "Attack Cooldown", "Weapon charge in percent.", Category.PVP, () -> "Charge: " + (int) (p().getAttackStrengthScale(0f) * 100) + "%");

        // ---------------- Movement ----------------
        t(l, "Speed", "Horizontal speed in blocks per second.", Category.HUD, () -> String.format("Speed: %.2f b/s", Math.hypot(p().getX() - p().xo, p().getZ() - p().zo) * 20));
        t(l, "Vertical Speed", "Up/down speed in blocks per second.", Category.HUD, () -> String.format("V-Speed: %.2f b/s", (p().getY() - p().yo) * 20));
        t(l, "Speed 3D", "Total speed in blocks per second.", Category.HUD, () -> {
            var pl = p(); double dx = pl.getX() - pl.xo, dy = pl.getY() - pl.yo, dz = pl.getZ() - pl.zo;
            return String.format("Speed 3D: %.2f b/s", Math.sqrt(dx * dx + dy * dy + dz * dz) * 20);
        });
        t(l, "Speed (km/h)", "Horizontal speed in km/h.", Category.HUD, () -> String.format("Speed: %.1f km/h", Math.hypot(p().getX() - p().xo, p().getZ() - p().zo) * 72));
        t(l, "Motion X", "Raw X velocity.", Category.HUD, () -> String.format("Motion X: %.3f", p().getDeltaMovement().x));
        t(l, "Motion Y", "Raw Y velocity.", Category.HUD, () -> String.format("Motion Y: %.3f", p().getDeltaMovement().y));
        t(l, "Motion Z", "Raw Z velocity.", Category.HUD, () -> String.format("Motion Z: %.3f", p().getDeltaMovement().z));
        t(l, "Fall Distance", "Blocks you have fallen.", Category.HUD, () -> p().fallDistance > 0.5f ? String.format("Fall: %.1f", p().fallDistance) : null);

        // ---------------- Position ----------------
        t(l, "X Coordinate", "Just your X.", Category.HUD, () -> String.format("X: %.1f", p().getX()));
        t(l, "Y Coordinate", "Just your Y.", Category.HUD, () -> String.format("Y: %.1f", p().getY()));
        t(l, "Z Coordinate", "Just your Z.", Category.HUD, () -> String.format("Z: %.1f", p().getZ()));
        t(l, "Block Position", "Whole-block XYZ.", Category.HUD, () -> {
            var b = p().blockPosition(); return "Block: " + b.getX() + " " + b.getY() + " " + b.getZ();
        });
        t(l, "Chunk Position", "Chunk X/Z you are in.", Category.HUD, () -> {
            var b = p().blockPosition(); return "Chunk: " + (b.getX() >> 4) + " " + (b.getZ() >> 4);
        });
        t(l, "Position In Chunk", "Block offset inside your chunk.", Category.HUD, () -> {
            var b = p().blockPosition(); return "In chunk: " + (b.getX() & 15) + " " + (b.getY()) + " " + (b.getZ() & 15);
        });
        t(l, "Nether Coordinates", "Matching Nether X/Z for your position.", Category.HUD, () -> String.format("Nether: %d / %d", (int) Math.floor(p().getX() / 8), (int) Math.floor(p().getZ() / 8)));
        t(l, "Overworld Coordinates", "Matching Overworld X/Z for your position.", Category.HUD, () -> String.format("Overworld: %d / %d", (int) Math.floor(p().getX() * 8), (int) Math.floor(p().getZ() * 8)));
        t(l, "Yaw", "Horizontal look angle.", Category.HUD, () -> String.format("Yaw: %.1f", Mth.wrapDegrees(p().getYRot())));
        t(l, "Pitch", "Vertical look angle.", Category.HUD, () -> String.format("Pitch: %.1f", p().getXRot()));
        t(l, "Facing", "Compass direction you face.", Category.HUD, () -> "Facing: " + cap(p().getDirection().getName()));
        t(l, "Compass Degrees", "Heading in 0-360 degrees.", Category.HUD, () -> String.format("Heading: %.0f", (Mth.wrapDegrees(p().getYRot()) + 540) % 360));
        t(l, "Last Death", "Coordinates of where you last died.", Category.HUD, () ->
            p().getLastDeathLocation().map(g -> "Died: " + g.pos().getX() + " " + g.pos().getY() + " " + g.pos().getZ()).orElse(null));
        t(l, "Spawn Coordinates", "World spawn position.", Category.HUD, () -> {
            var s = mc().level.getSharedSpawnPos(); return "Spawn: " + s.getX() + " " + s.getY() + " " + s.getZ();
        });
        t(l, "Distance To Spawn", "Blocks to world spawn.", Category.HUD, () ->
            "Spawn dist: " + (int) Math.sqrt(p().blockPosition().distSqr(mc().level.getSharedSpawnPos())));

        // ---------------- World ----------------
        t(l, "Biome", "Biome you are standing in.", Category.HUD, () ->
            mc().level.getBiome(p().blockPosition()).unwrapKey().map(k -> "Biome: " + Ids.pretty(k.location().getPath())).orElse(null));
        t(l, "Dimension", "Overworld, Nether or End.", Category.HUD, () -> "Dimension: " + Ids.pretty(mc().level.dimension().location().getPath()));
        t(l, "Light Level", "Combined light at your feet.", Category.HUD, () -> "Light: " + mc().level.getMaxLocalRawBrightness(p().blockPosition()));
        t(l, "Block Light", "Block light level at your feet.", Category.HUD, () -> "Block light: " + mc().level.getBrightness(LightLayer.BLOCK, p().blockPosition()));
        t(l, "Sky Light", "Sky light level at your feet.", Category.HUD, () -> "Sky light: " + mc().level.getBrightness(LightLayer.SKY, p().blockPosition()));
        t(l, "Spawn Warning", "Warns when block light is 0 (hostile mobs can spawn).", Category.HUD, () ->
            mc().level.getBrightness(LightLayer.BLOCK, p().blockPosition()) == 0 ? "Dark: mobs can spawn" : null);
        t(l, "Minecraft Time", "In-game clock (24h).", Category.HUD, () -> {
            long t = mc().level.getDayTime() % 24000L; long h = (t / 1000 + 6) % 24; long m = (t % 1000) * 60 / 1000;
            return String.format("Time: %02d:%02d", h, m);
        });
        t(l, "Day Ticks", "Ticks into the current day.", Category.HUD, () -> "Day ticks: " + (mc().level.getDayTime() % 24000L));
        t(l, "Day Count", "How many days the world has run.", Category.HUD, () -> "Day: " + (mc().level.getDayTime() / 24000L));
        t(l, "World Ticks", "Total game ticks.", Category.HUD, () -> "Ticks: " + mc().level.getGameTime());
        t(l, "Moon Phase", "Current moon phase (0 = full).", Category.HUD, () -> "Moon: " + mc().level.getMoonPhase());
        t(l, "Weather", "Clear / rain / thunder.", Category.HUD, () -> "Weather: " + (mc().level.isThundering() ? "Thunder" : mc().level.isRaining() ? "Rain" : "Clear"));
        t(l, "Difficulty", "World difficulty.", Category.HUD, () -> "Difficulty: " + mc().level.getDifficulty().getDisplayName().getString());
        t(l, "Looking At", "Block under your crosshair.", Category.HUD, () -> {
            HitResult h = mc().hitResult;
            if (h instanceof BlockHitResult b && h.getType() == HitResult.Type.BLOCK)
                return "Looking at: " + mc().level.getBlockState(b.getBlockPos()).getBlock().getName().getString();
            return null;
        });
        t(l, "Target Block Coordinates", "XYZ of the block under your crosshair.", Category.HUD, () -> {
            HitResult h = mc().hitResult;
            if (h instanceof BlockHitResult b && h.getType() == HitResult.Type.BLOCK) { var q = b.getBlockPos(); return "Target: " + q.getX() + " " + q.getY() + " " + q.getZ(); }
            return null;
        });
        t(l, "Target Entity", "Name of the entity under your crosshair.", Category.HUD, () ->
            mc().crosshairPickEntity == null ? null : "Target: " + mc().crosshairPickEntity.getName().getString());
        t(l, "Target Distance", "Distance to what your crosshair points at.", Category.HUD, () -> {
            HitResult h = mc().hitResult;
            if (h == null || h.getType() == HitResult.Type.MISS) return null;
            return String.format("Target dist: %.1f", h.getLocation().distanceTo(p().getEyePosition()));
        });

        // ---------------- Inventory / hands ----------------
        t(l, "Held Item", "Name of the item in your main hand.", Category.HUD, () -> p().getMainHandItem().isEmpty() ? null : "Held: " + p().getMainHandItem().getHoverName().getString());
        t(l, "Held Item Count", "Stack size of your held item.", Category.HUD, () -> p().getMainHandItem().isEmpty() ? null : "Count: " + p().getMainHandItem().getCount());
        t(l, "Held Durability", "Durability left on your held item.", Category.HUD, () -> {
            var s = p().getMainHandItem(); return s.isDamageableItem() ? "Durability: " + (s.getMaxDamage() - s.getDamageValue()) + " / " + s.getMaxDamage() : null;
        });
        t(l, "Held Durability Percent", "Durability percent of your held item.", Category.HUD, () -> {
            var s = p().getMainHandItem(); return s.isDamageableItem() ? "Durability: " + (100 * (s.getMaxDamage() - s.getDamageValue()) / s.getMaxDamage()) + "%" : null;
        });
        t(l, "Offhand Item", "Name of your offhand item.", Category.HUD, () -> p().getOffhandItem().isEmpty() ? null : "Offhand: " + p().getOffhandItem().getHoverName().getString());
        t(l, "Hotbar Slot", "Selected hotbar slot (1-9).", Category.HUD, () -> "Slot: " + (p().getInventory().selected + 1));
        t(l, "Free Inventory Slots", "Empty slots in your inventory.", Category.ITEMS, () -> {
            int n = 0; for (var s : p().getInventory().items) if (s.isEmpty()) n++; return "Free slots: " + n;
        });
        t(l, "Free Hotbar Slots", "Empty slots in your hotbar.", Category.ITEMS, () -> {
            int n = 0; for (int i = 0; i < 9; i++) if (p().getInventory().items.get(i).isEmpty()) n++; return "Free hotbar: " + n;
        });
        String[][] armor = { {"Helmet", "HEAD"}, {"Chestplate", "CHEST"}, {"Leggings", "LEGS"}, {"Boots", "FEET"} };
        for (String[] a : armor) {
            var slot = net.minecraft.world.entity.EquipmentSlot.valueOf(a[1]);
            t(l, a[0] + " Durability", "Durability left on your " + a[0].toLowerCase(Locale.ROOT) + ".", Category.ITEMS, () -> {
                var s = p().getItemBySlot(slot);
                return s.isDamageableItem() ? a[0] + ": " + (s.getMaxDamage() - s.getDamageValue()) : null;
            });
        }
        t(l, "Total Armor Durability", "Sum of durability across worn armor.", Category.ITEMS, () -> {
            int sum = 0;
            for (var slot : new net.minecraft.world.entity.EquipmentSlot[]{ net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST, net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET }) {
                var s = p().getItemBySlot(slot); if (s.isDamageableItem()) sum += s.getMaxDamage() - s.getDamageValue();
            }
            return "Armor durability: " + sum;
        });

        // ---------------- Server ----------------
        t(l, "Ping", "Your latency to the server.", Category.HUD, () -> {
            var c = mc().getConnection(); if (c == null) return null;
            var info = c.getPlayerInfo(p().getUUID()); return info == null ? null : "Ping: " + info.getLatency() + "ms";
        });
        t(l, "Server Address", "The server you are on.", Category.HUD, () -> mc().getCurrentServer() == null ? "Singleplayer" : mc().getCurrentServer().ip);
        t(l, "Players Online", "Players in the tab list.", Category.HUD, () -> {
            var c = mc().getConnection(); return c == null ? null : "Online: " + c.getOnlinePlayers().size();
        });

        // ---------------- System / performance ----------------
        t(l, "Frame Time", "Milliseconds per frame.", Category.HUD, () -> String.format("Frame: %.1f ms", FrameStats.frameMs()));
        t(l, "Average FPS", "Average FPS over the last few seconds.", Category.HUD, () -> "Avg FPS: " + FrameStats.avgFps());
        t(l, "Min FPS", "Lowest FPS in the recent window.", Category.HUD, () -> "Min FPS: " + FrameStats.minFps());
        t(l, "Max FPS", "Highest FPS in the recent window.", Category.HUD, () -> "Max FPS: " + FrameStats.maxFps());
        t(l, "1% Low FPS", "Average of your worst frames (stutter indicator).", Category.HUD, () -> "1% low: " + FrameStats.lowFps());
        t(l, "Memory Usage", "Java memory used / max.", Category.HUD, () -> {
            Runtime r = Runtime.getRuntime(); long used = (r.totalMemory() - r.freeMemory()) >> 20, max = r.maxMemory() >> 20;
            return "Mem: " + used + " / " + max + " MB";
        });
        t(l, "Memory Percent", "Java memory used in percent.", Category.HUD, () -> {
            Runtime r = Runtime.getRuntime(); return "Mem: " + (100 * (r.totalMemory() - r.freeMemory()) / r.maxMemory()) + "%";
        });
        t(l, "Java Version", "Java runtime version.", Category.HUD, () -> "Java " + System.getProperty("java.version"));
        t(l, "Minecraft Version", "Game version.", Category.HUD, () -> "MC " + mc().getLaunchedVersion());
        t(l, "Resolution", "Window size in pixels.", Category.HUD, () -> mc().getWindow().getWidth() + "x" + mc().getWindow().getHeight());
        t(l, "GUI Scale", "Current GUI scale.", Category.HUD, () -> "GUI scale: " + (int) mc().getWindow().getGuiScale());
        t(l, "Render Distance Info", "Your render distance setting.", Category.HUD, () -> "Render: " + mc().options.renderDistance().get());
        t(l, "Simulation Distance Info", "Your simulation distance setting.", Category.HUD, () -> "Simulation: " + mc().options.simulationDistance().get());
        t(l, "FOV Info", "Your field of view setting.", Category.HUD, () -> "FOV: " + mc().options.fov().get());
        t(l, "FPS Limit Info", "Your max framerate setting.", Category.HUD, () -> {
            int f = mc().options.framerateLimit().get(); return "FPS limit: " + (f >= 260 ? "Unlimited" : String.valueOf(f));
        });
        t(l, "Particle Count", "Particles currently alive.", Category.HUD, () -> "Particles: " + mc().particleEngine.countParticles());
        t(l, "Entities Loaded", "Entities your client knows about.", Category.HUD, () -> "Entities: " + EntityStats.loaded);

        // ---------------- Time ----------------
        DateTimeFormatter h12 = DateTimeFormatter.ofPattern("hh:mm a"), h24s = DateTimeFormatter.ofPattern("HH:mm:ss");
        t(l, "Clock 12h", "Real time, 12-hour.", Category.HUD, () -> LocalTime.now().format(h12));
        t(l, "Clock Seconds", "Real time with seconds.", Category.HUD, () -> LocalTime.now().format(h24s));
        t(l, "Real Date", "Today's date.", Category.HUD, () -> LocalDate.now().toString());
        t(l, "Session Time", "How long this game has been open.", Category.HUD, () -> "Session: " + mmss((System.currentTimeMillis() - START) / 1000));

        // ---------------- Combat stats ----------------
        t(l, "Combo Counter", "Hits in a row without being hit.", Category.PVP, () -> "Combo: " + PvpStats.combo);
        t(l, "Max Combo", "Your best combo this session.", Category.PVP, () -> "Max combo: " + PvpStats.maxCombo);
        t(l, "Hit Counter", "Total hits you landed on entities.", Category.PVP, () -> "Hits: " + PvpStats.hitsDealt);
        t(l, "Hits Taken", "Times you were hurt.", Category.PVP, () -> "Hurt: " + PvpStats.hitsTaken);
        t(l, "Reach Display", "Distance of your last hit.", Category.PVP, () -> PvpStats.lastReach < 0 ? "Reach: -" : String.format("Reach: %.2f", PvpStats.lastReach));
        t(l, "Hit Ratio", "Hits dealt per hit taken.", Category.PVP, () -> PvpStats.hitsTaken == 0 ? "Hit ratio: -" : String.format("Hit ratio: %.2f", PvpStats.hitsDealt / (float) PvpStats.hitsTaken));

        // ---------------- Nearby ----------------
        t(l, "Nearby Players 16", "Other players within 16 blocks.", Category.PVP, () -> "Players (16): " + EntityStats.players16);
        t(l, "Nearby Players 32", "Other players within 32 blocks.", Category.PVP, () -> "Players (32): " + EntityStats.players32);
        t(l, "Nearby Players 64", "Other players within 64 blocks.", Category.PVP, () -> "Players (64): " + EntityStats.players64);
        t(l, "Players Loaded", "Other players your client can see.", Category.PVP, () -> "Players: " + EntityStats.playersAll);
        t(l, "Nearest Player", "Name and distance of the closest player.", Category.PVP, () ->
            EntityStats.nearestPlayer == null ? null : String.format("%s: %.0fm", EntityStats.nearestPlayer, EntityStats.nearestPlayerDist));
        t(l, "Nearest Player Health", "Health of the closest player.", Category.PVP, () ->
            EntityStats.nearestPlayer == null ? null : String.format("%s HP: %.1f", EntityStats.nearestPlayer, EntityStats.nearestPlayerHealth));
        t(l, "Nearby Hostiles 16", "Hostile mobs within 16 blocks.", Category.HUD, () -> "Hostiles (16): " + EntityStats.hostile16);
        t(l, "Nearby Hostiles 32", "Hostile mobs within 32 blocks.", Category.HUD, () -> "Hostiles (32): " + EntityStats.hostile32);
        t(l, "Nearest Hostile", "Distance to the closest hostile mob.", Category.HUD, () -> EntityStats.nearestHostileDist < 0 ? null : String.format("Hostile: %.0fm", EntityStats.nearestHostileDist));
        t(l, "Nearby Animals 16", "Animals within 16 blocks.", Category.HUD, () -> "Animals (16): " + EntityStats.passive16);
        t(l, "Nearby Animals 32", "Animals within 32 blocks.", Category.HUD, () -> "Animals (32): " + EntityStats.passive32);
        t(l, "Nearby Items 8", "Dropped items within 8 blocks.", Category.HUD, () -> "Items (8): " + EntityStats.items8);
        t(l, "Nearby Items 16", "Dropped items within 16 blocks.", Category.HUD, () -> "Items (16): " + EntityStats.items16);
        t(l, "Nearby Projectiles", "Arrows, pearls etc. within 16 blocks.", Category.PVP, () -> "Projectiles: " + EntityStats.projectiles16);
        t(l, "Nearby Crystals 16", "End crystals within 16 blocks.", Category.PVP, () -> "Crystals (16): " + EntityStats.crystals16);
        t(l, "Nearby Crystals 32", "End crystals within 32 blocks.", Category.PVP, () -> "Crystals (32): " + EntityStats.crystals32);
        t(l, "Nearby Crystals 64", "End crystals within 64 blocks.", Category.PVP, () -> "Crystals (64): " + EntityStats.crystals64);
        t(l, "Primed TNT 16", "Lit TNT within 16 blocks.", Category.PVP, () -> "TNT (16): " + EntityStats.tnt16);
        t(l, "Primed TNT 32", "Lit TNT within 32 blocks.", Category.PVP, () -> "TNT (32): " + EntityStats.tnt32);
        t(l, "Primed TNT 64", "Lit TNT within 64 blocks.", Category.PVP, () -> "TNT (64): " + EntityStats.tnt64);
        t(l, "TNT Fuse Alert", "Lowest fuse among all lit TNT you can see.", Category.PVP, () ->
            EntityStats.minFuseSeconds < 0 ? null : String.format("TNT fuse: %.1fs", EntityStats.minFuseSeconds));
        return l;
    }

    private static void t(List<Module> l, String name, String desc, Category cat, Supplier<String> s) {
        l.add(new TextModule(name, desc, cat, s));
    }
}
