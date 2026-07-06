package infinitygroup.thecamarsenal.client.pose;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.weapon.ArsenalWeaponPoseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.api.distmarker.Dist;

public final class Akm47ClientPoseState {
    private static float poseAmount;
    private static float targetPoseAmount;
    private static boolean lastUseDown;
    private static int holdGraceTicks;

    private Akm47ClientPoseState() {
    }

    public static float getPoseAmount() {
        return poseAmount;
    }

    public static boolean isPoseActive() {
        return poseAmount > 0.001F;
    }

    public static boolean shouldPose(LocalPlayer player) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || player != minecraft.player) {
            return false;
        }
        return isPoseActive();
    }

    @EventBusSubscriber(modid = TheCamArsenal.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class ClientEvents {
        private ClientEvents() {
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player == null || minecraft.level == null) {
                reset();
                return;
            }

            if (!canPose(player)) {
                reset();
                return;
            }

            boolean holdingRifle = ArsenalWeaponPoseHelper.isHoldingRifle(player);
            boolean useDown = minecraft.options.keyUse.isDown();

            if (!holdingRifle) {
                reset();
                lastUseDown = useDown;
                return;
            }

            boolean aimingNow = useDown;
            boolean releasedAfterAim = lastUseDown && !useDown;

            if (aimingNow) {
                holdGraceTicks = 8;
            } else if (releasedAfterAim) {
                holdGraceTicks = 8;
            } else if (holdGraceTicks > 0) {
                holdGraceTicks--;
            }

            targetPoseAmount = (aimingNow || holdGraceTicks > 0) ? 1.0F : 0.0F;
            poseAmount = Mth.lerp(0.25F, poseAmount, targetPoseAmount);
            lastUseDown = useDown;
        }
    }

    private static void reset() {
        poseAmount = 0.0F;
        targetPoseAmount = 0.0F;
        holdGraceTicks = 0;
    }

    private static boolean canPose(LocalPlayer player) {
        if (player.isSpectator() || player.isSleeping() || player.isPassenger() || player.isFallFlying()) {
            return false;
        }
        return !player.isVisuallySwimming() && !player.isAutoSpinAttack();
    }

}
