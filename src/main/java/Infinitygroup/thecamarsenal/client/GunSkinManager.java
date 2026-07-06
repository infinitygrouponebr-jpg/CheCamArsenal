package infinitygroup.thecamarsenal.client;

import infinitygroup.thecamarsenal.TheCamArsenal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class GunSkinManager {
    private static final Pattern VALID_NAME = Pattern.compile("^[a-z0-9_\\-]+$");
    private static final Map<String, Map<String, ResourceLocation>> REGISTERED_TEXTURES = new HashMap<>();
    private static final Map<String, ResourceLocation> DEFAULT_TEXTURES = Map.of(
            "akm_47", ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "textures/item/akm_47.png"),
            "scarm", ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID, "textures/item/scarm.png"));
    private static boolean initialized;

    private GunSkinManager() {
    }

    public static void initialize() {
        if (!initialized) {
            initialized = true;
            reload();
        }
    }

    public static boolean isRecoilEnabled() {
        return infinitygroup.thecamarsenal.config.ClientConfig.INSTANCE.enableRecoilVisual.get();
    }

    public static void reload() {
        REGISTERED_TEXTURES.clear();

        if (!infinitygroup.thecamarsenal.config.ClientConfig.INSTANCE.enableExternalGunSkins.get()) {
            return;
        }

        Path root = FMLPaths.CONFIGDIR.get().resolve(TheCamArsenal.MODID).resolve("skins");
        try {
            Files.createDirectories(root);
            Files.createDirectories(root.resolve("akm_47"));
            Files.createDirectories(root.resolve("scarm"));
            scanWeaponFolder("akm_47", root.resolve("akm_47"));
            scanWeaponFolder("scarm", root.resolve("scarm"));
        } catch (IOException exception) {
            TheCamArsenal.LOGGER.warn("Failed to prepare gun skin directory {}", root, exception);
        }
    }

    public static ResourceLocation resolveTexture(String weaponId, String skinName) {
        Map<String, ResourceLocation> weaponTextures = REGISTERED_TEXTURES.getOrDefault(weaponId, Collections.emptyMap());
        ResourceLocation texture = weaponTextures.get(skinName);
        if (texture != null) {
            TheCamArsenal.LOGGER.debug("Gun skin resolved: weapon={} skin={} texture={}", weaponId, skinName, texture);
            return texture;
        }

        ResourceLocation defaultTexture = DEFAULT_TEXTURES.getOrDefault(weaponId, DEFAULT_TEXTURES.get("akm_47"));
        TheCamArsenal.LOGGER.debug("Gun skin fallback: weapon={} skin={} texture={}", weaponId, skinName, defaultTexture);
        return defaultTexture;
    }

    private static void scanWeaponFolder(String weaponId, Path weaponFolder) throws IOException {
        Map<String, ResourceLocation> textures = new HashMap<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(weaponFolder, "*.png")) {
            for (Path file : stream) {
                String fileName = stripExtension(file.getFileName().toString()).toLowerCase(Locale.ROOT);
                if (!VALID_NAME.matcher(fileName).matches()) {
                    continue;
                }

                ResourceLocation textureId = ResourceLocation.fromNamespaceAndPath(TheCamArsenal.MODID,
                        "dynamic/" + weaponId + "/" + fileName);
                registerDynamicTexture(textureId, file);
                textures.put(fileName, textureId);
            }
        }
        REGISTERED_TEXTURES.put(weaponId, textures);
    }

    private static void registerDynamicTexture(ResourceLocation textureId, Path file) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }

        try (InputStream inputStream = Files.newInputStream(file)) {
            NativeImage image = NativeImage.read(inputStream);
            minecraft.getTextureManager().register(textureId, new DynamicTexture(image));
        } catch (IOException exception) {
            TheCamArsenal.LOGGER.warn("Invalid gun skin texture {}", file, exception);
        }
    }

    private static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot >= 0 ? fileName.substring(0, dot) : fileName;
    }
}
