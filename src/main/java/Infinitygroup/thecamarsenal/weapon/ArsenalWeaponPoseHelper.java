package infinitygroup.thecamarsenal.weapon;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public final class ArsenalWeaponPoseHelper {
    private ArsenalWeaponPoseHelper() {
    }

    public static boolean isRifle(ItemStack stack) {
        return stack.getItem() instanceof Akm47Item || stack.getItem() instanceof ScarmItem;
    }

    public static boolean isHoldingRifle(LocalPlayer player) {
        return isRifle(player.getMainHandItem()) || isRifle(player.getOffhandItem());
    }
}
