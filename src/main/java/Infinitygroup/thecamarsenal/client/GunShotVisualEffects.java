package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.config.ClientConfig;
import infinitygroup.thecamarsenal.network.GunShotVisualPayload;
import infinitygroup.thecamarsenal.registry.ArsenalSounds;
import infinitygroup.thecamarsenal.config.CommonConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class GunShotVisualEffects {
    private static final int DEBUG_MAX_SEGMENTS = 32;
    private static final DustParticleOptions MUZZLE_FLASH = new DustParticleOptions(new Vector3f(1.0F, 0.86F, 0.35F), 0.45F);
    private static final DustParticleOptions TRACER_BRIGHT = new DustParticleOptions(new Vector3f(0.96F, 0.86F, 0.45F), 0.45F);
    private static final DustParticleOptions TRACER_SOFT = new DustParticleOptions(new Vector3f(0.78F, 0.76F, 0.70F), 0.32F);
    private static final DustParticleOptions ENTITY_HIT_RED_DARK = new DustParticleOptions(new Vector3f(0.75F, 0.01F, 0.01F), 1.35F);
    private static final DustParticleOptions ENTITY_HIT_RED_BRIGHT = new DustParticleOptions(new Vector3f(1.0F, 0.04F, 0.02F), 1.20F);
    private static final DustParticleOptions ENTITY_HIT_RED_GREY = new DustParticleOptions(new Vector3f(0.35F, 0.02F, 0.02F), 1.10F);
    private static final DustParticleOptions DEBUG_AIM_CYAN = new DustParticleOptions(new Vector3f(0.10F, 0.95F, 1.0F), 0.85F);
    private static final DustParticleOptions DEBUG_AIM_RED = new DustParticleOptions(new Vector3f(1.0F, 0.20F, 0.20F), 0.95F);
    private static final DustParticleOptions DEBUG_AIM_YELLOW = new DustParticleOptions(new Vector3f(1.0F, 0.92F, 0.25F), 0.95F);
    private static final DustParticleOptions DEBUG_AIM_GREEN = new DustParticleOptions(new Vector3f(0.15F, 1.0F, 0.20F), 0.95F);
    private static final DustParticleOptions DEBUG_AIM_BLUE = new DustParticleOptions(new Vector3f(0.20F, 0.45F, 1.0F), 0.95F);

    private GunShotVisualEffects() {
    }

    public static void spawn(ClientLevel level, Entity shooter, GunShotVisualPayload payload) {
        Vec3 cameraOrigin = new Vec3(payload.cameraOriginX(), payload.cameraOriginY(), payload.cameraOriginZ());
        Vec3 origin = new Vec3(payload.originX(), payload.originY(), payload.originZ());
        Vec3 aimTarget = new Vec3(payload.aimTargetX(), payload.aimTargetY(), payload.aimTargetZ());
        Vec3 hitPos = new Vec3(payload.hitX(), payload.hitY(), payload.hitZ());
        Vec3 travel = hitPos.subtract(origin);
        Vec3 direction = travel.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 0.0D, 1.0D) : travel.normalize();
        double muzzleForwardOffset = ClientConfig.INSTANCE.muzzleForwardOffset.get();
        Vec3 muzzlePos = origin.add(direction.scale(muzzleForwardOffset));

        if (ClientConfig.INSTANCE.enableGunSounds.get()) {
            ResourceLocation soundId = ResourceLocation.parse(payload.shootSoundId());
            SoundEvent shootSound = BuiltInRegistries.SOUND_EVENT.getOptional(soundId).orElse(ArsenalSounds.AKM_47_SHOOT.get());
            float volume = ClientConfig.INSTANCE.akm47ShootVolume.get().floatValue();
            float pitch = 0.96F + level.getRandom().nextFloat() * 0.08F;
            level.playLocalSound(muzzlePos.x, muzzlePos.y, muzzlePos.z, shootSound, SoundSource.PLAYERS,
                    volume, pitch, false);
        }

        if (ClientConfig.INSTANCE.enableGunParticles.get()) {
            if (ClientConfig.INSTANCE.enableMuzzleFlash.get()) {
                spawnMuzzleFlash(level, muzzlePos, direction);
            }

            if (ClientConfig.INSTANCE.enableGunTracer.get()) {
                spawnTracer(level, muzzlePos, hitPos);
            }

            if (payload.isBlockHit() && ClientConfig.INSTANCE.enableImpactParticles.get()) {
                spawnBlockImpact(level, hitPos);
            } else if (payload.isEntityHit() && ClientConfig.INSTANCE.enableImpactParticles.get()) {
                spawnEntityImpact(level, payload, hitPos, direction);
            }

        if (CommonConfig.INSTANCE.enableDebugAim.get()) {
            spawnDebugCameraLine(level, cameraOrigin, aimTarget, payload.aimProvider(), payload.hasAimTarget());
            spawnDebugShotLine(level, origin, hitPos, payload.aimProvider());
            spawnDebugMarker(level, aimTarget, debugAimMarker(payload.aimProvider(), payload.hasAimTarget(), true));
            spawnDebugMarker(level, hitPos, debugAimMarker(payload.aimProvider(), payload.hasAimTarget(), false));
            }

            if (ClientConfig.INSTANCE.enableShellEjection.get()) {
                spawnShellEjection(level, shooter, muzzlePos, direction);
            }
        }
    }

    private static void spawnMuzzleFlash(ClientLevel level, Vec3 origin, Vec3 direction) {
        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 side = direction.cross(up);
        if (side.lengthSqr() < 1.0E-6D) {
            side = new Vec3(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize();
        Vec3 vertical = side.cross(direction).normalize();

        double flashIntensity = ClientConfig.INSTANCE.muzzleFlashIntensity.get();
        int particles = Mth.clamp((int) Math.ceil(3.0D * flashIntensity), 2, 4);
        for (int i = 0; i < particles; i++) {
            double spread = 0.01D + (i * 0.006D);
            Vec3 pos = origin
                    .add(side.scale((i % 2 == 0 ? 1.0D : -1.0D) * spread))
                    .add(vertical.scale((i < 2 ? 1.0D : -1.0D) * spread));
            level.addParticle(MUZZLE_FLASH, pos.x, pos.y, pos.z, direction.x * 0.01D, direction.y * 0.01D, direction.z * 0.01D);
        }
        level.addParticle(ParticleTypes.FLAME, origin.x, origin.y, origin.z, direction.x * 0.005D, direction.y * 0.005D, direction.z * 0.005D);
        level.addParticle(ParticleTypes.SMOKE, origin.x, origin.y, origin.z, 0.0D, 0.005D, 0.0D);
    }

    private static void spawnTracer(ClientLevel level, Vec3 start, Vec3 end) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length < 0.001D) {
            return;
        }

        double tracerIntensity = ClientConfig.INSTANCE.tracerIntensity.get();
        int maxParticles = ClientConfig.INSTANCE.tracerMaxParticles.get();
        int steps = Mth.clamp((int) Math.ceil(length / 0.85D), 6, maxParticles);
        double startBoost = 1.25D * tracerIntensity;
        double endBoost = 0.75D * tracerIntensity;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / (double) steps;
            Vec3 pos = start.lerp(end, t);
            double strength = Mth.lerp((float) t, (float) startBoost, (float) endBoost);
            level.addParticle(TRACER_BRIGHT, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
            if (strength > 0.9D) {
                level.addParticle(TRACER_BRIGHT, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
            } else if (strength > 0.6D) {
                level.addParticle(TRACER_SOFT, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
            }
            if (i % 4 == 0) {
                level.addParticle(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static void spawnBlockImpact(ClientLevel level, Vec3 hitPos) {
        BlockPos pos = BlockPos.containing(hitPos);
        BlockState state = level.getBlockState(pos);
        level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), hitPos.x, hitPos.y, hitPos.z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.SMOKE, hitPos.x, hitPos.y, hitPos.z, 0.0D, 0.02D, 0.0D);
        level.addParticle(ParticleTypes.SMALL_FLAME, hitPos.x, hitPos.y, hitPos.z, 0.0D, 0.01D, 0.0D);
    }

    private static void spawnEntityImpact(ClientLevel level, GunShotVisualPayload payload, Vec3 hitPos, Vec3 direction) {
        if (!ClientConfig.INSTANCE.enableEntityHitParticles.get()) {
            return;
        }

        if (!ClientConfig.INSTANCE.entityHitTopDownMode.get()) {
            spawnLegacyEntityImpact(level, payload, hitPos, direction);
            return;
        }

        double intensity = Mth.clamp(ClientConfig.INSTANCE.entityHitParticleIntensity.get(), 0.5D, 6.0D);
        ImpactAnchor anchor = resolveImpactAnchor(level, payload.hitEntityId(), hitPos);
        Vec3 shotDirection = direction.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 1.0D, 0.0D) : direction.normalize();
        Vec3 back = shotDirection.scale(-0.18D);

        spawnEntityBurst(level, anchor.torsoPos(), back,
                countForIntensity(28, intensity, 28, 36), countForIntensity(12, intensity, 12, 16), countForIntensity(6, intensity, 6, 8), 4, 2,
                0.12D, 0.04D, 0.18D, 0.65D, 0.14D, true);

        if (ClientConfig.INSTANCE.entityHitUpperBodyBurst.get()) {
            spawnEntityBurst(level, anchor.upperPos(), back,
                    countForIntensity(16, intensity, 16, 22), countForIntensity(6, intensity, 6, 8), countForIntensity(3, intensity, 3, 3), 2, 0,
                    0.09D, 0.02D, 0.14D, 0.50D, 0.10D, false);
        }

        spawnVerticalBurst(level, anchor.upperPos(), back, countForIntensity(10, intensity, 10, 14), 0.22D, 0.04D);
        spawnImpactRing(level, anchor.torsoPos(), intensity);

        if (anchor.fallbackApplied()) {
            level.addParticle(ParticleTypes.CRIT, anchor.torsoPos().x, anchor.torsoPos().y, anchor.torsoPos().z,
                    back.x * 0.10D, back.y * 0.12D + 0.04D, back.z * 0.10D);
        }
    }

    private static void spawnLegacyEntityImpact(ClientLevel level, GunShotVisualPayload payload, Vec3 hitPos, Vec3 direction) {
        double intensity = Mth.clamp(ClientConfig.INSTANCE.entityHitParticleIntensity.get(), 0.5D, 6.0D);
        ImpactAnchor anchor = resolveImpactAnchor(level, payload.hitEntityId(), hitPos);
        Vec3 shotDirection = direction.lengthSqr() < 1.0E-6D ? new Vec3(0.0D, 1.0D, 0.0D) : direction.normalize();
        Vec3 back = shotDirection.scale(-0.14D);

        spawnEntityBurst(level, anchor.torsoPos(), back,
                countForIntensity(18, intensity, 18, 24), countForIntensity(8, intensity, 8, 12), countForIntensity(4, intensity, 4, 5), 2, 1,
                0.10D, 0.03D, 0.14D, 0.55D, 0.10D, false);
        spawnEntityBurst(level, anchor.upperPos(), back,
                countForIntensity(8, intensity, 8, 12), countForIntensity(4, intensity, 4, 6), countForIntensity(2, intensity, 2, 2), 1, 0,
                0.08D, 0.02D, 0.10D, 0.45D, 0.08D, false);
    }

    private static void spawnEntityBurst(ClientLevel level, Vec3 center, Vec3 back,
            int dustCount, int critCount, int damageCount, int poofCount, int smokeCount,
            double xzSpread, double yMin, double yMax, double velocityBackScale, double velocitySideScale,
            boolean alternateColor) {
        for (int i = 0; i < dustCount; i++) {
            Vec3 spread = new Vec3(
                    randomRange(level, -xzSpread, xzSpread),
                    randomRange(level, yMin, yMax),
                    randomRange(level, -xzSpread, xzSpread));
            DustParticleOptions dust = pickDust(i, alternateColor);
            Vec3 velocity = back.scale(velocityBackScale).add(new Vec3(
                    randomRange(level, -velocitySideScale, velocitySideScale),
                    randomRange(level, 0.02D, 0.12D),
                    randomRange(level, -velocitySideScale, velocitySideScale)));
            level.addParticle(dust,
                    center.x + spread.x, center.y + spread.y, center.z + spread.z,
                    velocity.x, velocity.y, velocity.z);
        }

        for (int i = 0; i < critCount; i++) {
            Vec3 spread = new Vec3(
                    randomRange(level, -xzSpread * 0.75D, xzSpread * 0.75D),
                    randomRange(level, yMin * 0.5D, yMax),
                    randomRange(level, -xzSpread * 0.75D, xzSpread * 0.75D));
            Vec3 velocity = back.scale(velocityBackScale * 0.8D).add(new Vec3(
                    randomRange(level, -velocitySideScale * 0.75D, velocitySideScale * 0.75D),
                    randomRange(level, 0.03D, 0.14D),
                    randomRange(level, -velocitySideScale * 0.75D, velocitySideScale * 0.75D)));
            level.addParticle(ParticleTypes.CRIT,
                    center.x + spread.x, center.y + spread.y, center.z + spread.z,
                    velocity.x, velocity.y, velocity.z);
        }

        for (int i = 0; i < damageCount; i++) {
            Vec3 offset = new Vec3(
                    randomRange(level, -xzSpread * 0.5D, xzSpread * 0.5D),
                    randomRange(level, 0.0D, yMax * 0.55D),
                    randomRange(level, -xzSpread * 0.5D, xzSpread * 0.5D));
            level.addParticle(ParticleTypes.DAMAGE_INDICATOR,
                    center.x + offset.x, center.y + offset.y, center.z + offset.z,
                    0.0D, 0.01D, 0.0D);
        }

        for (int i = 0; i < smokeCount; i++) {
            Vec3 offset = new Vec3(
                    randomRange(level, -xzSpread * 0.45D, xzSpread * 0.45D),
                    randomRange(level, 0.01D, yMin * 0.4D + 0.04D),
                    randomRange(level, -xzSpread * 0.45D, xzSpread * 0.45D));
            level.addParticle(ParticleTypes.SMOKE,
                    center.x + offset.x, center.y + offset.y, center.z + offset.z,
                    back.x * 0.25D, back.y * 0.25D + 0.01D, back.z * 0.25D);
        }

        for (int i = 0; i < poofCount; i++) {
            Vec3 offset = new Vec3(
                    randomRange(level, -xzSpread * 0.35D, xzSpread * 0.35D),
                    randomRange(level, 0.0D, yMax * 0.45D + 0.02D),
                    randomRange(level, -xzSpread * 0.35D, xzSpread * 0.35D));
            level.addParticle(ParticleTypes.POOF,
                    center.x + offset.x, center.y + offset.y, center.z + offset.z,
                    back.x * 0.12D, back.y * 0.12D + 0.01D, back.z * 0.12D);
        }
    }

    private static void spawnVerticalBurst(ClientLevel level, Vec3 center, Vec3 back, int count, double yVelocity, double yOffset) {
        for (int i = 0; i < count; i++) {
            Vec3 offset = new Vec3(
                    randomRange(level, -0.08D, 0.08D),
                    randomRange(level, 0.04D, 0.16D) + yOffset,
                    randomRange(level, -0.08D, 0.08D));
            level.addParticle(i % 2 == 0 ? ENTITY_HIT_RED_BRIGHT : ENTITY_HIT_RED_DARK,
                    center.x + offset.x, center.y + offset.y, center.z + offset.z,
                    randomRange(level, -0.04D, 0.04D),
                    randomRange(level, yVelocity * 0.5D, yVelocity),
                    randomRange(level, -0.04D, 0.04D));
        }
    }

    private static void spawnImpactRing(ClientLevel level, Vec3 center, double intensity) {
        int ringParticles = 12;
        double radiusBase = Mth.lerp((float) Mth.clamp(intensity / 6.0D, 0.0D, 1.0D), 0.25F, 0.45F);
        for (int i = 0; i < ringParticles; i++) {
            double angle = (Math.PI * 2.0D * i) / ringParticles;
            double radius = radiusBase + randomRange(level, -0.04D, 0.04D);
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            double y = center.y + randomRange(level, 0.0D, 0.15D);
            Vec3 outward = new Vec3(Math.cos(angle), randomRange(level, 0.02D, 0.06D), Math.sin(angle)).scale(0.07D);
            level.addParticle(ENTITY_HIT_RED_GREY, x, y, z, outward.x, outward.y, outward.z);
        }
    }

    private static ImpactAnchor resolveImpactAnchor(ClientLevel level, int hitEntityId, Vec3 hitPos) {
        Entity target = hitEntityId >= 0 ? level.getEntity(hitEntityId) : null;
        if (target == null) {
            Vec3 torsoPos = hitPos.add(0.0D, 0.28D, 0.0D);
            Vec3 upperPos = hitPos.add(0.0D, 0.42D, 0.0D);
            return new ImpactAnchor(torsoPos, upperPos, false);
        }

        double minY = target.getBoundingBox().minY;
        double maxY = target.getBoundingBox().maxY;
        double height = Math.max(0.001D, maxY - minY);
        double relativeY = (hitPos.y - minY) / height;
        double torsoY = minY + height * 0.58D;
        double upperY = minY + height * 0.78D;
        double hitY = Mth.clamp(hitPos.y, minY + height * 0.45D, minY + height * 0.85D);
        boolean fallbackApplied = relativeY < 0.35D;

        if (fallbackApplied) {
            hitY = torsoY;
        }

        Vec3 torsoPos = new Vec3(target.getX(), hitY, target.getZ());
        Vec3 upperPos = new Vec3(target.getX(), upperY, target.getZ());
        return new ImpactAnchor(torsoPos, upperPos, fallbackApplied);
    }

    private static double randomRange(ClientLevel level, double min, double max) {
        return Mth.lerp(level.getRandom().nextDouble(), min, max);
    }

    private static DustParticleOptions pickDust(int index, boolean alternateColor) {
        if (!alternateColor) {
            return index % 3 == 0 ? ENTITY_HIT_RED_DARK : ENTITY_HIT_RED_BRIGHT;
        }
        return switch (index % 3) {
            case 0 -> ENTITY_HIT_RED_DARK;
            case 1 -> ENTITY_HIT_RED_BRIGHT;
            default -> ENTITY_HIT_RED_GREY;
        };
    }

    private static int countForIntensity(int baseCount, double intensity, int minCount, int maxCount) {
        return Mth.clamp((int) Math.round(baseCount * (intensity / 3.0D)), minCount, maxCount);
    }

    private record ImpactAnchor(Vec3 torsoPos, Vec3 upperPos, boolean fallbackApplied) {
    }

    private static void spawnShellEjection(ClientLevel level, Entity shooter, Vec3 origin, Vec3 direction) {
        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = direction.cross(up);
        if (right.lengthSqr() < 1.0E-6D) {
            right = new Vec3(1.0D, 0.0D, 0.0D);
        }
        right = right.normalize();
        double side = shooter instanceof Player player && player.getMainArm() == HumanoidArm.RIGHT ? 1.0D : -1.0D;
        Vec3 spawn = origin.add(right.scale(0.18D * side)).add(up.scale(-0.08D));
        level.addParticle(ParticleTypes.SMOKE, spawn.x, spawn.y, spawn.z, right.x * 0.02D * side, 0.03D, right.z * 0.02D * side);
    }

    private static void spawnDebugShotLine(ClientLevel level, Vec3 shotOrigin, Vec3 hitPos, byte provider) {
        drawLine(level, shotOrigin, hitPos, debugShotDust(provider));
    }

    private static void spawnDebugCameraLine(ClientLevel level, Vec3 cameraOrigin, Vec3 aimTarget, byte provider, boolean hasAimTarget) {
        if (provider != GunShotVisualPayload.AIM_PROVIDER_THE_CAM || !hasAimTarget || !isFinite(cameraOrigin) || !isFinite(aimTarget)) {
            return;
        }
        drawLine(level, cameraOrigin, aimTarget, DEBUG_AIM_CYAN);
    }

    private static void drawLine(ClientLevel level, Vec3 start, Vec3 end, DustParticleOptions dust) {
        Vec3 delta = end.subtract(start);
        double length = delta.length();
        if (length < 0.001D) {
            return;
        }

        int segments = Mth.clamp((int) Math.ceil(length / 0.45D), 4, DEBUG_MAX_SEGMENTS);
        Vec3 step = delta.scale(1.0D / (double) segments);
        Vec3 current = start;
        for (int i = 0; i <= segments; i++) {
            level.addParticle(dust, current.x, current.y, current.z, 0.0D, 0.02D, 0.0D);
            current = current.add(step);
        }
    }

    private static void spawnDebugMarker(ClientLevel level, Vec3 pos, DustParticleOptions dust) {
        if (!isFinite(pos)) {
            return;
        }
        level.addParticle(dust, pos.x, pos.y, pos.z, 0.0D, 0.04D, 0.0D);
    }

    private static DustParticleOptions debugShotDust(byte provider) {
        return switch (provider) {
            case GunShotVisualPayload.AIM_PROVIDER_THE_CAM -> DEBUG_AIM_GREEN;
            case GunShotVisualPayload.AIM_PROVIDER_ERROR -> DEBUG_AIM_YELLOW;
            default -> DEBUG_AIM_RED;
        };
    }

    private static DustParticleOptions debugAimMarker(byte provider, boolean hasAimTarget, boolean isAimTarget) {
        if (provider == GunShotVisualPayload.AIM_PROVIDER_THE_CAM && hasAimTarget) {
            return isAimTarget ? DEBUG_AIM_BLUE : DEBUG_AIM_GREEN;
        }
        return provider == GunShotVisualPayload.AIM_PROVIDER_ERROR ? DEBUG_AIM_YELLOW : DEBUG_AIM_RED;
    }

    private static boolean isFinite(Vec3 vec) {
        return vec != null && Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
    }

}




