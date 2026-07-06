package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.config.ClientConfig;
import infinitygroup.thecamarsenal.weapon.GunAmmoHelper;
import infinitygroup.thecamarsenal.weapon.GunDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

public final class GunAmmoHudOverlay {
    private static final int BACKGROUND_PADDING_X = 2;
    private static final int BACKGROUND_PADDING_Y = 2;

    private GunAmmoHudOverlay() {
    }

    public static void render(GuiGraphics guiGraphics, net.minecraft.client.DeltaTracker partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.options.hideGui || !ClientConfig.INSTANCE.enableGunAmmoHud.get()) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        GunDefinition definition = GunAmmoHelper.getDefinition(stack);
        if (definition == null) {
            return;
        }

        int currentAmmo = GunAmmoHelper.getAmmo(stack, definition);
        int maxAmmo = GunAmmoHelper.getMaxAmmo(definition);
        int windowWidth = guiGraphics.guiWidth();
        int windowHeight = guiGraphics.guiHeight();

        boolean reloading = GunReloadClientState.isReloading() && definition.id().equals(GunReloadClientState.weaponId());
        int color = resolveColor(currentAmmo, maxAmmo, reloading);
        String text = currentAmmo + "/" + maxAmmo;
        Font font = minecraft.font;
        int textWidth = font.width(text);
        int textX = resolveHudX(windowWidth, textWidth);
        int textY = resolveHudY(windowHeight);
        if (ClientConfig.INSTANCE.ammoHudDrawBackground.get()) {
            guiGraphics.fill(
                    textX - BACKGROUND_PADDING_X,
                    textY - BACKGROUND_PADDING_Y,
                    textX + textWidth + BACKGROUND_PADDING_X,
                    textY + font.lineHeight + BACKGROUND_PADDING_Y,
                    0x66000000);
        }
        guiGraphics.drawString(font, Component.literal(text), textX, textY, color, true);
    }

    private static int resolveHudX(int windowWidth, int textWidth) {
        return switch (ClientConfig.INSTANCE.ammoHudPositionMode()) {
            case ABOVE_SELECTED_SLOT -> (windowWidth - textWidth) / 2;
            case BOTTOM_RIGHT -> windowWidth - textWidth - ClientConfig.INSTANCE.ammoHudOffsetX.get();
        };
    }

    private static int resolveHudY(int windowHeight) {
        return switch (ClientConfig.INSTANCE.ammoHudPositionMode()) {
            case ABOVE_SELECTED_SLOT -> windowHeight - 36;
            case BOTTOM_RIGHT -> windowHeight - ClientConfig.INSTANCE.ammoHudOffsetY.get();
        };
    }

    private static int resolveColor(int currentAmmo, int maxAmmo, boolean reloading) {
        if (reloading) {
            return 0x62D8FF;
        }

        if (currentAmmo <= 0) {
            return 0xFF5C5C;
        }

        int threshold = Mth.ceil(maxAmmo * 0.3F);
        if (currentAmmo <= threshold) {
            return 0xFFD45A;
        }

        return 0xF2F2F2;
    }
}
