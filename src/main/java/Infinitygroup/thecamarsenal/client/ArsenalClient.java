package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(modid = TheCamArsenal.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ArsenalClient {
    private static boolean bootstrapped;

    private ArsenalClient() {
    }

    public static void bootstrap() {
        if (!bootstrapped) {
            bootstrapped = true;
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(GunSkinManager::initialize);
    }
}
