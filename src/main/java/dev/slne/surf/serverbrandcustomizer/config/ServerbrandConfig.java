package dev.slne.surf.serverbrandcustomizer.config;

import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import dev.slne.surf.serverbrandcustomizer.SurfServerbrandCustomizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class ServerbrandConfig {

    private final SurfServerbrandCustomizer plugin;
    private @Nullable String customServerBrand;

    private volatile byte @Nullable [] customServerBrandBytes;

    public ServerbrandConfig(SurfServerbrandCustomizer plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        reloadFromConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        reloadFromConfig();
    }

    public void reloadFromConfig() {
        var rawBrand = plugin.getConfig().getString("brand");

        if (rawBrand == null) {
            customServerBrand = null;
            return;
        }

        //noinspection deprecation
        customServerBrand = LegacyComponentSerializer.legacySection()
                .serialize(Component.text()
                        .append(MiniMessage.miniMessage().deserialize(rawBrand))
                        .build()
                ) + ChatColor.RESET;

        customServerBrandBytes = null;
    }

    public boolean isCustomServerBrandSet() {
        return customServerBrand != null;
    }

    public void setCustomServerBrand(String customServerBrand) {
        plugin.getConfig().set("brand", customServerBrand);
        plugin.saveConfig();
        reloadFromConfig();
    }

    public @Nullable String getCustomServerBrand() {
        return customServerBrand;
    }

    public byte @Nullable [] getCustomServerBrandBytes() {
        if (customServerBrandBytes != null) {
            return customServerBrandBytes;
        }

        if (customServerBrand == null) {
            return null;
        }

        synchronized (this) {
            if (customServerBrandBytes != null) {
                return customServerBrandBytes;
            }

            var buf = UnpooledByteBufAllocationHelper.buffer();

            try {
                var wrapper = PacketWrapper.createUniversalPacketWrapper(buf);
                wrapper.writeString(customServerBrand);

                var data = new byte[ByteBufHelper.readableBytes(buf)];
                ByteBufHelper.readBytes(buf, data);

                return customServerBrandBytes = data;
            } finally {
                ByteBufHelper.release(buf);
            }
        }
    }
}
