package infinitygroup.thecamarsenal.weapon;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface GunItem {
    GunDefinition getGunDefinition();

    void triggerShootAnimation(ServerPlayer player, ServerLevel level, ItemStack stack);

    void triggerReloadAnimation(ServerPlayer player, ServerLevel level, ItemStack stack);
}
