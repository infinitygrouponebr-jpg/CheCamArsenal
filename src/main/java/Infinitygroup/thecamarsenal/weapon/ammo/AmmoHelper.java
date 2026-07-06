package infinitygroup.thecamarsenal.weapon.ammo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class AmmoHelper {
    private AmmoHelper() {
    }

    public static boolean hasAmmo(Player player, Item ammoItem) {
        if (player.getAbilities().instabuild) {
            return true;
        }

        return countAmmo(player, ammoItem) > 0;
    }

    public static boolean consumeAmmo(Player player, Item ammoItem, int amount) {
        if (player.getAbilities().instabuild || amount <= 0) {
            return true;
        }

        int remaining = amount;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ammoItem)) {
                int used = Math.min(remaining, stack.getCount());
                stack.shrink(used);
                remaining -= used;
                player.getInventory().setChanged();
                if (remaining <= 0) {
                    return true;
                }
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ammoItem)) {
                int used = Math.min(remaining, stack.getCount());
                stack.shrink(used);
                remaining -= used;
                player.getInventory().setChanged();
                if (remaining <= 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int countAmmo(Player player, Item ammoItem) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ammoItem)) {
                count += stack.getCount();
            }
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(ammoItem)) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
