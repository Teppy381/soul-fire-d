package it.crystalnest.soul_fire_d.mixin;

import it.crystalnest.soul_fire_d.api.FireManager;
import it.crystalnest.soul_fire_d.api.enchantment.FireEnchantmentHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Injects into {@link BowItem} to alter Fire behavior for consistency.
 */
@Mixin(BowItem.class)
public abstract class BowItemMixin {
  /**
   * Redirects the call to {@link AbstractArrow#setSecondsOnFire(int)} inside the method {@link BowItem#releaseUsing(ItemStack, Level, LivingEntity, int)}.<br />
   * Handles setting the arrow on the correct kind of fire if the bow has a custom fire enchantment.
   *
   * @param instance owner of the redirected method.
   * @param seconds parameter of the redirected method: number of seconds to set the arrow on fire for.
   * @param bow bow being released.
   * @param world world inside which the arrow should be generated.
   * @param user {@link LivingEntity} holding the {@code bow}.
   * @param remainingUseTicks time left before pulling the {@code bow} to the max.
   */
  @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setSecondsOnFire(I)V"))
  private void redirectSetSecondsOnFire(AbstractArrow instance, int seconds, ItemStack bow, Level world, LivingEntity user, int remainingUseTicks) {
    FireEnchantmentHelper.FireEnchantment fireEnchantment = FireEnchantmentHelper.getWhichFlame(bow);
    if (fireEnchantment.isApplied()) {
      FireManager.setOnFire(instance, seconds, fireEnchantment.getFireType());
    }
  }
}
