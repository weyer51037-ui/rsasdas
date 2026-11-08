# Solstice Client Mod (Fabric, Minecraft 1.21.1)

In-game modules for Solstice Client. Press **Right Shift** to open the module menu.
Hold **C** to zoom, hold **V** for freelook (both rebindable in Controls).

600+ modules in the menu, with a search box (type to search every tab at once).
Tabs: HUD, PvP, Visual, Utility, Performance, Items, Effects, Audio.

## Build
Needs JDK 21. In this folder:  `gradlew build`  (or `gradle build`)  → `build/libs/solstice-client-mod-1.2.0.jar`
Put the jar in an instance's `mods` folder with Fabric Loader + Fabric API for 1.21.1.

## Add a module
1. Create a class extending `Module` in `module/impl/` (name, description, category; override `onTick`/`renderHud`).
2. Add it to the list in `ModuleManager.init()`. It appears in the menu automatically.

STATUS: written without being able to compile or test. Expect to fix small compile errors on first build.

## 1.1.0
- Sharp hub UI (no menu blur), new Performance tab.
- FPS Boost module: fast graphics, minimal/thinned particles, no clouds/shadows, render distance <= 8, simulation <= 6, uncapped FPS. Restores your settings when turned off.

## 1.2.0
- **TNT Timer**: remaining fuse (green/yellow/red) floating above every lit TNT. Plus TNT Fuse Alert and Primed TNT counters.
- 100+ HUD readouts: health/hunger/XP, speed, coordinates (chunk/nether), biome, light, time, weather, ping, memory, frame stats (1% low), target info and more.
- Combat: Combo Counter, Reach Display, Hit Counter, Target HUD, nearby players/crystals/hostiles, Hitboxes, No Hurt Cam.
- Items tab: a live counter for ~300 items plus pearl/chorus/shield cooldowns. Effects tab: Potion Status and a timer for every potion effect.
- Presets that set one vanilla option and restore yours afterwards: render/simulation distance, max FPS, FOV, particles, audio per sound group, and more. Modules in the same group switch each other off.
- Client-side Time of Day (sunrise...midnight), Clear Weather, Fullbright.
- HUD wraps into extra columns when many modules are on. solstice.json only stores enabled modules.
- Only vanilla-legal features: no x-ray, fly, killaura, etc.

STATUS (1.2.0): still written without a compiler. Please run the build and send me any errors.
