package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record GunReloadStatePayload(int playerId, boolean active, String weaponId, int ticksRemaining, int totalTicks)
        implements CustomPacketPayload {
    public static final Type<GunReloadStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "gun_reload_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GunReloadStatePayload> STREAM_CODEC =
            StreamCodec.composite(
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, GunReloadStatePayload::playerId,
                    net.minecraft.network.codec.ByteBufCodecs.BOOL, GunReloadStatePayload::active,
                    net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8, GunReloadStatePayload::weaponId,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, GunReloadStatePayload::ticksRemaining,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, GunReloadStatePayload::totalTicks,
                    GunReloadStatePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
