package infinitygroup.thecamarsenal.weapon;

import infinitygroup.thecamarsenal.registry.ArsenalSounds;
import infinitygroup.thecamarsenal.weapon.ammo.AmmoType;

public final class GunDefinitions {
    public static final GunDefinition AKM_47 = new GunDefinition("akm_47", 7.0D, 80.0D, 4, AmmoType.MEDIUM_RIFLE,
            30, 40, ArsenalSounds.AKM_47_SHOOT);
    public static final GunDefinition SCARM = new GunDefinition("scarm", 8.0D, 90.0D, 5, AmmoType.MEDIUM_RIFLE,
            20, 45, ArsenalSounds.SCARM_SHOOT);

    private GunDefinitions() {
    }

    public static GunDefinition byId(String id) {
        return switch (id) {
            case "akm_47" -> AKM_47;
            case "scarm" -> SCARM;
            default -> null;
        };
    }
}
