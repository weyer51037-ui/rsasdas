package com.solstice.client.stats;

import net.minecraft.resources.ResourceLocation;

/** Single place that builds vanilla ResourceLocations, so a mapping change only needs one fix. */
public final class Ids {
    private Ids() {}
    public static ResourceLocation mc(String path) { return ResourceLocation.withDefaultNamespace(path); }

    /** "ender_pearl" -> "Ender Pearl" (a few special cases). */
    public static String pretty(String id) {
        switch (id) {
            case "tnt": return "TNT";
            case "tnt_minecart": return "TNT Minecart";
            case "xp": return "XP";
            default:
        }
        StringBuilder sb = new StringBuilder();
        for (String part : id.split("_")) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            if (part.equals("of") || part.equals("on") || part.equals("a") || part.equals("and")) sb.append(part);
            else sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }
}
