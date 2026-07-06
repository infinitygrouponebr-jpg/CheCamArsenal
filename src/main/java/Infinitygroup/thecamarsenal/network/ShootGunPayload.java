package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;

public record ShootGunPayload(
        String weaponId,
        boolean mainHand,
        boolean hasTheCamAim,
        boolean hasAimTarget,
        double aimOriginX,
        double aimOriginY,
        double aimOriginZ,
        double aimDirectionX,
        double aimDirectionY,
        double aimDirectionZ,
        double aimTargetX,
        double aimTargetY,
        double aimTargetZ,
        long clientGameTime) implements CustomPacketPayload {
    public static final Type<ShootGunPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "shoot_gun"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShootGunPayload> STREAM_CODEC =
            StreamCodec.of(ShootGunPayload::encode, ShootGunPayload::decode);

    public Vec3 aimOrigin() {
        return new Vec3(aimOriginX, aimOriginY, aimOriginZ);
    }

    public Vec3 aimDirection() {
        return new Vec3(aimDirectionX, aimDirectionY, aimDirectionZ);
    }

    public Vec3 aimTarget() {
        return new Vec3(aimTargetX, aimTargetY, aimTargetZ);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ShootGunPayload payload) {
        buffer.writeUtf(payload.weaponId, 64);
        buffer.writeBoolean(payload.mainHand);
        buffer.writeBoolean(payload.hasTheCamAim);
        buffer.writeBoolean(payload.hasAimTarget);
        buffer.writeDouble(payload.aimOriginX);
        buffer.writeDouble(payload.aimOriginY);
        buffer.writeDouble(payload.aimOriginZ);
        buffer.writeDouble(payload.aimDirectionX);
        buffer.writeDouble(payload.aimDirectionY);
        buffer.writeDouble(payload.aimDirectionZ);
        buffer.writeDouble(payload.aimTargetX);
        buffer.writeDouble(payload.aimTargetY);
        buffer.writeDouble(payload.aimTargetZ);
        buffer.writeLong(payload.clientGameTime);
    }

    private static ShootGunPayload decode(RegistryFriendlyByteBuf buffer) {
        String weaponId = buffer.readUtf(64);
        boolean mainHand = buffer.readBoolean();
        boolean hasTheCamAim = buffer.readBoolean();
        boolean hasAimTarget = buffer.readBoolean();
        double aimOriginX = buffer.readDouble();
        double aimOriginY = buffer.readDouble();
        double aimOriginZ = buffer.readDouble();
        double aimDirectionX = buffer.readDouble();
        double aimDirectionY = buffer.readDouble();
        double aimDirectionZ = buffer.readDouble();
        double aimTargetX = buffer.readDouble();
        double aimTargetY = buffer.readDouble();
        double aimTargetZ = buffer.readDouble();
        long clientGameTime = buffer.readLong();
        return new ShootGunPayload(weaponId, mainHand, hasTheCamAim, hasAimTarget,
                aimOriginX, aimOriginY, aimOriginZ,
                aimDirectionX, aimDirectionY, aimDirectionZ,
                aimTargetX, aimTargetY, aimTargetZ,
                clientGameTime);
    }

    public InteractionHand hand() {
        return this.mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
