package com.solstice.client.module.impl;

import com.solstice.client.module.*;
import com.solstice.client.module.Module;
import com.solstice.client.stats.Ids;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import java.util.*;

/**
 * One counter per item id (shows how many you carry) plus cooldown trackers.
 * Ids that do not exist in the running version simply show nothing, so a typo can never crash the game.
 */
public final class ItemModules {
    private ItemModules() {}

    private static final String PVP = """
        totem_of_undying golden_apple enchanted_golden_apple ender_pearl end_crystal obsidian crying_obsidian respawn_anchor
        glowstone tnt tnt_minecart arrow spectral_arrow tipped_arrow bow crossbow trident shield elytra firework_rocket
        fire_charge flint_and_steel water_bucket lava_bucket powder_snow_bucket milk_bucket cobweb ender_chest
        experience_bottle potion splash_potion lingering_potion wind_charge mace breeze_rod heavy_core snowball egg
        fishing_rod chorus_fruit ender_eye lead saddle name_tag
        """;
    private static final String FOOD = """
        golden_carrot cooked_beef cooked_porkchop cooked_chicken cooked_mutton cooked_rabbit cooked_salmon cooked_cod
        bread baked_potato apple carrot potato melon_slice sweet_berries glow_berries honey_bottle pumpkin_pie cake cookie
        mushroom_stew rabbit_stew beetroot_soup suspicious_stew dried_kelp beef porkchop chicken mutton salmon cod
        rotten_flesh spider_eye poisonous_potato beetroot pumpkin melon
        """;
    private static final String GEAR = """
        wooden_sword stone_sword iron_sword golden_sword diamond_sword netherite_sword
        wooden_axe stone_axe iron_axe golden_axe diamond_axe netherite_axe
        wooden_pickaxe stone_pickaxe iron_pickaxe golden_pickaxe diamond_pickaxe netherite_pickaxe
        wooden_shovel stone_shovel iron_shovel golden_shovel diamond_shovel netherite_shovel
        wooden_hoe stone_hoe iron_hoe golden_hoe diamond_hoe netherite_hoe
        leather_helmet leather_chestplate leather_leggings leather_boots
        chainmail_helmet chainmail_chestplate chainmail_leggings chainmail_boots
        iron_helmet iron_chestplate iron_leggings iron_boots
        golden_helmet golden_chestplate golden_leggings golden_boots
        diamond_helmet diamond_chestplate diamond_leggings diamond_boots
        netherite_helmet netherite_chestplate netherite_leggings netherite_boots turtle_helmet
        shears bucket brush spyglass compass recovery_compass clock map filled_map carrot_on_a_stick warped_fungus_on_a_stick
        """;
    private static final String MATERIALS = """
        diamond emerald gold_ingot iron_ingot copper_ingot netherite_ingot netherite_scrap ancient_debris
        raw_iron raw_gold raw_copper coal charcoal redstone lapis_lazuli quartz amethyst_shard glowstone_dust gunpowder
        blaze_rod blaze_powder nether_star ghast_tear slime_ball magma_cream phantom_membrane string feather leather
        rabbit_hide paper book stick flint clay_ball brick nether_brick sugar sugar_cane wheat gold_nugget iron_nugget
        bone bone_meal ink_sac glow_ink_sac prismarine_shard prismarine_crystals nautilus_shell heart_of_the_sea
        dragon_breath shulker_shell echo_shard disc_fragment_5 netherite_upgrade_smithing_template
        diamond_block emerald_block gold_block iron_block netherite_block coal_block redstone_block lapis_block
        copper_block quartz_block raw_iron_block raw_gold_block raw_copper_block amethyst_block hay_block
        """;
    private static final String BLOCKS = """
        cobblestone stone deepslate cobbled_deepslate dirt grass_block sand red_sand gravel glass
        oak_log spruce_log birch_log jungle_log acacia_log dark_oak_log mangrove_log cherry_log
        oak_planks spruce_planks birch_planks jungle_planks acacia_planks dark_oak_planks mangrove_planks cherry_planks
        stone_bricks deepslate_bricks bricks sandstone terracotta netherrack basalt blackstone end_stone soul_sand soul_soil
        ice packed_ice blue_ice snow_block slime_block honey_block sponge wet_sponge magma_block
        white_wool oak_leaves moss_block mud clay
        torch soul_torch lantern soul_lantern redstone_torch ladder scaffolding chain
        piston sticky_piston observer hopper dropper dispenser repeater comparator lever stone_button oak_button
        stone_pressure_plate oak_pressure_plate tripwire_hook rail powered_rail detector_rail activator_rail minecart
        chest barrel furnace blast_furnace smoker crafting_table anvil enchanting_table brewing_stand cauldron
        white_bed oak_boat oak_door iron_door oak_trapdoor iron_trapdoor oak_fence oak_fence_gate cobblestone_wall
        oak_sign flower_pot item_frame painting armor_stand bookshelf lectern jukebox note_block beacon conduit
        end_portal_frame spawner dragon_egg shulker_box
        """;
    private static final String COOLDOWNS = "ender_pearl chorus_fruit shield wind_charge goat_horn";

    public static List<Module> create() {
        List<Module> out = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (String block : List.of(PVP, FOOD, GEAR, MATERIALS, BLOCKS))
            for (String id : block.trim().split("\\s+")) if (!id.isEmpty() && seen.add(id)) out.add(new ItemCountModule(id));
        for (String id : COOLDOWNS.split(" ")) out.add(new CooldownModule(id));
        return out;
    }

    /** "Name: N" - always shown, even at 0, so you notice when you run out. */
    public static final class ItemCountModule extends TextModule {
        public ItemCountModule(String id) {
            super(Ids.pretty(id) + " Count", "How many " + Ids.pretty(id) + " you carry.", Category.ITEMS, supplier(id));
        }
        private static java.util.function.Supplier<String> supplier(String id) {
            return new java.util.function.Supplier<>() {
                private Item item; private boolean looked;
                @Override public String get() {
                    if (!looked) { looked = true; item = BuiltInRegistries.ITEM.getOptional(Ids.mc(id)).orElse(null); }
                    var p = net.minecraft.client.Minecraft.getInstance().player;
                    if (item == null || p == null) return null;
                    return Ids.pretty(id) + ": " + p.getInventory().countItem(item);
                }
            };
        }
    }

    /** Shows the remaining cooldown of an item (pearls, chorus fruit, shield...) while it is active. */
    public static final class CooldownModule extends TextModule {
        public CooldownModule(String id) {
            super(Ids.pretty(id) + " Cooldown", Ids.pretty(id) + " cooldown in percent (hidden when ready).", Category.ITEMS, supplier(id));
        }
        private static java.util.function.Supplier<String> supplier(String id) {
            return new java.util.function.Supplier<>() {
                private Item item; private boolean looked;
                @Override public String get() {
                    if (!looked) { looked = true; item = BuiltInRegistries.ITEM.getOptional(Ids.mc(id)).orElse(null); }
                    var p = net.minecraft.client.Minecraft.getInstance().player;
                    if (item == null || p == null) return null;
                    float c = p.getCooldowns().getCooldownPercent(item, 0f);
                    return c <= 0f ? null : Ids.pretty(id) + " CD: " + (int) (c * 100) + "%";
                }
            };
        }
    }
}
