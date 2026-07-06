package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.registry.ArsenalDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class GunRenderer {
    private GunRenderer() {
    }

    public static ResourceLocation resolveTexture(ItemStack stack, String weaponId) {
        String selectedSkin = stack.getOrDefault(ArsenalDataComponents.SELECTED_SKIN.get(), "default");
        return GunSkinManager.resolveTexture(weaponId, selectedSkin);
    }
}
