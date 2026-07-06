package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.weapon.Akm47Item;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Akm47Model extends GeoModel<Akm47Item> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "geo/item/akm_47.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "textures/item/akm_47.png");
    private static final ResourceLocation ANIM = ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "animations/item/akm_47.animation.json");
    private static final AtomicBoolean LOGGED_MODEL = new AtomicBoolean();
    private static final AtomicBoolean LOGGED_TEXTURE = new AtomicBoolean();
    private static final AtomicBoolean LOGGED_ANIM = new AtomicBoolean();

    @Override
    public ResourceLocation getModelResource(Akm47Item animatable) {
        if (LOGGED_MODEL.compareAndSet(false, true)) {
            TheCamArsenal.LOGGER.info("AKM-47 GeckoLib model requested: {}", MODEL);
        }
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Akm47Item animatable) {
        if (LOGGED_TEXTURE.compareAndSet(false, true)) {
            TheCamArsenal.LOGGER.info("AKM-47 GeckoLib texture requested: {}", TEXTURE);
        }
        TheCamArsenal.LOGGER.debug("AKM-47 GeckoLib texture base in use: {}", TEXTURE);
        return TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(Akm47Item animatable, @Nullable GeoRenderer<Akm47Item> renderer) {
        if (LOGGED_MODEL.compareAndSet(false, true)) {
            TheCamArsenal.LOGGER.info("AKM-47 GeckoLib model requested with renderer: {}", MODEL);
        }
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(Akm47Item animatable, @Nullable GeoRenderer<Akm47Item> renderer) {
        if (renderer instanceof Akm47Renderer akm47Renderer) {
            if (LOGGED_TEXTURE.compareAndSet(false, true)) {
                TheCamArsenal.LOGGER.info("AKM-47 GeckoLib texture requested with renderer");
            }
            ResourceLocation texture = akm47Renderer.resolveTextureLocation();
            TheCamArsenal.LOGGER.debug("AKM-47 GeckoLib renderer texture in use: {}", texture);
            return texture;
        }
        TheCamArsenal.LOGGER.debug("AKM-47 GeckoLib renderer missing, using base texture: {}", TEXTURE);
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(Akm47Item animatable) {
        if (LOGGED_ANIM.compareAndSet(false, true)) {
            TheCamArsenal.LOGGER.info("AKM-47 GeckoLib animation requested: {}", ANIM);
        }
        return ANIM;
    }
}
