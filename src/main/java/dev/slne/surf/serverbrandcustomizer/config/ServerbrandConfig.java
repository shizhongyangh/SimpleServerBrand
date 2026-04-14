package dev.slne.surf.serverbrandcustomizer.config;

import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import dev.slne.surf.serverbrandcustomizer.SurfServerbrandCustomizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public final class ServerbrandConfig {

  private final SurfServerbrandCustomizer plugin;
  private volatile String customServerBrand;

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

    // 纯文本解析，移除所有颜色/格式化代码
    customServerBrand = MiniMessage.miniMessage().stripTags(rawBrand);
  }

  public boolean isCustomServerBrandSet() {
    return customServerBrand != null;
  }

  public void setCustomServerBrand(String customServerBrand)
