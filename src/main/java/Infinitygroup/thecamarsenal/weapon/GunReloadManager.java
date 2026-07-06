package infinitygroup.thecamarsenal.weapon;

import infinitygroup.thecamarsenal.config.CommonConfig;
import infinitygroup.thecamarsenal.network.GunReloadStatePayload;
import infinitygroup.thecamarsenal.weapon.ammo.AmmoHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GunReloadManager {
    private static final Map<UUID, ReloadState> ACTIVE_RELOADS = new HashMap<>();

    private GunReloadManager() {
    }

    public static boolean isReloading(ServerPlayer player) {
        return ACTIVE_RELOADS.containsKey(player.getUUID());
    }

    public static boolean requestReload(ServerPlayer player) {
        if (!CommonConfig.INSTANCE.enableReloadSystem.get()) {
            return false;
        }

        if (player.isSpectator() || !player.isAlive()) {
            return false;
        }

        if (isReloading(player)) {
            return false;
        }

        ItemStack stack = player.getMainHandItem();
        GunDefinition definition = GunAmmoHelper.getDefinition(stack);
        if (definition == null) {
            return false;
        }

        GunAmmoHelper.ensureAmmoInitialized(stack, definition);
        int currentAmmo = GunAmmoHelper.getAmmo(stack, definition);
        int maxAmmo = GunAmmoHelper.getMaxAmmo(definition);
        if (currentAmmo >= maxAmmo) {
            return false;
        }

        if (!player.getAbilities().instabuild && AmmoHelper.countAmmo(player, definition.getAmmoItem()) <= 0) {
            return false;
        }

        int reloadTicks = GunAmmoHelper.getReloadTicks(definition);
        ReloadState state = new ReloadState(InteractionHand.MAIN_HAND, player.getInventory().selected,
                definition, reloadTicks);
        ACTIVE_RELOADS.put(player.getUUID(), state);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new GunReloadStatePayload(player.getId(), true, definition.id(), reloadTicks, reloadTicks));

        if (stack.getItem() instanceof GunItem gunItem) {
            gunItem.triggerReloadAnimation(player, player.serverLevel(), stack);
        }

        return true;
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ReloadState state = ACTIVE_RELOADS.get(player.getUUID());
        if (state == null) {
            return;
        }

        if (!isStateValid(player, state)) {
            ACTIVE_RELOADS.remove(player.getUUID());
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                    new GunReloadStatePayload(player.getId(), false, state.definition.id(), 0, state.totalTicks));
            return;
        }

        if (state.ticksRemaining > 1) {
            state.ticksRemaining--;
            return;
        }

        finishReload(player, state);
        ACTIVE_RELOADS.remove(player.getUUID());
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new GunReloadStatePayload(player.getId(), false, state.definition.id(), 0, state.totalTicks));
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        ACTIVE_RELOADS.remove(event.getEntity().getUUID());
    }

    private static boolean isStateValid(ServerPlayer player, ReloadState state) {
        if (player.isSpectator() || !player.isAlive()) {
            return false;
        }

        if (state.hand != InteractionHand.MAIN_HAND) {
            return false;
        }

        if (player.getInventory().selected != state.selectedSlot) {
            return false;
        }

        GunDefinition currentDefinition = GunAmmoHelper.getDefinition(player.getMainHandItem());
        return currentDefinition == state.definition;
    }

    private static void finishReload(ServerPlayer player, ReloadState state) {
        ItemStack stack = player.getMainHandItem();
        GunAmmoHelper.ensureAmmoInitialized(stack, state.definition);

        int currentAmmo = GunAmmoHelper.getAmmo(stack, state.definition);
        int maxAmmo = GunAmmoHelper.getMaxAmmo(state.definition);
        int missing = maxAmmo - currentAmmo;
        if (missing <= 0) {
            return;
        }

        int refillAmount;
        if (player.getAbilities().instabuild) {
            refillAmount = missing;
        } else {
            int availableAmmo = AmmoHelper.countAmmo(player, state.definition.getAmmoItem());
            refillAmount = Math.min(missing, availableAmmo);
            if (refillAmount > 0) {
                AmmoHelper.consumeAmmo(player, state.definition.getAmmoItem(), refillAmount);
            }
        }

        if (refillAmount > 0) {
            GunAmmoHelper.setAmmo(stack, state.definition, currentAmmo + refillAmount);
            player.getInventory().setChanged();
        }
    }

    private static final class ReloadState {
        private final InteractionHand hand;
        private final int selectedSlot;
        private final GunDefinition definition;
        private final int totalTicks;
        private int ticksRemaining;

        private ReloadState(InteractionHand hand, int selectedSlot, GunDefinition definition, int ticksRemaining) {
            this.hand = hand;
            this.selectedSlot = selectedSlot;
            this.definition = definition;
            this.totalTicks = ticksRemaining;
            this.ticksRemaining = ticksRemaining;
        }
    }
}
