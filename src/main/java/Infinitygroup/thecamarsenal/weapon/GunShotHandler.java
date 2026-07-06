package infinitygroup.thecamarsenal.weapon;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.aim.AimProvider;
import infinitygroup.thecamarsenal.aim.ArsenalAimManager;
import infinitygroup.thecamarsenal.aim.TheCamAimProvider;
import infinitygroup.thecamarsenal.config.CommonConfig;
import infinitygroup.thecamarsenal.network.GunShotVisualPayload;
import infinitygroup.thecamarsenal.network.ShootGunPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public final class GunShotHandler {
    private static final double AIM_TARGET_MIN_DISTANCE = 0.25D;
    private static final double AIM_TARGET_MAX_EXTRA_DISTANCE = 4.0D;

    private GunShotHandler() {
    }

    public static boolean tryFire(ServerPlayer player, ItemStack stack, GunDefinition definition) {
        return tryFire(player, stack, definition, null);
    }

    public static boolean tryFire(ServerPlayer player, ItemStack stack, GunDefinition definition, ShootGunPayload payload) {
        if (!isWeaponEnabled(definition.id())) {
            return false;
        }

        if (player.isSpectator()) {
            return false;
        }

        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }

        if (GunReloadManager.isReloading(player)) {
            return false;
        }

        ServerLevel level = player.serverLevel();
        GunStats stats = resolveStats(definition);
        ResolvedAim aim = resolveAim(player, stats, payload);

        GunAmmoHelper.ensureAmmoInitialized(stack, definition);
        int currentAmmo = GunAmmoHelper.getAmmo(stack, definition);
        if (currentAmmo <= 0) {
            player.displayClientMessage(Component.translatable("message.thecamarsenal.no_ammo_reload"), true);
            return false;
        }

        if (!player.getAbilities().instabuild) {
            GunAmmoHelper.setAmmo(stack, definition, currentAmmo - 1);
            player.getInventory().setChanged();
        }

        HitResult hitResult = GunRaycast.raycast(level, player, aim.shotOrigin(), aim.shotDirection(), stats.range());
        Vec3 hitPoint = hitResult.getLocation();
        byte hitType = GunShotVisualPayload.HIT_MISS;
        int hitEntityId = -1;

        if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof net.minecraft.world.phys.EntityHitResult entityHitResult) {
            hitType = GunShotVisualPayload.HIT_ENTITY;
            Entity target = entityHitResult.getEntity();
            hitEntityId = target.getId();
            if (target instanceof Player targetPlayer && !CommonConfig.INSTANCE.enableFriendlyFire.get() && !player.canHarmPlayer(targetPlayer)) {
                // Friendly fire is disabled. We still play the shot, but skip damage.
            } else {
                target.hurt(level.damageSources().playerAttack(player), (float) stats.damage());
            }
        } else if (hitResult.getType() == HitResult.Type.BLOCK) {
            hitType = GunShotVisualPayload.HIT_BLOCK;
        }

        if (CommonConfig.INSTANCE.enableDebugAim.get()) {
            logAimResolution(player, stats, aim, hitPoint, hitType);
        }

        GunCooldownManager.applyCooldown(player, stack.getItem(), stats.cooldownTicks());
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new GunShotVisualPayload(player.getId(), hitEntityId, aim.visualProvider(), aim.hasAimTarget(),
                        definition.shootSound().getId().toString(),
                        aim.cameraOrigin().x, aim.cameraOrigin().y, aim.cameraOrigin().z,
                        aim.shotOrigin().x, aim.shotOrigin().y, aim.shotOrigin().z,
                        aim.aimTarget().x, aim.aimTarget().y, aim.aimTarget().z,
                        hitPoint.x, hitPoint.y, hitPoint.z, hitType));

        return true;
    }

    public static GunStats resolveStats(GunDefinition definition) {
        return switch (definition.id()) {
            case "scarm" -> new GunStats(
                    CommonConfig.INSTANCE.scarmDamage.get(),
                    CommonConfig.INSTANCE.scarmRange.get(),
                    CommonConfig.INSTANCE.scarmCooldownTicks.get());
            case "akm_47" -> new GunStats(
                    CommonConfig.INSTANCE.akm47Damage.get(),
                    CommonConfig.INSTANCE.akm47Range.get(),
                    CommonConfig.INSTANCE.akm47CooldownTicks.get());
            default -> new GunStats(definition.defaultDamage(), definition.defaultRange(), definition.defaultCooldownTicks());
        };
    }

    private static ResolvedAim resolveAim(ServerPlayer player, GunStats stats, ShootGunPayload payload) {
        if (payload != null) {
            if (payload.hasTheCamAim()) {
                if (validateClientPayload(player, stats, payload) == null) {
                    Vec3 cameraOrigin = sanitizeVec(payload.aimOrigin(), player.getEyePosition(1.0F));
                    Vec3 aimTarget = sanitizeVec(payload.aimTarget(), cameraOrigin.add(normalizeOrDefault(payload.aimDirection()).scale(stats.range())));
                    Vec3 shotOrigin = resolveShotOrigin(player, cameraOrigin, true);
                    Vec3 shotDirection = normalizeOrDefault(aimTarget.subtract(shotOrigin));
                    return new ResolvedAim(
                            AimSource.CLIENT_PAYLOAD,
                            cameraOrigin,
                            shotOrigin,
                            shotDirection,
                            aimTarget,
                            payload.hasAimTarget(),
                            GunShotVisualPayload.AIM_PROVIDER_THE_CAM,
                            null);
                }
            }
        }

        ResolvedAim serverStoreAim = resolveServerStoreAim(player, stats);
        if (serverStoreAim != null) {
            return serverStoreAim;
        }

        if (payload != null && payload.hasTheCamAim()) {
            return resolveFallbackAim(player, stats, "INVALID_CLIENT_AIM_PAYLOAD");
        }
        if (payload != null) {
            return resolveFallbackAim(player, stats, "CLIENT_PAYLOAD_VANILLA_AIM");
        }
        return resolveFallbackAim(player, stats, "SERVER_THECAM_AIM_STORE_UNAVAILABLE");
    }

    private static ResolvedAim resolveServerStoreAim(ServerPlayer player, GunStats stats) {
        AimProvider aimProvider = ArsenalAimManager.getAimProvider(player);
        boolean theCamLoaded = TheCamAimProvider.isTheCamLoaded();
        boolean theCamActive = aimProvider == TheCamAimProvider.INSTANCE && aimProvider.isTheCamAimActive(player);
        if (!theCamLoaded || !theCamActive) {
            return null;
        }

        Vec3 cameraOrigin = sanitizeVec(aimProvider.getAimOrigin(player), player.getEyePosition(1.0F));
        Vec3 aimTarget = sanitizeVec(aimProvider.getAimTarget(player), cameraOrigin.add(normalizeOrDefault(aimProvider.getAimDirection(player)).scale(stats.range())));
        Vec3 shotOrigin = resolveShotOrigin(player, cameraOrigin, true);
        String validationReason = validateAimTarget(shotOrigin, aimTarget, stats);
        if (validationReason != null) {
            return null;
        }

        return new ResolvedAim(
                AimSource.SERVER_STORE,
                cameraOrigin,
                shotOrigin,
                normalizeOrDefault(aimTarget.subtract(shotOrigin)),
                aimTarget,
                aimProvider.hasAimTarget(player),
                GunShotVisualPayload.AIM_PROVIDER_THE_CAM,
                null);
    }

    private static ResolvedAim resolveFallbackAim(ServerPlayer player, GunStats stats, String reason) {
        Vec3 cameraOrigin = player.getEyePosition(1.0F);
        Vec3 shotOrigin = cameraOrigin;
        Vec3 shotDirection = normalizeOrDefault(Vec3.directionFromRotation(player.getXRot(), player.getYRot()));
        Vec3 aimTarget = shotOrigin.add(shotDirection.scale(stats.range()));
        return new ResolvedAim(
                AimSource.VANILLA_FALLBACK,
                cameraOrigin,
                shotOrigin,
                shotDirection,
                aimTarget,
                false,
                GunShotVisualPayload.AIM_PROVIDER_VANILLA,
                reason);
    }

    private static String validateClientPayload(ServerPlayer player, GunStats stats, ShootGunPayload payload) {
        if (!isFinite(payload.aimOrigin()) || !isFinite(payload.aimDirection()) || !isFinite(payload.aimTarget())) {
            return "INVALID_CLIENT_AIM_PAYLOAD_NON_FINITE";
        }

        Vec3 rawDirection = payload.aimDirection();
        if (rawDirection.lengthSqr() <= 1.0E-6D) {
            return "INVALID_CLIENT_AIM_PAYLOAD_DIRECTION";
        }
        Vec3 payloadDirection = rawDirection.normalize();

        Vec3 shotOrigin = resolveShotOrigin(player, payload.aimOrigin(), true);
        String targetValidation = validateAimTarget(shotOrigin, payload.aimTarget(), stats);
        if (targetValidation != null) {
            return "INVALID_CLIENT_AIM_PAYLOAD_" + targetValidation;
        }

        Vec3 expectedDirection = normalizeOrDefault(Vec3.directionFromRotation(player.getXRot(), player.getYRot()));
        double maxAngleDegrees = CommonConfig.INSTANCE.clientAimPayloadMaxAngleDegrees.get();
        if (maxAngleDegrees >= 0.0D && maxAngleDegrees < 180.0D) {
            double dot = clamp(payloadDirection.dot(expectedDirection), -1.0D, 1.0D);
            double threshold = Math.cos(Math.toRadians(maxAngleDegrees));
            if (dot < threshold) {
                return "INVALID_CLIENT_AIM_PAYLOAD_ANGLE";
            }
        }

        return null;
    }

    private static String validateAimTarget(Vec3 shotOrigin, Vec3 aimTarget, GunStats stats) {
        if (!isFinite(shotOrigin) || !isFinite(aimTarget) || stats == null) {
            return "INVALID_AIM_TARGET";
        }

        double distance = shotOrigin.distanceTo(aimTarget);
        if (distance < AIM_TARGET_MIN_DISTANCE) {
            return "AIM_TARGET_TOO_CLOSE";
        }
        if (distance > stats.range() + AIM_TARGET_MAX_EXTRA_DISTANCE) {
            return "AIM_TARGET_TOO_FAR";
        }
        return null;
    }

    private static void logAimResolution(ServerPlayer player, GunStats stats, ResolvedAim aim, Vec3 hitPoint, byte hitType) {
        String providerName = aim.source() == AimSource.VANILLA_FALLBACK ? "VANILLA_AIM_PROVIDER" : "THE_CAM_MOUSE_FOLLOW";

        String hitDescription = switch (hitType) {
            case GunShotVisualPayload.HIT_ENTITY -> "ENTITY";
            case GunShotVisualPayload.HIT_BLOCK -> "BLOCK";
            default -> "MISS";
        };

        if (aim.source() == AimSource.CLIENT_PAYLOAD) {
            TheCamArsenal.LOGGER.info(
                    "ARSENAL_AIM_PROVIDER_SELECTED provider={} active={} hasTarget={} origin={} direction={} target={}",
                    providerName,
                    true,
                    aim.hasAimTarget(),
                    aim.cameraOrigin(),
                    aim.shotDirection(),
                    aim.aimTarget());
            TheCamArsenal.LOGGER.info(
                    "ARSENAL_SHOT_USING_CLIENT_THECAM_MOUSE_FOLLOW_PAYLOAD shotOrigin={} aimTarget={} shotDirection={}",
                    aim.shotOrigin(),
                    aim.aimTarget(),
                    aim.shotDirection());
        } else if (aim.source() == AimSource.SERVER_STORE) {
            TheCamArsenal.LOGGER.info(
                    "ARSENAL_AIM_PROVIDER_SELECTED provider={} active={} hasTarget={} origin={} direction={} target={}",
                    providerName,
                    true,
                    aim.hasAimTarget(),
                    aim.cameraOrigin(),
                    aim.shotDirection(),
                    aim.aimTarget());
            TheCamArsenal.LOGGER.info(
                    "ARSENAL_SHOT_USING_SERVER_THECAM_AIM_STORE shotOrigin={} aimTarget={} shotDirection={}",
                    aim.shotOrigin(),
                    aim.aimTarget(),
                    aim.shotDirection());
        } else {
            TheCamArsenal.LOGGER.info(
                    "ARSENAL_SHOT_FALLBACK reason={} provider={} cameraOrigin={} shotOrigin={} shotDirection={} aimTarget={} hitPos={} hitType={} distance={} player={} range={}",
                    aim.fallbackReason(),
                    providerName,
                    aim.cameraOrigin(),
                    aim.shotOrigin(),
                    aim.shotDirection(),
                    aim.aimTarget(),
                    hitPoint,
                    hitDescription,
                    hitPoint.distanceTo(aim.shotOrigin()),
                    player.getGameProfile().getName(),
                    stats.range());
        }
    }

    private static Vec3 resolveShotOrigin(ServerPlayer player, Vec3 cameraOrigin, boolean useTheCamAim) {
        if (!useTheCamAim) {
            return player.getEyePosition(1.0F);
        }
        if (CommonConfig.INSTANCE.isTheCamAimOriginPlayerEye()) {
            return player.getEyePosition(1.0F);
        }
        return cameraOrigin;
    }

    private static Vec3 normalizeOrDefault(Vec3 vector) {
        if (vector == null || !isFinite(vector) || vector.lengthSqr() <= 1.0E-6D) {
            return new Vec3(0.0D, 0.0D, 1.0D);
        }
        return vector.normalize();
    }

    private static Vec3 sanitizeVec(Vec3 candidate, Vec3 fallback) {
        return isFinite(candidate) ? candidate : fallback;
    }

    private static boolean isWeaponEnabled(String weaponId) {
        return switch (weaponId) {
            case "scarm" -> CommonConfig.INSTANCE.enableScarm.get();
            case "akm_47" -> CommonConfig.INSTANCE.enableAkm47.get();
            default -> true;
        };
    }

    private static boolean isFinite(Vec3 vec) {
        return vec != null
                && Double.isFinite(vec.x)
                && Double.isFinite(vec.y)
                && Double.isFinite(vec.z);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private enum AimSource {
        CLIENT_PAYLOAD,
        SERVER_STORE,
        VANILLA_FALLBACK
    }

    private record ResolvedAim(AimSource source, Vec3 cameraOrigin, Vec3 shotOrigin, Vec3 shotDirection, Vec3 aimTarget,
            boolean hasAimTarget, byte visualProvider, String fallbackReason) {
    }
}
