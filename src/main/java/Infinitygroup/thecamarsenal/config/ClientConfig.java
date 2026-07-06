package infinitygroup.thecamarsenal.config;

import org.apache.commons.lang3.tuple.Pair;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    public enum AmmoHudPositionMode {
        ABOVE_SELECTED_SLOT,
        BOTTOM_RIGHT
    }

    public static final ClientConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        Pair<ClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(ClientConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.BooleanValue enableGunParticles;
    public final ModConfigSpec.BooleanValue enableGunTracer;
    public final ModConfigSpec.BooleanValue enableMuzzleFlash;
    public final ModConfigSpec.BooleanValue enableImpactParticles;
    public final ModConfigSpec.BooleanValue enableEntityHitParticles;
    public final ModConfigSpec.BooleanValue enableShellEjection;
    public final ModConfigSpec.DoubleValue muzzleFlashIntensity;
    public final ModConfigSpec.DoubleValue tracerIntensity;
    public final ModConfigSpec.IntValue tracerMaxParticles;
    public final ModConfigSpec.DoubleValue muzzleForwardOffset;
    public final ModConfigSpec.DoubleValue entityHitParticleIntensity;
    public final ModConfigSpec.BooleanValue entityHitTopDownMode;
    public final ModConfigSpec.BooleanValue entityHitUpperBodyBurst;
    public final ModConfigSpec.DoubleValue akm47ShootVolume;
    public final ModConfigSpec.BooleanValue enableGunSounds;
    public final ModConfigSpec.BooleanValue enableRecoilVisual;
    public final ModConfigSpec.BooleanValue enableExternalGunSkins;
    public final ModConfigSpec.BooleanValue enableGunAmmoHud;
    public final ModConfigSpec.IntValue ammoHudOffsetX;
    public final ModConfigSpec.IntValue ammoHudOffsetY;
    public final ModConfigSpec.BooleanValue ammoHudDrawBackground;
    public final ModConfigSpec.ConfigValue<String> ammoHudPositionMode;

    private ClientConfig(ModConfigSpec.Builder builder) {
        builder.push("client");
        enableGunParticles = builder.comment("Render gun particles and trail effects.")
                .define("enableGunParticles", true);
        enableGunTracer = builder.comment("Render tracer effects for gunfire.")
                .define("enableGunTracer", true);
        enableMuzzleFlash = builder.comment("Render muzzle flash effects when firing guns.")
                .define("enableMuzzleFlash", true);
        enableImpactParticles = builder.comment("Render impact particles when shots hit.")
                .define("enableImpactParticles", true);
        enableEntityHitParticles = builder.comment("Render impact particles when shots hit entities.")
                .define("enableEntityHitParticles", true);
        enableShellEjection = builder.comment("Render lightweight shell ejection effects.")
                .define("enableShellEjection", true);
        muzzleFlashIntensity = builder.comment("Overall strength of muzzle flash particles.")
                .defineInRange("muzzleFlashIntensity", 0.6D, 0.0D, 2.0D);
        tracerIntensity = builder.comment("Overall strength of tracer particles.")
                .defineInRange("tracerIntensity", 1.2D, 0.1D, 3.0D);
        tracerMaxParticles = builder.comment("Maximum number of tracer particles per shot.")
                .defineInRange("tracerMaxParticles", 40, 4, 128);
        muzzleForwardOffset = builder.comment("Forward offset from the aim origin to place the muzzle visual.")
                .defineInRange("muzzleForwardOffset", 1.0D, 0.25D, 3.0D);
        entityHitParticleIntensity = builder.comment("Overall strength of entity hit impact particles.")
                .defineInRange("entityHitParticleIntensity", 3.0D, 0.5D, 6.0D);
        entityHitTopDownMode = builder.comment("Use a stronger vertical entity hit impact tuned for high or top-down cameras.")
                .define("entityHitTopDownMode", true);
        entityHitUpperBodyBurst = builder.comment("Add a secondary burst above the body for entity hit impacts.")
                .define("entityHitUpperBodyBurst", true);
        akm47ShootVolume = builder.comment("Volume multiplier for the AKM-47 shoot sound.")
                .defineInRange("akm47ShootVolume", 1.2D, 0.0D, 4.0D);
        enableGunSounds = builder.comment("Play local gun sound effects.")
                .define("enableGunSounds", true);
        enableRecoilVisual = builder.comment("Apply first-person recoil transforms.")
                .define("enableRecoilVisual", true);
        enableExternalGunSkins = builder.comment("Load gun skins from the external config folder.")
                .define("enableExternalGunSkins", true);
        enableGunAmmoHud = builder.comment("Enable the ammo HUD.")
                .define("enableGunAmmoHud", true);
        ammoHudOffsetX = builder.comment("Horizontal offset from the right edge for the ammo HUD in bottom-right mode.")
                .defineInRange("ammoHudOffsetX", 12, 0, 200);
        ammoHudOffsetY = builder.comment("Vertical offset from the bottom edge for the ammo HUD in bottom-right mode.")
                .defineInRange("ammoHudOffsetY", 28, 0, 200);
        ammoHudDrawBackground = builder.comment("Draw a subtle background behind the ammo HUD text.")
                .define("ammoHudDrawBackground", true);
        ammoHudPositionMode = builder.comment("Ammo HUD positioning mode.")
                .define("ammoHudPositionMode", AmmoHudPositionMode.BOTTOM_RIGHT.name());
        builder.pop();
    }

    public AmmoHudPositionMode ammoHudPositionMode() {
        String value = ammoHudPositionMode.get();
        try {
            return AmmoHudPositionMode.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return AmmoHudPositionMode.BOTTOM_RIGHT;
        }
    }
}
