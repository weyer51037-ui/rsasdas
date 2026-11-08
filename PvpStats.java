package com.solstice.client.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Combat bookkeeping fed by MultiPlayerGameModeMixin (attacks) and the client tick (damage taken). */
public final class PvpStats {
    public static int combo, maxCombo, hitsDealt, hitsTaken;
    public static double lastReach = -1;
    public static LivingEntity target;
    public static long targetTime;
    private static int lastHurt;
    private PvpStats() {}

    public static void onAttack(Player p, Entity t) {
        hitsDealt++; combo++;
        if (combo > maxCombo) maxCombo = combo;
        Vec3 eye = p.getEyePosition();
        AABB b = t.getBoundingBox();
        double dx = Mth.clamp(eye.x, b.minX, b.maxX) - eye.x;
        double dy = Mth.clamp(eye.y, b.minY, b.maxY) - eye.y;
        double dz = Mth.clamp(eye.z, b.minZ, b.maxZ) - eye.z;
        lastReach = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (t instanceof LivingEntity le) { target = le; targetTime = System.currentTimeMillis(); }
    }

    public static void tick() {
        var p = Minecraft.getInstance().player;
        if (p == null) return;
        if (p.hurtTime > 0 && lastHurt == 0) { hitsTaken++; combo = 0; }
        lastHurt = p.hurtTime;
    }

    public static void reset() { combo = maxCombo = hitsDealt = hitsTaken = 0; lastReach = -1; target = null; }
}
