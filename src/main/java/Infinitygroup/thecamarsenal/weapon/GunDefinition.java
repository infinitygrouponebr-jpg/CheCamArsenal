package infinitygroup.thecamarsenal.weapon;

import infinitygroup.thecamarsenal.weapon.ammo.AmmoType;
import net.minecraft.world.item.Item;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public record GunDefinition(String id, double defaultDamage, double defaultRange, int defaultCooldownTicks,
        AmmoType ammoType, int internalAmmoCapacity, int reloadTicks,
        DeferredHolder<SoundEvent, SoundEvent> shootSound) {
    public Item getAmmoItem() {
        return this.ammoType.getItem();
    }
}
