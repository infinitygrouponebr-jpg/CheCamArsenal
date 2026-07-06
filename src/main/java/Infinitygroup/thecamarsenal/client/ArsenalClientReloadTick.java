package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.network.ReloadGunPayload;
import infinitygroup.thecamarsenal.weapon.GunAmmoHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ArsenalClientReloadTick {
    private ArsenalClientReloadTick() {
    }

    @EventBusSubscriber(modid = TheCamArsenal.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static final class Events {
        private Events() {
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player == null || minecraft.level == null) {
                GunReloadClientState.clear();
                return;
            }

            GunReloadClientState.tick();

            while (ArsenalClientBindings.RELOAD_KEY.consumeClick()) {
                ItemStack stack = player.getMainHandItem();
                if (GunAmmoHelper.getDefinition(stack) == null) {
                    continue;
                }

                if (GunReloadClientState.isReloading()) {
                    continue;
                }

                PacketDistributor.sendToServer(new ReloadGunPayload());
            }
        }
    }
}
