package infinitygroup.thecamarsenal.weapon;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public final class GunCooldownManager {
    private GunCooldownManager() {
    }

    public static void applyCooldown(Player player, Item item, int cooldownTicks) {
        player.getCooldowns().addCooldown(item, cooldownTicks);
    }
}
