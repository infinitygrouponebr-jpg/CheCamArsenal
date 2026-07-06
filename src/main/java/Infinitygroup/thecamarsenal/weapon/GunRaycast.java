package infinitygroup.thecamarsenal.weapon;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;

public final class GunRaycast {
    private GunRaycast() {
    }

    public static HitResult raycast(Level level, Entity shooter, Vec3 origin, Vec3 direction, double range) {
        Vec3 end = origin.add(direction.scale(range));
        BlockHitResult blockHit = level.clip(new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(level, shooter, origin, end,
                shooter.getBoundingBox().expandTowards(direction.scale(range)).inflate(1.0D),
                entity -> entity.isPickable() && entity != shooter);

        if (entityHit == null) {
            return blockHit;
        }

        if (blockHit.getType() != HitResult.Type.MISS
                && blockHit.getLocation().distanceToSqr(origin) < entityHit.getLocation().distanceToSqr(origin)) {
            return blockHit;
        }

        return entityHit;
    }
}
