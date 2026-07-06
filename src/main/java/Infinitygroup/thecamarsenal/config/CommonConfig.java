package infinitygroup.thecamarsenal.config;

import org.apache.commons.lang3.tuple.Pair;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class CommonConfig {
    public static final CommonConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        Pair<CommonConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.BooleanValue enableAkm47;
    public final ModConfigSpec.DoubleValue akm47Damage;
    public final ModConfigSpec.DoubleValue akm47Range;
    public final ModConfigSpec.IntValue akm47CooldownTicks;
    public final ModConfigSpec.IntValue akm47InternalAmmoCapacity;
    public final ModConfigSpec.IntValue akm47ReloadTicks;
    public final ModConfigSpec.BooleanValue enableScarm;
    public final ModConfigSpec.DoubleValue scarmDamage;
    public final ModConfigSpec.DoubleValue scarmRange;
    public final ModConfigSpec.IntValue scarmCooldownTicks;
    public final ModConfigSpec.IntValue scarmInternalAmmoCapacity;
    public final ModConfigSpec.IntValue scarmReloadTicks;
    public final ModConfigSpec.BooleanValue enableFriendlyFire;
    public final ModConfigSpec.BooleanValue enableDebugAim;
    public final ModConfigSpec.DoubleValue clientAimPayloadMaxAngleDegrees;
    public final ModConfigSpec.ConfigValue<String> theCamAimOriginMode;
    public final ModConfigSpec.BooleanValue requireAmmoForGuns;
    public final ModConfigSpec.BooleanValue creativeGunsIgnoreAmmo;
    public final ModConfigSpec.BooleanValue enableReloadSystem;

    private CommonConfig(ModConfigSpec.Builder builder) {
        builder.push("general");
        enableAkm47 = builder.comment("Enable the AKM-47 item and firing behavior.")
                .define("enableAkm47", true);
        akm47Damage = builder.comment("Damage dealt by the AKM-47.")
                .defineInRange("akm47Damage", 7.0D, 0.0D, 1000.0D);
        akm47Range = builder.comment("Maximum raycast range for the AKM-47.")
                .defineInRange("akm47Range", 80.0D, 1.0D, 1024.0D);
        akm47CooldownTicks = builder.comment("Cooldown in ticks after firing the AKM-47.")
                .defineInRange("akm47CooldownTicks", 4, 0, 200);
        akm47InternalAmmoCapacity = builder.comment("Internal ammo capacity of the AKM-47.")
                .defineInRange("akm47InternalAmmoCapacity", 30, 1, 1000);
        akm47ReloadTicks = builder.comment("Reload time in ticks for the AKM-47.")
                .defineInRange("akm47ReloadTicks", 40, 0, 2000);
        enableScarm = builder.comment("Enable the Scarm item and firing behavior.")
                .define("enableScarm", true);
        scarmDamage = builder.comment("Damage dealt by the Scarm.")
                .defineInRange("scarmDamage", 8.0D, 0.0D, 1000.0D);
        scarmRange = builder.comment("Maximum raycast range for the Scarm.")
                .defineInRange("scarmRange", 90.0D, 1.0D, 1024.0D);
        scarmCooldownTicks = builder.comment("Cooldown in ticks after firing the Scarm.")
                .defineInRange("scarmCooldownTicks", 5, 0, 200);
        scarmInternalAmmoCapacity = builder.comment("Internal ammo capacity of the Scarm.")
                .defineInRange("scarmInternalAmmoCapacity", 20, 1, 1000);
        scarmReloadTicks = builder.comment("Reload time in ticks for the Scarm.")
                .defineInRange("scarmReloadTicks", 45, 0, 2000);
        enableFriendlyFire = builder.comment("Allow the AKM-47 to damage other players.")
                .define("enableFriendlyFire", false);
        enableDebugAim = builder.comment("Log aim origin/direction details for debugging.")
                .define("enableDebugAim", false);
        clientAimPayloadMaxAngleDegrees = builder.comment("Maximum allowed angle in degrees between client-shipped aim direction and server-side player view direction.")
                .defineInRange("clientAimPayloadMaxAngleDegrees", 120.0D, 0.0D, 180.0D);
        theCamAimOriginMode = builder.comment("The Cam aim origin mode: PLAYER_EYE or CAMERA.")
                .define("theCamAimOriginMode", "PLAYER_EYE");
        requireAmmoForGuns = builder.comment("Require ammunition for rifle firing.")
                .define("requireAmmoForGuns", true);
        creativeGunsIgnoreAmmo = builder.comment("Allow creative players to fire rifles without consuming ammunition.")
                .define("creativeGunsIgnoreAmmo", true);
        enableReloadSystem = builder.comment("Enable manual reload with R.")
                .define("enableReloadSystem", true);
        builder.pop();
    }

    public boolean isTheCamAimOriginPlayerEye() {
        return "PLAYER_EYE".equalsIgnoreCase(theCamAimOriginMode.get());
    }
}
