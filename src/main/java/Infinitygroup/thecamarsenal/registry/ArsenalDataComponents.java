package infinitygroup.thecamarsenal.registry;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ArsenalDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, TheCamArsenal.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> SELECTED_SKIN =
            DATA_COMPONENT_TYPES.register("selected_skin", () -> DataComponentType.<String>builder()
                    .persistent(com.mojang.serialization.Codec.STRING)
                    .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.STRING_UTF8)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GunAmmoData>> GUN_AMMO =
            DATA_COMPONENT_TYPES.register("gun_ammo", () -> DataComponentType.<GunAmmoData>builder()
                    .persistent(GunAmmoData.CODEC)
                    .networkSynchronized(GunAmmoData.STREAM_CODEC)
                    .build());

    private ArsenalDataComponents() {
    }
}
