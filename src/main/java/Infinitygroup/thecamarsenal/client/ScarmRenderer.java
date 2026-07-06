package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.registry.ArsenalDataComponents;
import infinitygroup.thecamarsenal.weapon.ScarmItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.concurrent.atomic.AtomicBoolean;

public final class ScarmRenderer extends GeoItemRenderer<ScarmItem> {
    private static final AtomicBoolean LOGGED_CREATE = new AtomicBoolean();
    private static final ResourceLocation BASE_TEXTURE = ResourceLocation.fromNamespaceAndPath("thecamarsenal", "textures/item/scarm.png");

    public ScarmRenderer() {
        super(new ScarmModel());
        if (LOGGED_CREATE.compareAndSet(false, true)) {
            infinitygroup.thecamarsenal.TheCamArsenal.LOGGER.info("Scarm GeckoLib renderer created");
        }
    }

    public ResourceLocation resolveTextureLocation() {
        ItemStack stack = getCurrentItemStack();
        if (stack != null && !stack.isEmpty()) {
            String selectedSkin = stack.getOrDefault(ArsenalDataComponents.SELECTED_SKIN.get(), "default");
            ResourceLocation texture = GunRenderer.resolveTexture(stack, "scarm");
            infinitygroup.thecamarsenal.TheCamArsenal.LOGGER.debug("Scarm resolveTextureLocation stack skin={} texture={}", selectedSkin, texture);
            return texture;
        }
        infinitygroup.thecamarsenal.TheCamArsenal.LOGGER.debug("Scarm resolveTextureLocation fallback to base texture {}", BASE_TEXTURE);
        return BASE_TEXTURE;
    }

    @Override
    public ResourceLocation getTextureLocation(ScarmItem animatable) {
        return resolveTextureLocation();
    }
}
