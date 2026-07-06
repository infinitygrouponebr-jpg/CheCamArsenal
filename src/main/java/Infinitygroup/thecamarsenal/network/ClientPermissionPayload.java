package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientPermissionPayload(boolean technicalTooltipAllowed) implements CustomPacketPayload {
    public static final Type<ClientPermissionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "client_permission"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientPermissionPayload> STREAM_CODEC =
            StreamCodec.of(ClientPermissionPayload::encode, ClientPermissionPayload::decode);

    private static void encode(RegistryFriendlyByteBuf buffer, ClientPermissionPayload payload) {
        buffer.writeBoolean(payload.technicalTooltipAllowed);
    }

    private static ClientPermissionPayload decode(RegistryFriendlyByteBuf buffer) {
        return new ClientPermissionPayload(buffer.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
