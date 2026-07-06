package infinitygroup.thecamarsenal.aim;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class VanillaAimProvider implements AimProvider {
    public static final VanillaAimProvider INSTANCE = new VanillaAimProvider();

    private VanillaAimProvider() {
    }

    @Override
    public Vec3 getAimOrigin(Player player) {
        return player.getEyePosition(1.0F);
    }

    @Override
    public Vec3 getAimDirection(Player player) {
        return Vec3.directionFromRotation(player.getXRot(), player.getYRot());
    }

    @Override
    public Vec3 getAimTarget(Player player) {
        Vec3 direction = Vec3.directionFromRotation(player.getXRot(), player.getYRot());
        return player.getEyePosition(1.0F).add(direction.scale(96.0D));
    }

    @Override
    public boolean hasAimTarget(Player player) {
        return true;
    }

    @Override
    public boolean isTheCamAimActive(Player player) {
        return false;
    }
}
