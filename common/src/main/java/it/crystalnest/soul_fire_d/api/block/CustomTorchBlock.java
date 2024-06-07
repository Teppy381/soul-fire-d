package it.crystalnest.soul_fire_d.api.block;

import it.crystalnest.soul_fire_d.api.FireManager;
import it.crystalnest.soul_fire_d.api.type.FireTyped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CustomTorchBlock extends TorchBlock implements FireTyped {
  private final ResourceLocation fireType;

  private final Supplier<SimpleParticleType> type;

  public CustomTorchBlock(ResourceLocation fireType, Supplier<SimpleParticleType> type, Properties properties) {
    // noinspection DataFlowIssue
    super(null, properties.lightLevel(state -> FireManager.getLight(fireType)));
    this.fireType = fireType;
    this.type = type;
  }

  @Override
  public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
    // noinspection ConstantValue
    if (this.flameParticle == null) {
      this.flameParticle = type.get();
    }
    super.animateTick(state, level, pos, random);
  }

  @Override
  public ResourceLocation getFireType() {
    return fireType;
  }
}
