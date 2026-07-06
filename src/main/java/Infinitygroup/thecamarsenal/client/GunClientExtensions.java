package infinitygroup.thecamarsenal.client;

import com.mojang.blaze3d.vertex.PoseStack;
import infinitygroup.thecamarsenal.weapon.Akm47Item;
import infinitygroup.thecamarsenal.weapon.ScarmItem;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public final class GunClientExtensions implements IClientItemExtensions {
    public static final GunClientExtensions INSTANCE = new GunClientExtensions();
    private static final Akm47Renderer AKM47_RENDERER = new Akm47Renderer();
    private static final ScarmRenderer SCARM_RENDERER = new ScarmRenderer();

    private GunClientExtensions() {
    }

    public static Akm47Renderer akm47Renderer() {
        return AKM47_RENDERER;
    }

    public static ScarmRenderer scarmRenderer() {
        return SCARM_RENDERER;
    }

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand,
            float partialTick, float equipProcess, float swingProcess) {
        if (!GunSkinManager.isRecoilEnabled()) {
            return false;
        }

        if (!player.isUsingItem() || itemInHand.isEmpty()) {
            return false;
        }

        HumanoidArm usingArm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        if (usingArm != arm) {
            return false;
        }

        float recoil = Math.min(1.0F, (20.0F - player.getUseItemRemainingTicks()) / 6.0F);
        poseStack.translate(0.0D, -0.05D * recoil, -0.08D * recoil);
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-6.0F * recoil));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(2.0F * recoil));
        return true;
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return AKM47_RENDERER;
    }
}
