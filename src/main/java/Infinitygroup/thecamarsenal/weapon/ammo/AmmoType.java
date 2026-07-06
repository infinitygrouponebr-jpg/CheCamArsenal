package infinitygroup.thecamarsenal.weapon.ammo;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public enum AmmoType {
    MEDIUM_RIFLE;

    private Supplier<? extends Item> itemSupplier;

    public AmmoType bind(Supplier<? extends Item> itemSupplier) {
        this.itemSupplier = itemSupplier;
        return this;
    }

    public Item getItem() {
        if (this.itemSupplier == null) {
            throw new IllegalStateException("Ammo type not bound: " + this.name());
        }
        return this.itemSupplier.get();
    }
}
