package it.crystalnest.soul_fire_d.api.block;

import it.crystalnest.soul_fire_d.api.Fire;
import it.crystalnest.soul_fire_d.api.FireManager;
import it.crystalnest.soul_fire_d.api.block.entity.CustomCampfireBlockEntity;
import it.crystalnest.soul_fire_d.api.block.entity.DynamicBlockEntityType;
import it.crystalnest.soul_fire_d.api.type.FireTyped;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom campfire block.
 */
public class CustomCampfireBlock extends CampfireBlock implements FireTyped {
  /**
   * Fire type.
   */
  private final ResourceLocation fireType;

  /**
   * @param fireType fire type.
   * @param spawnParticles whether to spawn crackling particles.
   */
  public CustomCampfireBlock(ResourceLocation fireType, boolean spawnParticles) {
    this(
      fireType,
      spawnParticles,
      BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.PODZOL).strength(2.0F).sound(SoundType.WOOD).noOcclusion()
    );
  }

  /**
   * Use the {@link CustomCampfireBlock#CustomCampfireBlock(ResourceLocation, boolean) other constructor} if your campfire should behave similarly to the Vanilla ones (suggested).
   *
   * @param fireType fire type.
   * @param spawnParticles whether to spawn crackling particles.
   * @param properties block properties.
   */
  public CustomCampfireBlock(ResourceLocation fireType, boolean spawnParticles, Properties properties) {
    super(spawnParticles, Math.round(FireManager.getProperty(fireType, Fire::getDamage)), properties.lightLevel(state -> state.getValue(BlockStateProperties.LIT) ? FireManager.getProperty(fireType, Fire::getLight) : 0));
    this.fireType = fireType;
  }

  /**
   * Return the {@link DynamicBlockEntityType} for the custom campfire block entity.<br />
   * Override to change it with a more specific one if you registered a different {@link DynamicBlockEntityType} for a subclass of {@link CustomCampfireBlockEntity}.
   *
   * @return {@link DynamicBlockEntityType}.
   */
  protected DynamicBlockEntityType<CustomCampfireBlockEntity> getBlockEntityType() {
    return FireManager.CUSTOM_CAMPFIRE_ENTITY_TYPE.get();
  }

  /**
   * Return the {@link CampfireBlockEntity#particleTick(Level, BlockPos, BlockState, CampfireBlockEntity)} override for the custom campfire block entity.<br />
   * Override to change it with a more specific one if you subclass {@link CustomCampfireBlockEntity}.
   *
   * @return {@link CampfireBlockEntity#particleTick(Level, BlockPos, BlockState, CampfireBlockEntity)} custom override.
   */
  protected BlockEntityTicker<CampfireBlockEntity> particleTick() {
    return CustomCampfireBlockEntity::particleTick;
  }

  /**
   * Return the {@link CampfireBlockEntity#cookTick(Level, BlockPos, BlockState, CampfireBlockEntity)} override for the custom campfire block entity.<br />
   * Override to change it with a more specific one if you subclass {@link CustomCampfireBlockEntity}.
   *
   * @return {@link CampfireBlockEntity#cookTick(Level, BlockPos, BlockState, CampfireBlockEntity)} custom override.
   */
  protected BlockEntityTicker<CampfireBlockEntity> cookTick() {
    return CustomCampfireBlockEntity::cookTick;
  }

  /**
   * Return the {@link CampfireBlockEntity#cooldownTick(Level, BlockPos, BlockState, CampfireBlockEntity)} override for the custom campfire block entity.<br />
   * Override to change it with a more specific one if you subclass {@link CustomCampfireBlockEntity}.
   *
   * @return {@link CampfireBlockEntity#cooldownTick(Level, BlockPos, BlockState, CampfireBlockEntity)} custom override.
   */
  protected BlockEntityTicker<CampfireBlockEntity> cooldownTick() {
    return CustomCampfireBlockEntity::cooldownTick;
  }

  /**
   * Return a new block entity for this block.<br />
   * Override this to change the block entity created when this block is placed down.
   *
   * @param pos block position.
   * @param state block state.
   * @return new block entity.
   */
  @Override
  public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
    return new CustomCampfireBlockEntity(pos, state);
  }

  /**
   * Handles custom block entity.<br />
   * To change the block entity used, override the other specific methods in {@link CustomCampfireBlock}.
   *
   * @param level level.
   * @param state block state.
   * @param blockEntityType block entity type.
   * @param <T> block entity.
   * @return {@link BlockEntityTicker}.
   */
  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
    BlockEntityType<CustomCampfireBlockEntity> customBlockEntityType = getBlockEntityType();
    if (level.isClientSide) {
      return state.getValue(LIT) ? createTickerHelper(blockEntityType, customBlockEntityType, particleTick()) : null;
    } else {
      return state.getValue(LIT) ? createTickerHelper(blockEntityType, customBlockEntityType, cookTick()) : createTickerHelper(blockEntityType, customBlockEntityType, cooldownTick());
    }
  }

  @Override
  public ResourceLocation getFireType() {
    return fireType;
  }
}
