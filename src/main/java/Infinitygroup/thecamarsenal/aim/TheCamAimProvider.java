package infinitygroup.thecamarsenal.aim;

import infinitygroup.thecamarsenal.config.CommonConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class TheCamAimProvider implements AimProvider {
    public static final TheCamAimProvider INSTANCE = new TheCamAimProvider();

    private TheCamAimProvider() {
    }

    public static boolean isTheCamLoaded() {
        return TheCamCompat.isInstalled();
    }

    @Override
    public Vec3 getAimOrigin(Player player) {
        if (CommonConfig.INSTANCE.isTheCamAimOriginPlayerEye()) {
            return player.getEyePosition(1.0F);
        }
        return TheCamCompat.getAimOrigin(player);
    }

    @Override
    public Vec3 getAimDirection(Player player) {
        return TheCamCompat.getAimDirection(player);
    }

    @Override
    public Vec3 getAimTarget(Player player) {
        Vec3 target = TheCamCompat.getAimTarget(player);
        if (target != null) {
            return target;
        }
        Vec3 origin = getAimOrigin(player);
        return origin.add(getAimDirection(player).normalize().scale(96.0D));
    }

    @Override
    public boolean hasAimTarget(Player player) {
        return TheCamCompat.hasAimTarget(player);
    }

    @Override
    public boolean isTheCamAimActive(Player player) {
        return TheCamCompat.isAimActive(player);
    }
}
