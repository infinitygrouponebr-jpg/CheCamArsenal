package infinitygroup.thecamarsenal.registry;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.weapon.Akm47Item;
import infinitygroup.thecamarsenal.weapon.GunDefinitions;
import infinitygroup.thecamarsenal.weapon.ScarmItem;
import infinitygroup.thecamarsenal.weapon.ammo.AmmoType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.world.item.Item;

public final class ArsenalItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheCamArsenal.MODID);

    public static final DeferredItem<Item> MEDIUM_RIFLE_AMMO = ITEMS.registerItem("medium_rifle_ammo",
            properties -> new Item(properties.stacksTo(64)));
    public static final DeferredItem<Item> AKM_47 = ITEMS.registerItem("akm_47",
            properties -> new Akm47Item(properties, GunDefinitions.AKM_47));
    public static final DeferredItem<Item> SCARM = ITEMS.registerItem("scarm",
            properties -> new ScarmItem(properties, GunDefinitions.SCARM));

    static {
        AmmoType.MEDIUM_RIFLE.bind(MEDIUM_RIFLE_AMMO);
    }

    private ArsenalItems() {
    }
}
