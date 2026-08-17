package com.atomgdx.editor.particle2d.presets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Built-in Library containing 100 categorized and tagged LibGDX 2D Particle Effect presets.
 */
public class ParticlePresetsLibrary {

    private static final List<ParticlePreset> PRESETS = new ArrayList<>();

    static {
        // --- 1. Sci-Fi & Energy (20 Presets) ---
        add("Plasma Burst", "Sci-Fi & Energy", "High-energy pulsing plasma discharge", 800, 300, 120, 400, 800, 12, 28, 150, 300, 0, 360, 0, 0, true, true, "particle.png", "plasma", "energy", "burst", "scifi", "glow");
        add("Ion Thruster", "Sci-Fi & Energy", "Continuous collimated blue ion rocket plume", 1200, 450, 180, 500, 1000, 8, 20, 250, 450, 260, 280, 0, 0, true, true, "particle.png", "thruster", "engine", "blue", "ship", "trail");
        add("Warp Jump Flash", "Sci-Fi & Energy", "Radial hyperdrive entry burst", 500, 600, 300, 300, 600, 16, 40, 400, 800, 0, 360, 0, 0, true, false, "particle.png", "warp", "flash", "hyperspace", "burst");
        add("Quantum Singularity", "Sci-Fi & Energy", "Swirling black hole accretion vortex", 2000, 400, 100, 800, 1600, 6, 18, -100, -200, 0, 360, 0, 0, true, true, "particle.png", "vortex", "singularity", "spiral", "gravity");
        add("Photon Torpedo Trail", "Sci-Fi & Energy", "Luminescent photon missile wake", 1000, 250, 90, 400, 700, 10, 22, 50, 100, 170, 190, 0, 0, true, true, "particle.png", "missile", "torpedo", "trail", "weapon");
        add("Antimatter Rift", "Sci-Fi & Energy", "Violent dimensional rift sparks", 1500, 350, 110, 600, 1200, 14, 32, 100, 250, 45, 135, 0, 0, true, true, "particle.png", "rift", "portal", "purple", "energy");
        add("Cyber Shield Deflect", "Sci-Fi & Energy", "Hexagonal energy barrier deflection sparks", 400, 200, 150, 200, 400, 8, 16, 120, 280, 0, 180, 0, 0, true, false, "particle.png", "shield", "defense", "spark", "cyan");
        add("Tachyon Beam", "Sci-Fi & Energy", "Continuous fast particle beam stream", 1000, 300, 200, 200, 400, 4, 12, 500, 900, 85, 95, 0, 0, true, true, "particle.png", "laser", "beam", "stream", "fast");
        add("Arc Welder Discharge", "Sci-Fi & Energy", "Erratic electrical sparks and blue arcs", 700, 180, 80, 150, 350, 4, 10, 200, 400, 30, 150, 0, 200, true, true, "particle.png", "electric", "spark", "industrial", "blue");
        add("Sub-Light Exhaust", "Sci-Fi & Energy", "Warm orange propulsion trail", 1100, 320, 130, 450, 900, 10, 24, 180, 320, 265, 275, 0, 0, true, true, "particle.png", "exhaust", "orange", "rocket", "thrust");
        add("Gauss Rifle Vapour", "Sci-Fi & Energy", "Supersonic kinetic projectile ionization trail", 600, 220, 140, 250, 500, 6, 14, 300, 600, 0, 10, 0, 0, true, false, "particle.png", "kinetic", "railgun", "gauss", "weapon");
        add("Hologram Glitch", "Sci-Fi & Energy", "Flickering digital cyan noise bits", 1200, 150, 60, 300, 700, 4, 8, 20, 60, 0, 360, 0, 0, true, true, "particle.png", "hologram", "glitch", "ui", "digital");
        add("Energy Siphon", "Sci-Fi & Energy", "Inward converging particle stream", 1400, 280, 90, 600, 1100, 8, 16, -150, -300, 0, 360, 0, 0, true, true, "particle.png", "siphon", "absorb", "inward", "green");
        add("Nanite Swarm", "Sci-Fi & Energy", "Self-assembling micro-machine cloud", 2500, 400, 80, 1000, 2000, 4, 8, 30, 80, 0, 360, 0, 0, false, true, "particle.png", "nanite", "swarm", "robot", "cloud");
        add("Reactor Core Pulse", "Sci-Fi & Energy", "Rhythmic glowing reactor shockwave", 1600, 350, 70, 700, 1400, 16, 36, 80, 180, 0, 360, 0, 0, true, true, "particle.png", "reactor", "pulse", "core", "glow");
        add("Laser Burn Embers", "Sci-Fi & Energy", "Molten metal sparks from laser cutting", 800, 200, 90, 300, 600, 4, 10, 100, 220, 60, 120, 0, 150, true, true, "particle.png", "laser", "cutting", "metal", "embers");
        add("Solar Flare Jet", "Sci-Fi & Energy", "Coronal mass ejection plasma burst", 2000, 500, 150, 800, 1600, 20, 48, 150, 350, 70, 110, 0, 0, true, true, "particle.png", "sun", "solar", "flare", "plasma");
        add("Forcefield Barrier", "Sci-Fi & Energy", "Hex grid ambient shield luminescence", 2200, 260, 60, 1000, 1800, 10, 20, 10, 30, 0, 360, 0, 0, true, true, "particle.png", "forcefield", "shield", "defense");
        add("EMP Shockwave", "Sci-Fi & Energy", "Electromagnetic wave radiating outwards", 600, 450, 250, 300, 600, 12, 30, 350, 700, 0, 360, 0, 0, true, false, "particle.png", "emp", "shockwave", "blast");
        add("Hyperdrive Overheat", "Sci-Fi & Energy", "Hot steam and red plasma venting", 1300, 320, 110, 500, 1000, 8, 22, 80, 180, 120, 240, -50, 0, true, true, "particle.png", "overheat", "vent", "smoke", "red");

        // --- 2. Combat & Weapons (16 Presets) ---
        add("Muzzle Flash", "Combat & Weapons", "High-intensity firearm gunshot burst", 150, 150, 250, 80, 150, 16, 32, 200, 450, -30, 30, 0, 0, true, false, "particle.png", "muzzle", "gun", "shot", "firearm");
        add("Bullet Shell Eject", "Combat & Weapons", "Spinning brass casing particle ejection", 600, 80, 40, 300, 500, 6, 12, 100, 200, 110, 150, 0, 400, false, true, "particle.png", "shell", "brass", "casing", "bullet");
        add("Blood Splatter Impact", "Combat & Weapons", "Directional red arterial splatter burst", 400, 180, 120, 200, 400, 6, 14, 150, 350, 30, 150, 0, 300, false, false, "particle.png", "blood", "gore", "hit", "impact");
        add("Spark Ricochet", "Combat & Weapons", "High-velocity metal ricochet fragments", 300, 120, 100, 150, 300, 4, 8, 250, 550, 45, 135, 0, 250, true, false, "particle.png", "spark", "ricochet", "metal", "bullet");
        add("Flame Thrower Stream", "Combat & Weapons", "Volatile burning fuel stream", 1500, 600, 250, 600, 1200, 16, 36, 200, 400, -15, 15, 0, -50, true, true, "particle.png", "flame", "fire", "stream", "weapon");
        add("Grenade Shrapnel", "Combat & Weapons", "High-explosive fragmentation spray", 500, 350, 200, 250, 500, 8, 18, 300, 650, 0, 360, 0, 200, true, false, "particle.png", "shrapnel", "grenade", "explosion");
        add("Sword Slash Arc", "Combat & Weapons", "Melee weapon trailing blade swoosh", 300, 200, 180, 150, 300, 8, 20, 80, 180, 0, 180, 0, 0, true, false, "particle.png", "slash", "sword", "melee", "trail");
        add("Critical Hit Sparks", "Combat & Weapons", "Golden starburst combat critical indicator", 450, 250, 150, 250, 450, 10, 24, 180, 380, 0, 360, 0, 0, true, false, "particle.png", "crit", "golden", "spark", "rpg");
        add("Arrow Trail", "Combat & Weapons", "Whistling aerodynamic flight trail", 800, 120, 60, 300, 600, 4, 8, 20, 50, 175, 185, 0, 0, false, true, "particle.png", "arrow", "bow", "trail", "wind");
        add("Missile Smoke Trail", "Combat & Weapons", "Dense spiraling white rocket smoke", 1400, 400, 150, 600, 1200, 12, 28, 40, 90, 170, 190, 0, 0, false, true, "particle.png", "smoke", "missile", "rocket", "trail");
        add("Laser Blaster Bolt", "Combat & Weapons", "Red plasma blaster energy projectile", 600, 160, 80, 200, 400, 8, 16, 350, 600, -5, 5, 0, 0, true, true, "particle.png", "laser", "blaster", "red", "weapon");
        add("Smoke Grenade Screen", "Combat & Weapons", "Dense expanding tactical fog screen", 3500, 500, 100, 1500, 3000, 24, 60, 30, 70, 0, 360, 10, -10, false, true, "particle.png", "smoke", "grenade", "tactical", "fog");
        add("Napalm Ground Fire", "Combat & Weapons", "Persistent burning liquid floor patch", 4000, 450, 120, 1000, 2500, 14, 30, 40, 100, 70, 110, 0, -80, true, true, "particle.png", "napalm", "fire", "ground", "burn");
        add("Shock Stun Taser", "Combat & Weapons", "Paralyzing yellow high-voltage discharge", 600, 220, 130, 200, 450, 6, 12, 100, 250, 0, 360, 0, 0, true, true, "particle.png", "taser", "stun", "electric", "yellow");
        add("Armor Shatter Debris", "Combat & Weapons", "Metallic plating armor break fragments", 600, 180, 100, 300, 600, 8, 18, 150, 350, 45, 135, 0, 350, false, false, "particle.png", "armor", "shatter", "debris", "break");
        add("Sniper Bullet Vapor", "Combat & Weapons", "Long high-speed supersonic cone wake", 500, 200, 120, 200, 450, 4, 12, 600, 1200, -2, 2, 0, 0, false, false, "particle.png", "sniper", "vapor", "supersonic", "trace");

        // --- 3. Magic & Fantasy (20 Presets) ---
        add("Fireball Blast", "Magic & Fantasy", "Classic wizard fireball detonation", 900, 450, 160, 400, 850, 16, 36, 180, 360, 0, 360, 0, -40, true, false, "particle.png", "fireball", "magic", "fire", "explosion");
        add("Frost Nova", "Magic & Fantasy", "Expanding ring of freezing ice shards", 1000, 380, 140, 500, 950, 10, 22, 160, 320, 0, 360, 0, 0, true, false, "particle.png", "frost", "ice", "nova", "blue");
        add("Healing Aura", "Magic & Fantasy", "Ascending green vitality orbs", 1800, 220, 70, 800, 1600, 8, 18, 40, 90, 75, 105, 0, -20, true, true, "particle.png", "healing", "green", "aura", "holy");
        add("Dark Necromancy Soul", "Magic & Fantasy", "Floating spectral souls with purple smoke", 2200, 320, 80, 1000, 2000, 12, 26, 30, 80, 60, 120, 10, -30, true, true, "particle.png", "dark", "soul", "necromancy", "purple");
        add("Arcane Runes Swirl", "Magic & Fantasy", "Orbiting glowing magical glyph fragments", 2500, 260, 60, 1200, 2200, 8, 16, 50, 100, 0, 360, 0, 0, true, true, "particle.png", "arcane", "runes", "magic", "cyan");
        add("Dragon Breath Flame", "Magic & Fantasy", "Massive conical mythic inferno", 2000, 700, 280, 800, 1700, 20, 48, 220, 480, -25, 25, 0, -40, true, true, "particle.png", "dragon", "fire", "breath", "inferno");
        add("Holy Radiance Pillar", "Magic & Fantasy", "Divine golden light blessing shaft", 2400, 380, 90, 1000, 2200, 14, 30, 60, 140, 80, 100, 0, -10, true, true, "particle.png", "holy", "divine", "gold", "light");
        add("Poison Spore Mist", "Magic & Fantasy", "Toxic lingering venom cloud", 3000, 350, 75, 1200, 2600, 16, 34, 20, 60, 0, 360, 5, 0, false, true, "particle.png", "poison", "toxic", "spores", "green");
        add("Lightning Storm Strike", "Magic & Fantasy", "Vertical thunderstorm electric thunderbolt", 400, 280, 200, 150, 350, 6, 14, 200, 600, 260, 280, 0, 0, true, false, "particle.png", "lightning", "storm", "thunder", "electric");
        add("Fairy Dust Sparkles", "Magic & Fantasy", "Glittering multi-colored enchanted dust", 2000, 240, 70, 900, 1800, 6, 12, 20, 50, 0, 360, 0, -10, true, true, "particle.png", "fairy", "glitter", "sparkle", "dust");
        add("Meteor Strike Impact", "Magic & Fantasy", "Celestial burning boulder ground crater", 1200, 550, 220, 500, 1100, 18, 42, 250, 500, 30, 150, 0, 180, true, false, "particle.png", "meteor", "impact", "fire", "crater");
        add("Shadow Tendrils", "Magic & Fantasy", "Writhing black void tendrils", 1800, 300, 85, 700, 1500, 10, 24, 40, 100, 0, 360, 0, 0, false, true, "particle.png", "shadow", "void", "dark", "tendrils");
        add("Mystic Teleport Portal", "Magic & Fantasy", "Rotating interdimensional gateway vortex", 2800, 450, 110, 1000, 2400, 12, 28, 80, 180, 0, 360, 0, 0, true, true, "particle.png", "portal", "teleport", "mystic", "blue");
        add("Solar Flare Magic", "Magic & Fantasy", "Radiant sunfire sphere eruption", 1400, 420, 130, 600, 1200, 16, 32, 140, 300, 0, 360, 0, 0, true, false, "particle.png", "sun", "solar", "fire", "magic");
        add("Moonbeam Glow", "Magic & Fantasy", "Gentle silvery lunar shimmer", 2600, 200, 50, 1200, 2400, 6, 14, 15, 35, 260, 280, 0, 0, true, true, "particle.png", "moon", "lunar", "silver", "gentle");
        add("Chaos Orb Detonation", "Magic & Fantasy", "Rainbow unpredictable chaos explosion", 800, 400, 170, 350, 750, 12, 28, 200, 420, 0, 360, 0, 0, true, false, "particle.png", "chaos", "rainbow", "magic", "orb");
        add("Blood Ritual Circle", "Magic & Fantasy", "Crimson occult runes pulsing on the floor", 3200, 340, 70, 1400, 2800, 8, 18, 20, 50, 0, 360, 0, 0, true, true, "particle.png", "blood", "ritual", "occult", "crimson");
        add("Blizzard Vortex", "Magic & Fantasy", "Violent swirling arctic storm", 2200, 500, 160, 800, 1800, 8, 20, 180, 380, 170, 210, 100, 0, false, true, "particle.png", "blizzard", "ice", "snow", "wind");
        add("Mana Siphon Stream", "Magic & Fantasy", "Blue energy wisps returning to the caster", 1600, 260, 80, 700, 1400, 6, 14, -120, -250, 0, 360, 0, 0, true, true, "particle.png", "mana", "siphon", "blue", "magic");
        add("Earthquake Dust", "Magic & Fantasy", "Stomp shockwave with flying dirt rocks", 1100, 300, 120, 450, 950, 12, 26, 120, 260, 30, 150, 0, 350, false, false, "particle.png", "earth", "rock", "dust", "quake");

        // --- 4. Nature & Weather (16 Presets) ---
        add("Gentle Rain", "Nature & Weather", "Steady vertical raindrops", 1200, 350, 150, 500, 1000, 4, 8, 300, 550, 265, 275, 20, 0, false, true, "particle.png", "rain", "weather", "water", "nature");
        add("Snow Blizzard", "Nature & Weather", "Soft drifting snowflakes with wind flutter", 2500, 400, 90, 1200, 2400, 6, 14, 60, 140, 240, 280, 40, 0, false, true, "particle.png", "snow", "winter", "weather", "cold");
        add("Falling Autumn Leaves", "Nature & Weather", "Swaying orange and brown leaves falling", 3500, 180, 35, 1800, 3200, 10, 22, 40, 90, 250, 290, 60, 20, false, true, "particle.png", "leaves", "autumn", "fall", "nature");
        add("Campfire Embers", "Nature & Weather", "Rising glowing wood embers and orange sparks", 2200, 280, 75, 900, 2000, 6, 14, 50, 120, 75, 105, 10, -40, true, true, "particle.png", "campfire", "embers", "wood", "sparks");
        add("Waterfall Mist", "Nature & Weather", "Dense white water vapor rising from rapids", 2000, 420, 130, 800, 1700, 16, 38, 40, 90, 70, 110, 15, -20, false, true, "particle.png", "waterfall", "mist", "water", "nature");
        add("Summer Fireflies", "Nature & Weather", "Nighttime pulsing warm bioluminescent bugs", 3500, 150, 25, 1600, 3200, 8, 16, 15, 35, 0, 360, 0, 0, true, true, "particle.png", "fireflies", "bugs", "night", "glow");
        add("Desert Sandstorm", "Nature & Weather", "Fast horizontal sweeping dust wall", 1800, 550, 180, 700, 1500, 8, 20, 350, 650, 175, 185, 0, 0, false, true, "particle.png", "sand", "desert", "storm", "wind");
        add("Underwater Bubbles", "Nature & Weather", "Buoyant rising aeration air bubbles", 2800, 240, 60, 1200, 2500, 6, 16, 40, 100, 80, 100, 0, -60, false, true, "particle.png", "bubbles", "water", "ocean", "swim");
        add("Volcanic Ash Cloud", "Nature & Weather", "Dark oppressive billowing ash plumes", 3000, 450, 90, 1400, 2800, 20, 50, 30, 80, 60, 120, 20, -10, false, true, "particle.png", "volcano", "ash", "cloud", "dark");
        add("Morning Dew Drops", "Nature & Weather", "Glistening sunlight droplets on grass", 2400, 160, 40, 1000, 2200, 4, 10, 5, 15, 0, 360, 0, 0, true, true, "particle.png", "dew", "morning", "drop", "glisten");
        add("Wind Dust Swirl", "Nature & Weather", "Twirling mini dust tornado vortex", 1800, 320, 90, 700, 1600, 8, 18, 100, 220, 0, 360, 30, -50, false, true, "particle.png", "dust", "wind", "tornado", "swirl");
        add("Lava Bubble Burst", "Nature & Weather", "Hot magma pops flinging red lava droplets", 1000, 220, 80, 400, 900, 10, 24, 80, 180, 60, 120, 0, 250, true, true, "particle.png", "lava", "magma", "bubble", "hot");
        add("Spring Pollen Cloud", "Nature & Weather", "Drifting yellow floral pollen in gentle breeze", 3200, 200, 35, 1500, 3000, 4, 10, 20, 45, 160, 200, 15, 0, false, true, "particle.png", "pollen", "spring", "yellow", "nature");
        add("Geyser Steam Jet", "Nature & Weather", "Pressurized boiling geothermal water blast", 1600, 480, 160, 600, 1400, 14, 32, 250, 500, 85, 95, 0, 50, false, true, "particle.png", "geyser", "steam", "water", "hot");
        add("Ocean Wave Foam", "Nature & Weather", "White sea spray breaking on rocky cliffs", 1400, 380, 110, 500, 1200, 10, 22, 120, 260, 45, 135, 0, 200, false, true, "particle.png", "ocean", "wave", "foam", "spray");
        add("Dandelion Seeds", "Nature & Weather", "Floating white dandelion parachutes in wind", 4000, 120, 20, 2000, 3800, 8, 16, 25, 60, 170, 210, 25, -10, false, true, "particle.png", "dandelion", "seed", "wind", "nature");

        // --- 5. Explosions & Impacts (14 Presets) ---
        add("Fiery Bomb Explosion", "Explosions & Impacts", "Classic blockbuster spherical fire blast", 1000, 600, 280, 400, 900, 20, 48, 200, 450, 0, 360, 0, 50, true, false, "particle.png", "explosion", "bomb", "fire", "blast");
        add("Nuclear Mushroom Head", "Explosions & Impacts", "Towering apocalyptic atomic cloud cap", 2500, 750, 200, 1000, 2400, 30, 70, 80, 180, 60, 120, 0, -60, false, false, "particle.png", "nuke", "mushroom", "atomic", "smoke");
        add("Debris Ground Crater", "Explosions & Impacts", "Flying asphalt rocks and dirt chunks", 900, 350, 150, 350, 800, 10, 26, 180, 400, 30, 150, 0, 450, false, false, "particle.png", "debris", "crater", "rock", "dirt");
        add("Shockwave Ring", "Explosions & Impacts", "Expanding transparent blast ring distortion", 600, 400, 220, 250, 550, 12, 28, 300, 600, 0, 360, 0, 0, true, false, "particle.png", "shockwave", "ring", "blast", "wave");
        add("Cluster Bomb Submunition", "Explosions & Impacts", "Secondary popping fiery mini-explosions", 800, 300, 160, 300, 700, 12, 26, 150, 350, 0, 360, 0, 100, true, false, "particle.png", "cluster", "bomb", "explosion", "fire");
        add("Glass Shatter Window", "Explosions & Impacts", "Reflective sharp glass shard explosion", 700, 240, 120, 300, 650, 6, 16, 160, 380, 0, 360, 0, 380, false, false, "particle.png", "glass", "shatter", "window", "debris");
        add("Wood Splinter Barrel", "Explosions & Impacts", "Wooden crate destroying brown splinters", 800, 220, 110, 350, 750, 6, 18, 140, 320, 20, 160, 0, 350, false, false, "particle.png", "wood", "splinters", "crate", "barrel");
        add("Flak Anti-Air Burst", "Explosions & Impacts", "Black flak smoke burst high in the sky", 1200, 380, 140, 500, 1100, 18, 40, 60, 140, 0, 360, 0, 0, false, false, "particle.png", "flak", "antiair", "smoke", "black");
        add("Fireworks Rocket Peony", "Explosions & Impacts", "Colorful celebration fireworks burst", 1500, 500, 200, 600, 1400, 8, 18, 180, 380, 0, 360, 0, 80, true, false, "particle.png", "fireworks", "peony", "celebration", "color");
        add("Sparks Shower Fountain", "Explosions & Impacts", "Cascading golden pyrotechnic fountain", 2000, 450, 130, 700, 1800, 6, 14, 180, 360, 70, 110, 0, 300, true, true, "particle.png", "fountain", "sparks", "gold", "pyro");
        add("Dynamite Mine Blast", "Explosions & Impacts", "Underground rock and orange fire flash", 900, 420, 180, 350, 850, 14, 32, 220, 460, 0, 360, 0, 200, true, false, "particle.png", "dynamite", "mining", "blast", "rock");
        add("C4 Breaching Charge", "Explosions & Impacts", "Directional wall-breaching dust explosion", 700, 360, 170, 250, 650, 16, 36, 250, 500, -45, 45, 0, 100, false, false, "particle.png", "c4", "breach", "wall", "tactical");
        add("Steam Boiler Rupture", "Explosions & Impacts", "High-pressure white scalded vapor burst", 1100, 480, 190, 450, 1000, 20, 44, 180, 380, 0, 360, 0, -30, false, false, "particle.png", "steam", "boiler", "rupture", "white");
        add("Stomp Shockwave Crater", "Explosions & Impacts", "Radial monster ground slam shockwave", 800, 380, 160, 300, 750, 12, 28, 200, 440, 0, 360, 0, 150, false, false, "particle.png", "stomp", "ground", "slam", "shockwave");

        // --- 6. Atmospheric & Ambient (14 Presets) ---
        add("Torch Wall Flame", "Atmospheric & Ambient", "Steady flickering dungeon wall torch", 1400, 220, 65, 500, 1200, 10, 22, 40, 90, 80, 100, 5, -50, true, true, "particle.png", "torch", "dungeon", "fire", "ambient");
        add("Candle Flame Tiny", "Atmospheric & Ambient", "Delicate micro candle flame flickers", 1000, 80, 25, 350, 800, 4, 10, 15, 35, 85, 95, 0, -30, true, true, "particle.png", "candle", "tiny", "light", "ambient");
        add("Chimney Roof Smoke", "Atmospheric & Ambient", "Slow gray billowing house chimney smoke", 3500, 250, 40, 1500, 3200, 16, 38, 25, 60, 75, 105, 30, -20, false, true, "particle.png", "chimney", "smoke", "house", "village");
        add("Sewer Toxic Vapors", "Atmospheric & Ambient", "Greenish miasma rising from street manhole", 2800, 200, 45, 1100, 2500, 14, 30, 20, 50, 70, 110, 5, -15, false, true, "particle.png", "sewer", "toxic", "green", "vapor");
        add("Industrial Pipe Steam", "Atmospheric & Ambient", "High velocity white factory exhaust steam", 1600, 350, 90, 600, 1400, 12, 26, 120, 240, 160, 200, 0, -20, false, true, "particle.png", "factory", "steam", "industrial", "pipe");
        add("Swamp Bioluminescent Spores", "Atmospheric & Ambient", "Eerie glowing blue swamp vegetation spores", 3200, 220, 40, 1400, 3000, 6, 14, 15, 40, 0, 360, 0, -10, true, true, "particle.png", "swamp", "spores", "blue", "glowing");
        add("Magic Crystal Dust", "Atmospheric & Ambient", "Glittering aura around enchanted crystal shard", 2200, 180, 50, 900, 2000, 6, 12, 20, 45, 0, 360, 0, 0, true, true, "particle.png", "crystal", "magic", "dust", "glitter");
        add("Stardust Space Ambient", "Atmospheric & Ambient", "Slow twinkling galactic dust field", 4000, 300, 45, 2000, 4000, 4, 10, 5, 20, 0, 360, 0, 0, true, true, "particle.png", "stardust", "space", "stars", "twinkle");
        add("Aurora Borealis Curtains", "Atmospheric & Ambient", "Waving neon green northern lights ribbons", 3800, 400, 60, 1800, 3600, 24, 55, 10, 30, 80, 100, 40, 0, true, true, "particle.png", "aurora", "borealis", "green", "lights");
        add("Acid Drip Puddle", "Atmospheric & Ambient", "Periodic dripping green caustic droplets", 1500, 100, 30, 500, 1200, 6, 12, 100, 220, 265, 275, 0, 250, false, true, "particle.png", "acid", "drip", "green", "liquid");
        add("Cobweb Dust Motes", "Atmospheric & Ambient", "Abandoned dusty manor sunlight motes", 3500, 140, 25, 1600, 3200, 4, 8, 5, 15, 0, 360, 0, 0, false, true, "particle.png", "dust", "motes", "sunlight", "cobweb");
        add("Energy Conduit Sparks", "Atmospheric & Ambient", "Occasional short-circuit electrical pop", 1200, 140, 45, 300, 800, 4, 10, 80, 200, 0, 360, 0, 100, true, true, "particle.png", "conduit", "sparks", "electric", "short");
        add("Smoldering Ash Embers", "Atmospheric & Ambient", "Warm glowing burnt ruins floor embers", 2600, 220, 50, 1100, 2400, 4, 10, 15, 40, 70, 110, 0, -20, true, true, "particle.png", "smolder", "ruins", "ash", "embers");
        add("Bioluminescent Algae", "Atmospheric & Ambient", "Soft cyan waves glowing in ocean shoreline", 3000, 280, 55, 1300, 2800, 8, 18, 10, 30, 0, 360, 0, 0, true, true, "particle.png", "algae", "bioluminescent", "cyan", "ocean");
    }

    private static void add(String name, String category, String description,
                            float duration, int maxCount, float emissionRate,
                            float lifeMin, float lifeMax, float scaleMin, float scaleMax,
                            float velMin, float velMax, float angleMin, float angleMax,
                            float wind, float gravity, boolean additive, boolean continuous,
                            String imagePath, String... tags) {
        PRESETS.add(new ParticlePreset(name, category, description, duration, maxCount, emissionRate,
                lifeMin, lifeMax, scaleMin, scaleMax, velMin, velMax, angleMin, angleMax,
                wind, gravity, additive, continuous, imagePath, tags));
    }

    public static List<ParticlePreset> getAllPresets() {
        return Collections.unmodifiableList(PRESETS);
    }

    public static List<String> getCategories() {
        return PRESETS.stream().map(ParticlePreset::getCategory).distinct().sorted().collect(Collectors.toList());
    }

    public static List<ParticlePreset> search(String category, String query) {
        String q = query != null ? query.toLowerCase().trim() : "";
        return PRESETS.stream().filter(p -> {
            boolean matchCategory = (category == null || category.equals("All Categories") || p.getCategory().equalsIgnoreCase(category));
            if (!matchCategory) return false;
            if (q.isEmpty()) return true;
            return p.getName().toLowerCase().contains(q)
                    || p.getDescription().toLowerCase().contains(q)
                    || p.getTags().stream().anyMatch(t -> t.toLowerCase().contains(q));
        }).collect(Collectors.toList());
    }
}
