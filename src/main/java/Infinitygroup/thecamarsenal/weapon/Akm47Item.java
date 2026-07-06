package infinitygroup.thecamarsenal.weapon;

import infinitygroup.thecamarsenal.client.GunClientExtensions;
import infinitygroup.thecamarsenal.client.ArsenalClientPermissionState;
import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class Akm47Item extends Item implements GeoItem, GunItem {
    private static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenPlay("shoot");
    private static final RawAnimation RELOAD_ANIM = RawAnimation.begin().thenPlay("reload");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final GunDefinition definition;

    public Akm47Item(Properties properties, GunDefinition definition) {
        super(properties.stacksTo(1));
        this.definition = definition;
        GeoItem.registerSyncedAnimatable(this);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, net.minecraft.world.entity.player.Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            if (GunShotHandler.tryFire(serverPlayer, stack, this.definition)) {
                triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), "akm47_controller", "shoot");
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        GunStats stats = GunShotHandler.resolveStats(this.definition);
        Component damageValue = Component.literal(String.format(Locale.ROOT, "%.1f", stats.damage())).withStyle(ChatFormatting.AQUA);
        Component rangeValue = Component.literal(Long.toString(Math.round(stats.range()))).withStyle(ChatFormatting.GREEN);
        int ammo = GunAmmoHelper.getAmmo(stack, this.definition);
        int maxAmmo = GunAmmoHelper.getMaxAmmo(this.definition);
        boolean canSeeTechnicalTooltips = ArsenalClientPermissionState.canSeeTechnicalTooltips();

        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.subtitle").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.damage", damageValue).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.range", rangeValue).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.fire_rate").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.fire_type").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.aim").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.ammo", ammo, maxAmmo).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.description_1").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.description_2").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.empty());

        if (canSeeTechnicalTooltips && tooltipFlag.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.details_title").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
            tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.tech_fire_system").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.tech_recoil").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.tech_visual").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.tech_sound").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.tech_no_arrow").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.thecamarsenal.weapon.tech_reload_manual").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.thecamarsenal.weapon.tech_ammo_component").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.thecamarsenal.weapon.tech_ammo_use").withStyle(ChatFormatting.GRAY));
        } else if (canSeeTechnicalTooltips) {
            tooltip.add(Component.translatable("tooltip.thecamarsenal.akm_47.shift").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(GunClientExtensions.INSTANCE);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        TheCamArsenal.LOGGER.info("AKM-47 GeckoLib render provider requested");
        consumer.accept(new GeoRenderProvider() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                TheCamArsenal.LOGGER.info("AKM-47 GeckoLib item renderer supplied");
                return GunClientExtensions.akm47Renderer();
            }
        });
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "akm47_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("shoot", SHOOT_ANIM)
                .triggerableAnim("reload", RELOAD_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean isPerspectiveAware() {
        return true;
    }

    @Override
    public GunDefinition getGunDefinition() {
        return this.definition;
    }

    @Override
    public void triggerReloadAnimation(net.minecraft.server.level.ServerPlayer player, ServerLevel level, ItemStack stack) {
        triggerAnim(player, GeoItem.getOrAssignId(stack, level), "akm47_controller", "reload");
    }
}
