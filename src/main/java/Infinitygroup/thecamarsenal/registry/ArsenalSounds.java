package infinitygroup.thecamarsenal.registry;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ArsenalSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, TheCamArsenal.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent> AKM_47_SHOOT = SOUND_EVENTS.register("akm_47_shoot",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "akm_47_shoot")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SCARM_SHOOT = SOUND_EVENTS.register("scarm_shoot",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "scarm_shoot")));

    private ArsenalSounds() {
    }
}
