package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ReloadSkinsPayload() implements CustomPacketPayload {
    public static final Type<ReloadSkinsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "reload_skins"));
    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, ReloadSkinsPayload> STREAM_CODEC =
            StreamCodec.unit(new ReloadSkinsPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
