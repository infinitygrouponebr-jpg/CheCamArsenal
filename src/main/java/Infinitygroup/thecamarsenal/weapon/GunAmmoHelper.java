package infinitygroup.thecamarsenal.weapon;

import infinitygroup.thecamarsenal.config.CommonConfig;
import infinitygroup.thecamarsenal.registry.ArsenalDataComponents;
import infinitygroup.thecamarsenal.registry.GunAmmoData;
import net.minecraft.world.item.ItemStack;

public final class GunAmmoHelper {
    private GunAmmoHelper() {
    }

    public static boolean isGun(ItemStack stack) {
        return getDefinition(stack) != null;
    }

    public static boolean isRifle(ItemStack stack) {
        return isGun(stack);
    }

    public static GunDefinition getDefinition(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof GunItem gunItem)) {
            return null;
        }
        return gunItem.getGunDefinition();
    }

    public static int getMaxAmmo(GunDefinition definition) {
        return switch (definition.id()) {
            case "akm_47" -> CommonConfig.INSTANCE.akm47InternalAmmoCapacity.get();
            case "scarm" -> CommonConfig.INSTANCE.scarmInternalAmmoCapacity.get();
            default -> definition.internalAmmoCapacity();
        };
    }

    public static int getReloadTicks(GunDefinition definition) {
        return switch (definition.id()) {
            case "akm_47" -> CommonConfig.INSTANCE.akm47ReloadTicks.get();
            case "scarm" -> CommonConfig.INSTANCE.scarmReloadTicks.get();
            default -> definition.reloadTicks();
        };
    }

    public static int getAmmo(ItemStack stack, GunDefinition definition) {
        ensureAmmoInitialized(stack, definition);
        GunAmmoData ammoData = stack.get(ArsenalDataComponents.GUN_AMMO.get());
        return ammoData == null ? getMaxAmmo(definition) : clamp(ammoData.ammo(), definition);
    }

    public static void setAmmo(ItemStack stack, GunDefinition definition, int ammo) {
        int clamped = clamp(ammo, definition);
        stack.set(ArsenalDataComponents.GUN_AMMO.get(), new GunAmmoData(clamped));
    }

    public static void ensureAmmoInitialized(ItemStack stack, GunDefinition definition) {
        if (stack.get(ArsenalDataComponents.GUN_AMMO.get()) == null) {
            stack.set(ArsenalDataComponents.GUN_AMMO.get(), new GunAmmoData(getMaxAmmo(definition)));
        }
    }

    public static int clamp(int ammo, GunDefinition definition) {
        return Math.max(0, Math.min(ammo, getMaxAmmo(definition)));
    }
}
