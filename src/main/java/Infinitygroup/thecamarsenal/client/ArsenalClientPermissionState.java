package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public final class ArsenalClientPermissionState {
    private static volatile boolean technicalTooltipsAllowed;

    private ArsenalClientPermissionState() {
    }

    public static boolean canSeeTechnicalTooltips() {
        return technicalTooltipsAllowed;
    }

    public static void setTechnicalTooltipsAllowed(boolean allowed) {
        technicalTooltipsAllowed = allowed;
    }

    @EventBusSubscriber(modid = TheCamArsenal.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class Events {
        private Events() {
        }

        @SubscribeEvent
        public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
            ArsenalClientPermissionState.setTechnicalTooltipsAllowed(false);
        }
    }
}
