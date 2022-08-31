package crystalspider.soulfired.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import crystalspider.soulfired.api.FireManager;
import crystalspider.soulfired.api.type.FireTypeChanger;
import crystalspider.soulfired.api.type.FireTyped;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Injects into {@link Entity} to alter Fire behavior for consistency.
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements FireTypeChanger {
  /**
   * Shadowed {@link Entity#level}.
   */
  @Shadow
  public World level;
  /**
   * Shadowed {@link Entity#entityData}.
   */
  @Shadow
  protected EntityDataManager entityData;

  /**
   * {@link DataParameter} to synchronize the Fire Id across client and server.
   */
  private static final DataParameter<String> DATA_FIRE_ID = EntityDataManager.defineId(Entity.class, DataSerializers.STRING);

  /**
   * Shadowed {@link Entity#defineSynchedData()}.
   */
  @Shadow
  protected abstract void defineSynchedData();
  /**
   * Shadowed {@link Entity#getRemainingFireTicks()}.
   * 
   * @return the remaining ticks the entity is set to burn for.
   */
  @Shadow
  public abstract int getRemainingFireTicks();
  /**
   * Shadowed {@link Entity#isInLava()}.
   * 
   * @return whether this entity is in lava.
   */
  @Shadow
  public abstract boolean isInLava();

  @Override
  public void setFireId(String fireId) {
    entityData.set(DATA_FIRE_ID, fireId != null ? fireId.trim() : "");
  }

  @Override
  public String getFireId() {
    return entityData.get(DATA_FIRE_ID);
  }

  /**
   * Redirects the call to {@link Entity#defineSynchedData()} inside the constructor.
   * <p>
   * Defines the {@link #DATA_FIRE_ID Fire Id data} to synchronize across client and server.
   * 
   * @param caller {@link Entity} invoking (owning) the redirected method. It's the same as {@code this} entity.
   */
  @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;defineSynchedData()V"))
  private void redirectDefineSynchedData(Entity caller) {
    entityData.define(DATA_FIRE_ID, "");
    defineSynchedData();
  }

  /**
   * Redirects the call to {@link Entity#hurt(DamageSource, float)} inside the method {@link Entity#baseTick()}.
   * <p>
   * Hurts the entity with the correct fire damage and {@link DamageSource}.
   * 
   * @param caller {@link Entity} invoking (owning) the redirected method. It's the same as this entity.
   * @param damageSource original {@link DamageSource} (normale fire).
   * @param damage original damage (normal fire).
   * @return the result of calling the redirected method.
   */
  @Redirect(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hurt(Lnet/minecraft/util/DamageSource;F)Z"))
  private boolean redirectHurt(Entity caller, DamageSource damageSource, float damage) {
    return FireManager.damageOnFire(caller, ((FireTyped) caller).getFireId(), damageSource, damage);
  }

  /**
   * Injects at the start of the method {@link Entity#setRemainingFireTicks(int)}.
   * <p>
   * Resets the FireId when this entity stops burning or catches fire from a new fire source.
   * 
   * @param ticks ticks this entity should burn for.
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "setRemainingFireTicks", at = @At(value = "HEAD"))
  private void onSetRemainingFireTicks(int ticks, CallbackInfo ci) {
    if (!level.isClientSide && ticks >= getRemainingFireTicks()) {
      setFireId(FireManager.BASE_FIRE_ID);
    }
  }

  /**
   * Injects in the method {@link Entity#saveWithoutId(CompoundNBT)} before the invocation of {@link Entity#addAdditionalSaveData(CompoundNBT)}.
   * <p>
   * If valid, saves the current FireId in the given {@link CompoundNBT}.
   * 
   * @param tag
   * @param cir {@link CallbackInfoReturnable}.
   */
  @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V"))
  private void onSaveWithoutId(CompoundNBT tag, CallbackInfoReturnable<CompoundNBT> cir) {
    if (FireManager.isFireId(getFireId())) {
      tag.putString("FireId", getFireId());
    }
  }

  /**
   * Injects in the method {@link Entity#load(CompoundNBT)} before the invocation of {@link Entity#readAdditionalSaveData(CompoundNBT)}.
   * <p>
   * Loads the FireId from the given {@link CompoundNBT}.
   * 
   * @param tag
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V"))
  private void onLoad(CompoundNBT tag, CallbackInfo ci) {
    setFireId(FireManager.ensureFireId(tag.getString("FireId")));
  }
}
