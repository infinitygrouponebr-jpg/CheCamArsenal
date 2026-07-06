package infinitygroup.thecamarsenal.aim;

import net.minecraft.world.entity.player.Player;

public final class ArsenalAimManager {
    private ArsenalAimManager() {
    }

    public static AimProvider getAimProvider(Player player) {
        if (TheCamAimProvider.isTheCamLoaded() && TheCamAimProvider.INSTANCE.isTheCamAimActive(player)) {
            return TheCamAimProvider.INSTANCE;
        }
        return VanillaAimProvider.INSTANCE;
    }
}
