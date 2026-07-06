package infinitygroup.thecamarsenal.weapon;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.aim.AimProvider;
import infinitygroup.thecamarsenal.aim.ArsenalAimManager;
import infinitygroup.thecamarsenal.aim.TheCamAimProvider;
import infinitygroup.thecamarsenal.config.CommonConfig;
import infinitygroup.thecamarsenal.network.GunShotVisualPayload;
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

    private GunShotHandler() {
    }

    public static boolean tryFire(ServerPlayer player, ItemStack stack, GunDefinition definition) {
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
        AimProvider aimProvider = ArsenalAimManager.getAimProvider(player);
        boolean theCamLoaded = TheCamAimProvider.isTheCamLoaded();
        boolean theCamActive = aimProvider == TheCamAimProvider.INSTANCE && aimProvider.isTheCamAimActive(player);
        GunStats stats = resolveStats(definition);

        Vec3 cameraOrigin = aimProvider.getAimOrigin(player);
        Vec3 cameraDirection = normalizeOrDefault(aimProvider.getAimDirection(player));
        Vec3 shotOrigin = resolveShotOrigin(player, aimProvider, cameraOrigin, theCamActive);
        Vec3 aimTarget = aimProvider.getAimTarget(player);
        boolean hasAimTarget = theCamActive && aimProvider.hasAimTarget(player);
        boolean aimTargetValid = isValidAimTarget(shotOrigin, aimTarget, stats);
        Vec3 fallbackDirection = normalizeOrDefault(Vec3.directionFromRotation(player.getXRot(), player.getYRot()));
        boolean usedTheCamAim = theCamActive && aimTargetValid;

        Vec3 shotDirection;
        String fallbackReason = null;
        if (usedTheCamAim) {
            shotDirection = aimTarget.subtract(shotOrigin).normalize();
        } else {
            if (theCamLoaded && theCamActive && !aimTargetValid) {
                fallbackReason = "INVALID_AIM_TARGET";
            } else if (theCamLoaded && !theCamActive) {
                fallbackReason = "THE_CAM_INACTIVE";
            } else if (!theCamLoaded) {
                fallbackReason = "THE_CAM_NOT_LOADED";
            } else {
                fallbackReason = "VANILLA_FALLBACK";
            }

            aimTarget = shotOrigin.add(fallbackDirection.scale(stats.range()));
            shotDirection = fallbackDirection;
        }

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

        HitResult hitResult = GunRaycast.raycast(level, player, shotOrigin, shotDirection, stats.range());
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
            String providerName = theCamLoaded ? "THE_CAM_MOUSE_FOLLOW" : "VANILLA_AIM_PROVIDER";
            String hitDescription = switch (hitType) {
                case GunShotVisualPayload.HIT_ENTITY -> "ENTITY";
                case GunShotVisualPayload.HIT_BLOCK -> "BLOCK";
                default -> "MISS";
            };
            String targetName = hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof net.minecraft.world.phys.EntityHitResult entityHitResult
                    ? entityHitResult.getEntity().getType().toShortString()
                    : hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof net.minecraft.world.phys.BlockHitResult blockHitResult
                            ? blockHitResult.getBlockPos().toString()
                            : "none";
            if (usedTheCamAim) {
                TheCamArsenal.LOGGER.info(
                        "ARSENAL_AIM_PROVIDER_SELECTED provider={} active={} hasTarget={} origin={} direction={} target={}",
                        providerName,
                        theCamActive,
                        hasAimTarget,
                        cameraOrigin,
                        cameraDirection,
                        aimTarget);
                TheCamArsenal.LOGGER.info(
                        "ARSENAL_SHOT_USING_THECAM_MOUSE_FOLLOW shotOrigin={} aimTarget={} shotDirection={}",
                        shotOrigin,
                        aimTarget,
                        shotDirection);
            } else if (theCamLoaded) {
                TheCamArsenal.LOGGER.info(
                        "ARSENAL_SHOT_FALLBACK reason={} provider={} cameraOrigin={} cameraDirection={} aimTarget={} shotOrigin={} shotDirection={} hitPos={} hitType={} distance={} target={} player={}",
                        fallbackReason,
                        providerName,
                        cameraOrigin,
                        cameraDirection,
                        aimTarget,
                        shotOrigin,
                        shotDirection,
                        hitPoint,
                        hitDescription,
                        hitPoint.distanceTo(shotOrigin),
                        targetName,
                        player.getGameProfile().getName());
            }
        }

        GunCooldownManager.applyCooldown(player, stack.getItem(), stats.cooldownTicks());
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new GunShotVisualPayload(player.getId(), hitEntityId,
                        aimProvider == TheCamAimProvider.INSTANCE ? GunShotVisualPayload.AIM_PROVIDER_THE_CAM : GunShotVisualPayload.AIM_PROVIDER_VANILLA,
                        hasAimTarget,
                        definition.shootSound().getId().toString(),
                        cameraOrigin.x, cameraOrigin.y, cameraOrigin.z,
                        shotOrigin.x, shotOrigin.y, shotOrigin.z,
                        aimTarget.x, aimTarget.y, aimTarget.z,
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

    private static boolean isWeaponEnabled(String weaponId) {
        return switch (weaponId) {
            case "scarm" -> CommonConfig.INSTANCE.enableScarm.get();
            case "akm_47" -> CommonConfig.INSTANCE.enableAkm47.get();
            default -> true;
        };
    }

    private static Vec3 resolveShotOrigin(ServerPlayer player, AimProvider aimProvider, Vec3 cameraOrigin, boolean theCamActive) {
        if (!theCamActive) {
            return aimProvider.getAimOrigin(player);
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

    private static boolean isValidAimTarget(Vec3 shotOrigin, Vec3 aimTarget, GunStats stats) {
        if (!isFinite(shotOrigin) || !isFinite(aimTarget) || stats == null) {
            return false;
        }

        double distance = shotOrigin.distanceTo(aimTarget);
        return distance >= AIM_TARGET_MIN_DISTANCE;
    }

    private static boolean isFinite(Vec3 vec) {
        return vec != null
                && Double.isFinite(vec.x)
                && Double.isFinite(vec.y)
                && Double.isFinite(vec.z);
    }
}
