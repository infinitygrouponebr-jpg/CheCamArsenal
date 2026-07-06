package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.weapon.GunReloadManager;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ServerPayloadHandlers {
    private ServerPayloadHandlers() {
    }

    public static void handleReloadGun(ReloadGunPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                GunReloadManager.requestReload(serverPlayer);
            }
        });
    }
}
