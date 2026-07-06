package infinitygroup.thecamarsenal.mixin.client;

import infinitygroup.thecamarsenal.client.pose.Akm47ClientPoseState;
import infinitygroup.thecamarsenal.weapon.ArsenalWeaponPoseHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class Akm47PlayerPoseMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart body;

    @Inject(method = "setupAnim", at = @At("TAIL"))
    private void thecamarsenal$applyAkm47Pose(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                                              float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player player)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || player != minecraft.player) {
            return;
        }

        if (!Akm47ClientPoseState.shouldPose(minecraft.player)) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean mainHasRifle = isAkmOrScarm(mainHand);
        boolean offHasRifle = isAkmOrScarm(offHand);
        if (!mainHasRifle && !offHasRifle) {
            return;
        }

        float poseAmount = Akm47ClientPoseState.getPoseAmount();
        if (poseAmount <= 0.0F) {
            return;
        }

        HumanoidArm weaponArm = mainHasRifle ? player.getMainArm() : player.getMainArm().getOpposite();
        if (weaponArm == HumanoidArm.RIGHT) {
            applyAimingPose(this.rightArm, poseAmount, -1.32F, -0.36F, 0.08F);
            applyAimingPose(this.leftArm, poseAmount, -1.42F, 0.00F, -0.56F);
            this.body.yRot = Mth.lerp(poseAmount, this.body.yRot, 0.05F);
        } else {
            applyAimingPose(this.leftArm, poseAmount, -1.32F, 0.36F, -0.08F);
            applyAimingPose(this.rightArm, poseAmount, -1.42F, 0.00F, 0.56F);
            this.body.yRot = Mth.lerp(poseAmount, this.body.yRot, -0.05F);
        }
    }

    private static void applyAimingPose(ModelPart arm, float poseAmount, float targetXRot, float targetYRot, float targetZRot) {
        arm.xRot = Mth.lerp(poseAmount, arm.xRot, targetXRot);
        arm.yRot = Mth.lerp(poseAmount, arm.yRot, targetYRot);
        arm.zRot = Mth.lerp(poseAmount, arm.zRot, targetZRot);
    }

    private static boolean isAkmOrScarm(ItemStack stack) {
        return ArsenalWeaponPoseHelper.isRifle(stack);
    }
}
