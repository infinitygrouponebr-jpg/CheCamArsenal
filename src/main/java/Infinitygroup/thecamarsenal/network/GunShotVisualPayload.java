package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GunShotVisualPayload(int shooterId, int hitEntityId, byte aimProvider, boolean hasAimTarget,
        String shootSoundId,
        double cameraOriginX, double cameraOriginY, double cameraOriginZ,
        double originX, double originY, double originZ,
        double aimTargetX, double aimTargetY, double aimTargetZ,
        double hitX, double hitY, double hitZ, byte hitType) implements CustomPacketPayload {
    public static final byte HIT_MISS = 0;
    public static final byte HIT_BLOCK = 1;
    public static final byte HIT_ENTITY = 2;
    public static final byte AIM_PROVIDER_VANILLA = 0;
    public static final byte AIM_PROVIDER_THE_CAM = 1;
    public static final byte AIM_PROVIDER_ERROR = 2;

    public static final Type<GunShotVisualPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "gun_shot_visual"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GunShotVisualPayload> STREAM_CODEC =
            StreamCodec.of(GunShotVisualPayload::encode, GunShotVisualPayload::decode);

    public boolean isMiss() {
        return this.hitType == HIT_MISS;
    }

    public boolean isBlockHit() {
        return this.hitType == HIT_BLOCK;
    }

    public boolean isEntityHit() {
        return this.hitType == HIT_ENTITY;
    }

    private static void encode(RegistryFriendlyByteBuf buffer, GunShotVisualPayload payload) {
        buffer.writeVarInt(payload.shooterId);
        buffer.writeVarInt(payload.hitEntityId);
        buffer.writeByte(payload.aimProvider);
        buffer.writeBoolean(payload.hasAimTarget);
        buffer.writeUtf(payload.shootSoundId, 64);
        buffer.writeDouble(payload.cameraOriginX);
        buffer.writeDouble(payload.cameraOriginY);
        buffer.writeDouble(payload.cameraOriginZ);
        buffer.writeDouble(payload.originX);
        buffer.writeDouble(payload.originY);
        buffer.writeDouble(payload.originZ);
        buffer.writeDouble(payload.aimTargetX);
        buffer.writeDouble(payload.aimTargetY);
        buffer.writeDouble(payload.aimTargetZ);
        buffer.writeDouble(payload.hitX);
        buffer.writeDouble(payload.hitY);
        buffer.writeDouble(payload.hitZ);
        buffer.writeByte(payload.hitType);
    }

    private static GunShotVisualPayload decode(RegistryFriendlyByteBuf buffer) {
        int shooterId = buffer.readVarInt();
        int hitEntityId = buffer.readVarInt();
        byte aimProvider = buffer.readByte();
        boolean hasAimTarget = buffer.readBoolean();
        String shootSoundId = buffer.readUtf(64);
        double cameraOriginX = buffer.readDouble();
        double cameraOriginY = buffer.readDouble();
        double cameraOriginZ = buffer.readDouble();
        double originX = buffer.readDouble();
        double originY = buffer.readDouble();
        double originZ = buffer.readDouble();
        double aimTargetX = buffer.readDouble();
        double aimTargetY = buffer.readDouble();
        double aimTargetZ = buffer.readDouble();
        double hitX = buffer.readDouble();
        double hitY = buffer.readDouble();
        double hitZ = buffer.readDouble();
        byte hitType = buffer.readByte();
        return new GunShotVisualPayload(shooterId, hitEntityId, aimProvider, hasAimTarget,
                shootSoundId,
                cameraOriginX, cameraOriginY, cameraOriginZ,
                originX, originY, originZ,
                aimTargetX, aimTargetY, aimTargetZ,
                hitX, hitY, hitZ, hitType);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
