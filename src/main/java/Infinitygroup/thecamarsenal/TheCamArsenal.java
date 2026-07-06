package infinitygroup.thecamarsenal;

import com.mojang.logging.LogUtils;
import infinitygroup.thecamarsenal.command.ArsenalCommands;
import infinitygroup.thecamarsenal.config.ClientConfig;
import infinitygroup.thecamarsenal.config.CommonConfig;
import infinitygroup.thecamarsenal.network.ArsenalNetworking;
import infinitygroup.thecamarsenal.network.ArsenalPermissionSync;
import infinitygroup.thecamarsenal.registry.ArsenalCreativeTabs;
import infinitygroup.thecamarsenal.registry.ArsenalDataComponents;
import infinitygroup.thecamarsenal.registry.ArsenalItems;
import infinitygroup.thecamarsenal.registry.ArsenalSounds;
import infinitygroup.thecamarsenal.weapon.GunReloadManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(TheCamArsenal.MODID)
public final class TheCamArsenal {
    public static final String MODID = "thecamarsenal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TheCamArsenal(IEventBus modEventBus, ModContainer modContainer) {
        ArsenalItems.ITEMS.register(modEventBus);
        ArsenalCreativeTabs.TABS.register(modEventBus);
        ArsenalDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);
        ArsenalSounds.SOUND_EVENTS.register(modEventBus);

        modEventBus.addListener(ArsenalNetworking::registerPayloadHandlers);

        NeoForge.EVENT_BUS.addListener(ArsenalCommands::registerCommands);
        NeoForge.EVENT_BUS.addListener(ArsenalPermissionSync::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(ArsenalPermissionSync::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(ArsenalPermissionSync::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(GunReloadManager::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(GunReloadManager::onPlayerLoggedOut);

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
