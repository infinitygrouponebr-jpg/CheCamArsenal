package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.aim.TheCamCompat;
import infinitygroup.thecamarsenal.config.CommonConfig;
import infinitygroup.thecamarsenal.network.ShootGunPayload;
import infinitygroup.thecamarsenal.weapon.GunDefinition;
import infinitygroup.thecamarsenal.weapon.GunShotHandler;
import infinitygroup.thecamarsenal.weapon.GunStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class GunShootClientPayloadHelper {
    private GunShootClientPayloadHelper() {
    }

    public static void sendShootPayload(GunDefinition definition, InteractionHand hand) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft == null ? null : minecraft.player;
        if (minecraft == null || player == null || minecraft.level == null) {
            return;
        }

        GunStats stats = GunShotHandler.resolveStats(definition);
        boolean theCamActive = TheCamCompat.isAimActive(player);
        boolean hasTheCamAim = theCamActive;
        boolean hasAimTarget = false;
        Vec3 aimOrigin;
        Vec3 aimDirection;
        Vec3 aimTarget;

        if (theCamActive) {
            aimOrigin = sanitizeVec(TheCamCompat.getAimOrigin(player), player.getEyePosition(1.0F));
            aimDirection = sanitizeDirection(TheCamCompat.getAimDirection(player), fallbackDirection(player));
            hasAimTarget = TheCamCompat.hasAimTarget(player);
            aimTarget = sanitizeVec(TheCamCompat.getAimTarget(player), aimOrigin.add(aimDirection.scale(stats.range())));
        } else {
            aimOrigin = player.getEyePosition(1.0F);
            aimDirection = fallbackDirection(player);
            aimTarget = aimOrigin.add(aimDirection.scale(stats.range()));
        }

        ShootGunPayload payload = new ShootGunPayload(
                definition.id(),
                hand == InteractionHand.MAIN_HAND,
                hasTheCamAim,
                hasAimTarget,
                aimOrigin.x,
                aimOrigin.y,
                aimOrigin.z,
                aimDirection.x,
                aimDirection.y,
                aimDirection.z,
                aimTarget.x,
                aimTarget.y,
                aimTarget.z,
                minecraft.level.getGameTime());

        if (CommonConfig.INSTANCE.enableDebugAim.get()) {
            TheCamArsenal.LOGGER.info(
                    "ARSENAL_CLIENT_SHOOT_PAYLOAD weaponId={} hand={} hasTheCamAim={} hasAimTarget={} origin={} direction={} target={} clientGameTime={}",
                    definition.id(),
                    payload.hand(),
                    payload.hasTheCamAim(),
                    payload.hasAimTarget(),
                    aimOrigin,
                    aimDirection,
                    aimTarget,
                    payload.clientGameTime());
        }

        PacketDistributor.sendToServer(payload);
    }

    private static Vec3 fallbackDirection(LocalPlayer player) {
        Vec3 direction = Vec3.directionFromRotation(player.getXRot(), player.getYRot());
        if (isFinite(direction) && direction.lengthSqr() > 1.0E-6D) {
            return direction.normalize();
        }
        return new Vec3(0.0D, 0.0D, 1.0D);
    }

    private static Vec3 sanitizeVec(Vec3 candidate, Vec3 fallback) {
        return isFinite(candidate) ? candidate : fallback;
    }

    private static Vec3 sanitizeDirection(Vec3 candidate, Vec3 fallback) {
        if (!isFinite(candidate) || candidate.lengthSqr() <= 1.0E-6D) {
            return fallback;
        }
        return candidate.normalize();
    }

    private static boolean isFinite(Vec3 vec) {
        return vec != null && Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }
}
