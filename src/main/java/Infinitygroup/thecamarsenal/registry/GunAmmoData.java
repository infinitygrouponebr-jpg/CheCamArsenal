package infinitygroup.thecamarsenal.registry;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GunAmmoData(int ammo) {
    public static final Codec<GunAmmoData> CODEC = Codec.INT.xmap(GunAmmoData::new, GunAmmoData::ammo);
    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GunAmmoData> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, GunAmmoData::ammo, GunAmmoData::new);
}
