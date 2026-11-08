package com.solstice.client.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;

/** Counts of nearby entities, refreshed every few ticks. */
public final class EntityStats {
    public static int loaded;
    public static int players16, players32, players64, playersAll;
    public static int hostile16, hostile32, passive16, passive32;
    public static int items8, items16, projectiles16;
    public static int crystals16, crystals32, crystals64;
    public static int tnt16, tnt32, tnt64, tntAll;
    public static float minFuseSeconds = -1;
    public static String nearestPlayer; public static double nearestPlayerDist = -1; public static float nearestPlayerHealth;
    public static double nearestHostileDist = -1;
    private EntityStats() {}

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.player.tickCount % 5 != 0) return;
        loaded = 0; players16 = players32 = players64 = playersAll = 0;
        hostile16 = hostile32 = passive16 = passive32 = 0;
        items8 = items16 = projectiles16 = 0;
        crystals16 = crystals32 = crystals64 = 0;
        tnt16 = tnt32 = tnt64 = tntAll = 0;
        minFuseSeconds = -1; nearestPlayer = null; nearestPlayerDist = -1; nearestHostileDist = -1;
        for (Entity e : mc.level.entitiesForRendering()) {
            loaded++;
            if (e == mc.player) continue;
            double d = mc.player.distanceTo(e);
            if (e instanceof Player pl) {
                if (pl.isSpectator()) continue;
                playersAll++;
                if (d <= 16) players16++;
                if (d <= 32) players32++;
                if (d <= 64) players64++;
                if (nearestPlayerDist < 0 || d < nearestPlayerDist) {
                    nearestPlayerDist = d; nearestPlayer = pl.getName().getString(); nearestPlayerHealth = pl.getHealth();
                }
            } else if (e instanceof Enemy) {
                if (d <= 16) hostile16++;
                if (d <= 32) hostile32++;
                if (nearestHostileDist < 0 || d < nearestHostileDist) nearestHostileDist = d;
            } else if (e instanceof Animal) {
                if (d <= 16) passive16++;
                if (d <= 32) passive32++;
            } else if (e instanceof ItemEntity) {
                if (d <= 8) items8++;
                if (d <= 16) items16++;
            } else if (e instanceof EndCrystal) {
                if (d <= 16) crystals16++;
                if (d <= 32) crystals32++;
                if (d <= 64) crystals64++;
            } else if (e instanceof PrimedTnt t) {
                tntAll++;
                if (d <= 16) tnt16++;
                if (d <= 32) tnt32++;
                if (d <= 64) tnt64++;
                float s = t.getFuse() / 20f;
                if (minFuseSeconds < 0 || s < minFuseSeconds) minFuseSeconds = s;
            } else if (e instanceof Projectile) {
                if (d <= 16) projectiles16++;
            }
        }
    }
}
