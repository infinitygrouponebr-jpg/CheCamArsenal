package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.registry.ArsenalDataComponents;
import infinitygroup.thecamarsenal.client.GunSkinManager;
import infinitygroup.thecamarsenal.weapon.Akm47Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Akm47Renderer extends GeoItemRenderer<Akm47Item> {
    private static final AtomicBoolean LOGGED_CREATE = new AtomicBoolean();
    private static final ResourceLocation BASE_TEXTURE = ResourceLocation.fromNamespaceAndPath("thecamarsenal", "textures/item/akm_47.png");

    public Akm47Renderer() {
        super(new Akm47Model());
        if (LOGGED_CREATE.compareAndSet(false, true)) {
            infinitygroup.thecamarsenal.TheCamArsenal.LOGGER.info("AKM-47 GeckoLib renderer created");
        }
    }

    public ResourceLocation resolveTextureLocation() {
        ItemStack stack = getCurrentItemStack();
        if (stack != null && !stack.isEmpty()) {
            String selectedSkin = stack.getOrDefault(ArsenalDataComponents.SELECTED_SKIN.get(), "default");
            ResourceLocation texture = GunSkinManager.resolveTexture("akm_47", selectedSkin);
            infinitygroup.thecamarsenal.TheCamArsenal.LOGGER.debug("AKM-47 resolveTextureLocation stack skin={} texture={}", selectedSkin, texture);
            return texture;
        }
        infinitygroup.thecamarsenal.TheCamArsenal.LOGGER.debug("AKM-47 resolveTextureLocation fallback to base texture {}", BASE_TEXTURE);
        return BASE_TEXTURE;
    }

    @Override
    public ResourceLocation getTextureLocation(Akm47Item animatable) {
        return resolveTextureLocation();
    }
}
