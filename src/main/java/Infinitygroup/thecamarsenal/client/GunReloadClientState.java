package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.network.GunReloadStatePayload;
import net.minecraft.client.Minecraft;

public final class GunReloadClientState {
    private static boolean reloading;
    private static String weaponId = "";
    private static int ticksRemaining;
    private static int totalTicks;

    private GunReloadClientState() {
    }

    public static boolean isReloading() {
        return reloading;
    }

    public static String weaponId() {
        return weaponId;
    }

    public static int ticksRemaining() {
        return ticksRemaining;
    }

    public static int totalTicks() {
        return totalTicks;
    }

    public static void apply(GunReloadStatePayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.player.getId() != payload.playerId()) {
            return;
        }

        reloading = payload.active();
        weaponId = payload.weaponId();
        ticksRemaining = Math.max(0, payload.ticksRemaining());
        totalTicks = Math.max(0, payload.totalTicks());
        if (!reloading) {
            weaponId = "";
            ticksRemaining = 0;
            totalTicks = 0;
        }
    }

    public static void tick() {
        if (!reloading) {
            return;
        }

        if (ticksRemaining > 0) {
            ticksRemaining--;
        }

        if (ticksRemaining <= 0) {
            reloading = false;
            weaponId = "";
            totalTicks = 0;
            ticksRemaining = 0;
        }
    }

    public static void clear() {
        reloading = false;
        weaponId = "";
        ticksRemaining = 0;
        totalTicks = 0;
    }
}
