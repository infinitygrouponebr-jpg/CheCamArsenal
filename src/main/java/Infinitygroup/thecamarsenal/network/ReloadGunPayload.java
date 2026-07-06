package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ReloadGunPayload() implements CustomPacketPayload {
    public static final Type<ReloadGunPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "reload_gun"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadGunPayload> STREAM_CODEC =
            StreamCodec.unit(new ReloadGunPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
