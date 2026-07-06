package infinitygroup.thecamarsenal.registry;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ArsenalCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TheCamArsenal.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + TheCamArsenal.MODID + ".main"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(ArsenalItems.AKM_47.get()))
                    .displayItems((params, output) -> {
                        output.accept(ArsenalItems.AKM_47.get());
                        output.accept(ArsenalItems.SCARM.get());
                        output.accept(ArsenalItems.MEDIUM_RIFLE_AMMO.get());
                    })
                    .build());

    private ArsenalCreativeTabs() {
    }
}
