package crystalspider.soulfired.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import crystalspider.soulfired.api.FireManager;
import crystalspider.soulfired.api.client.FireClientManager;
import crystalspider.soulfired.api.type.FireTyped;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Injects into {@link InGameOverlayRenderer} to alter Fire behavior for consistency.
 */
@Mixin(InGameOverlayRenderer.class)
public abstract class InGameOverlayRendererMixin {
  /**
   * Modifies the assignment value returned by the call to {@link SpriteIdentifier#getSprite()} in the method {@link InGameOverlayRenderer#renderFireOverlay(MinecraftClient, MatrixStack)}.
   * <p>
   * Assigns the correct sprite for the Fire Type the player is burning from.
   * 
   * @param value original sprite returned by the modified method.
   * @param poseStack
   * @param multiBufferSource
   * @param entity {@link Entity} that's burning.
   * @return {@link Sprite} to assign.
   */
	@ModifyVariable(method = "renderFireOverlay", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/util/SpriteIdentifier;getSprite()Lnet/minecraft/client/texture/Sprite;"))
  private static Sprite onRenderFireOverlay(Sprite value, MinecraftClient client, MatrixStack matrices) {
    Identifier fireType = ((FireTyped) client.player).getFireType();
    if (FireManager.isRegisteredType(fireType)) {
      return FireClientManager.getSprite1(fireType);
    }
    return value;
  }
}
