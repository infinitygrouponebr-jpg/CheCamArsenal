package infinitygroup.thecamarsenal.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class ArsenalNetworking {
    private ArsenalNetworking() {
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playBidirectional(ReloadSkinsPayload.TYPE, ReloadSkinsPayload.STREAM_CODEC, ClientPayloadHandlers::handleReloadSkins);
        registrar.playToClient(GunShotVisualPayload.TYPE, GunShotVisualPayload.STREAM_CODEC, ClientPayloadHandlers::handleGunShotVisual);
        registrar.playToClient(ClientPermissionPayload.TYPE, ClientPermissionPayload.STREAM_CODEC, ClientPayloadHandlers::handleClientPermission);
        registrar.playToClient(GunReloadStatePayload.TYPE, GunReloadStatePayload.STREAM_CODEC, ClientPayloadHandlers::handleGunReloadState);
        registrar.playToServer(ReloadGunPayload.TYPE, ReloadGunPayload.STREAM_CODEC, ServerPayloadHandlers::handleReloadGun);
    }
}
