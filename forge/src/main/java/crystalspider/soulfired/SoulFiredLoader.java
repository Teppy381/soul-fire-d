package crystalspider.soulfired;

import com.mojang.serialization.Codec;

import crystalspider.soulfired.api.FireManager;
import crystalspider.soulfired.config.SoulFiredConfig;
import crystalspider.soulfired.loot.ChestLootModifier;
import crystalspider.soulfired.network.SoulFiredNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Soul fire'd mod loader.
 */
@Mod(SoulFiredLoader.MODID)
public final class SoulFiredLoader {
  /**
   * ID of this mod.
   */
  public static final String MODID = "soulfired";

  /**
   * Network channel protocol version.
   */
  public static final int PROTOCOL_VERSION = 1_20__3_2;
  /**
   * {@link SimpleChannel} instance for compatibility client-server.
   */
  public static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation(MODID, "main")).networkProtocolVersion(PROTOCOL_VERSION).acceptedVersions((status, version) -> version == PROTOCOL_VERSION).simpleChannel();

  /**
   * {@link Codec<? extends IGlobalLootModifier>} {@link DeferredRegister deferred register}.
   */
  public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

  /**
   * {@link RegistryObject} for {@link Codec} of {@link ChestLootModifier}.
   */
  public static final RegistryObject<Codec<ChestLootModifier>> CHEST_LOOT_MODIFIER = LOOT_MODIFIERS.register("chest_loot_modifier", ChestLootModifier.CODEC);

  /**
   * Registers {@link SoulFiredConfig}, {@link #LOOT_MODIFIERS}, Soul Fire and {@link SoulFiredNetwork}.
   */
  public SoulFiredLoader() {
    ModLoadingContext.get().registerConfig(Type.COMMON, SoulFiredConfig.SPEC);
    LOOT_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    FireManager.registerFire(
      FireManager.fireBuilder(FireManager.SOUL_FIRE_TYPE)
        .setDamage(2)
        .setFireAspectConfig(builder -> builder
          .setEnabled(SoulFiredConfig::getEnableSoulFireAspect)
          .setIsDiscoverable(SoulFiredConfig::getEnableSoulFireAspectDiscovery)
          .setIsTradeable(SoulFiredConfig::getEnableSoulFireAspectTrades)
          .setIsTreasure(SoulFiredConfig::getEnableSoulFireAspectTreasure)
        )
        .setFlameConfig(builder -> builder
          .setEnabled(SoulFiredConfig::getEnableSoulFlame)
          .setIsDiscoverable(SoulFiredConfig::getEnableSoulFlameDiscovery)
          .setIsTradeable(SoulFiredConfig::getEnableSoulFlameTrades)
          .setIsTreasure(SoulFiredConfig::getEnableSoulFlameTreasure)
        )
      .build()
    );
    SoulFiredNetwork.register();
  }
}
