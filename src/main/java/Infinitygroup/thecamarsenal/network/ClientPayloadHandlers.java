package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.client.GunShotVisualEffects;
import infinitygroup.thecamarsenal.client.GunSkinManager;
import infinitygroup.thecamarsenal.client.ArsenalClientPermissionState;
import infinitygroup.thecamarsenal.client.GunReloadClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ClientPayloadHandlers {
    private ClientPayloadHandlers() {
    }

    public static void handleReloadSkins(ReloadSkinsPayload payload, IPayloadContext context) {
        context.enqueueWork(GunSkinManager::reload);
    }

    public static void handleGunShotVisual(GunShotVisualPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level == null) {
                return;
            }

            var shooter = level.getEntity(payload.shooterId());
            if (shooter == null) {
                shooter = minecraft.player;
            }
            if (shooter == null) {
                return;
            }

            GunShotVisualEffects.spawn(level, shooter, payload);
        });
    }

    public static void handleClientPermission(ClientPermissionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ArsenalClientPermissionState.setTechnicalTooltipsAllowed(payload.technicalTooltipAllowed()));
    }

    public static void handleGunReloadState(GunReloadStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> GunReloadClientState.apply(payload));
    }
}
