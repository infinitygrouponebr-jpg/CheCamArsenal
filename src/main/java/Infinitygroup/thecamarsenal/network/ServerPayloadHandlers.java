package infinitygroup.thecamarsenal.network;

import infinitygroup.thecamarsenal.weapon.GunDefinition;
import infinitygroup.thecamarsenal.weapon.GunItem;
import infinitygroup.thecamarsenal.weapon.GunReloadManager;
import infinitygroup.thecamarsenal.weapon.GunShotHandler;
import infinitygroup.thecamarsenal.config.CommonConfig;
import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
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

    public static void handleShootGun(ShootGunPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) {
                return;
            }

            InteractionHand hand = payload.hand();
            ItemStack stack = serverPlayer.getItemInHand(hand);
            if (!(stack.getItem() instanceof GunItem gunItem)) {
                return;
            }

            GunDefinition definition = gunItem.getGunDefinition();
            if (definition == null || !definition.id().equals(payload.weaponId())) {
                return;
            }

            if (CommonConfig.INSTANCE.enableDebugAim.get()) {
                TheCamArsenal.LOGGER.info(
                        "ARSENAL_SERVER_SHOOT_PAYLOAD_RECEIVED weaponId={} hand={} hasTheCamAim={} hasAimTarget={} origin={} direction={} target={} clientGameTime={}",
                        payload.weaponId(),
                        hand,
                        payload.hasTheCamAim(),
                        payload.hasAimTarget(),
                        payload.aimOrigin(),
                        payload.aimDirection(),
                        payload.aimTarget(),
                        payload.clientGameTime());
            }

            if (GunShotHandler.tryFire(serverPlayer, stack, definition, payload)) {
                ServerLevel level = serverPlayer.serverLevel();
                gunItem.triggerShootAnimation(serverPlayer, level, stack);
            }
        });
    }
}
