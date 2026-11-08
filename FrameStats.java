package com.solstice.client.stats;

import java.util.Arrays;

/** Rolling frame-time statistics, fed once per HUD render. */
public final class FrameStats {
    private static final float[] TIMES = new float[240];
    private static int idx, filled, sinceSort;
    private static long last = System.nanoTime();
    private static float avgMs, maxMs, minMs, lowMs;
    private FrameStats() {}

    public static void frame() {
        long now = System.nanoTime();
        float ms = (now - last) / 1_000_000f;
        last = now;
        TIMES[idx] = ms;
        idx = (idx + 1) % TIMES.length;
        if (filled < TIMES.length) filled++;
        if (++sinceSort >= 20) { sinceSort = 0; recompute(); }
    }

    private static void recompute() {
        if (filled == 0) return;
        float[] copy = Arrays.copyOf(TIMES, filled);
        Arrays.sort(copy);
        float sum = 0;
        for (float f : copy) sum += f;
        avgMs = sum / filled;
        minMs = copy[0];
        maxMs = copy[filled - 1];
        int worst = Math.max(1, filled / 100);
        float w = 0;
        for (int i = 0; i < worst; i++) w += copy[filled - 1 - i];
        lowMs = w / worst;
    }

    private static int fps(float ms) { return ms <= 0.01f ? 0 : Math.round(1000f / ms); }
    public static float frameMs() { return TIMES[(idx + TIMES.length - 1) % TIMES.length]; }
    public static int avgFps() { return fps(avgMs); }
    public static int minFps() { return fps(maxMs); }
    public static int maxFps() { return fps(minMs); }
    public static int lowFps() { return fps(lowMs); }
}
