package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

@EventBusSubscriber(modid = TheCamArsenal.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ArsenalClientBindings {
    private static final String CATEGORY = "key.categories.thecamarsenal";
    public static final KeyMapping RELOAD_KEY = new KeyMapping(
            "key.thecamarsenal.reload",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            CATEGORY);

    private static final net.minecraft.resources.ResourceLocation GUN_AMMO_HUD_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "gun_ammo_hud");

    private ArsenalClientBindings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(RELOAD_KEY);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, GUN_AMMO_HUD_ID, GunAmmoHudOverlay::render);
    }
}
