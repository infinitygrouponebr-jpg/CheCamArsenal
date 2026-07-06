package infinitygroup.thecamarsenal.aim;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface AimProvider {
    Vec3 getAimOrigin(Player player);
    Vec3 getAimDirection(Player player);
    Vec3 getAimTarget(Player player);
    boolean hasAimTarget(Player player);
    boolean isTheCamAimActive(Player player);
}
