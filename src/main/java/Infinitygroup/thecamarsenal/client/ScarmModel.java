package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.weapon.ScarmItem;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.concurrent.atomic.AtomicBoolean;

public final class ScarmModel extends GeoModel<ScarmItem> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "geo/item/scarm.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "textures/item/scarm.png");
    private static final ResourceLocation ANIM = ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "animations/item/scarm.animation.json");
    private static final AtomicBoolean LOGGED_MODEL = new AtomicBoolean();

    public ScarmModel() {
        TheCamArsenal.LOGGER.info("Scarm model = {}", MODEL);
        TheCamArsenal.LOGGER.info("Scarm texture = {}", TEXTURE);
        TheCamArsenal.LOGGER.info("Scarm animation = {}", ANIM);
    }

    @Override
    public ResourceLocation getModelResource(ScarmItem animatable) {
        if (LOGGED_MODEL.compareAndSet(false, true)) {
            TheCamArsenal.LOGGER.info("Scarm model = {}", MODEL);
        }
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ScarmItem animatable) {
        TheCamArsenal.LOGGER.info("Scarm texture = {}", TEXTURE);
        return TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(ScarmItem animatable, @Nullable GeoRenderer<ScarmItem> renderer) {
        if (LOGGED_MODEL.compareAndSet(false, true)) {
            TheCamArsenal.LOGGER.info("Scarm model = {}", MODEL);
        }
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ScarmItem animatable, @Nullable GeoRenderer<ScarmItem> renderer) {
        TheCamArsenal.LOGGER.info("Scarm texture = {}", TEXTURE);
        if (renderer instanceof ScarmRenderer scarmRenderer) {
            return scarmRenderer.resolveTextureLocation();
        }
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ScarmItem animatable) {
        TheCamArsenal.LOGGER.info("Scarm animation = {}", ANIM);
        return ANIM;
    }
}
