package crystalspider.soulfired.network.unregister;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.network.NetworkEvent.Context;

/**
 * Network packet to unregister a Fire.
 */
public final class UnregisterFirePacket {
  /**
   * Fire type of the Fire to unregister.
   */
  final ResourceLocation fireType;

  /**
   * @param fireType fire type of the Fire to unregister.
   */
  public UnregisterFirePacket(ResourceLocation fireType) {
    this.fireType = fireType;
  }

  /**
   * Decodes data to instantiate an UnregisterFirePacket from the network buffer.
   * 
   * @param buffer {@link FriendlyByteBuf} from which to read data to get the {@link Fire} to register.
   */
  public UnregisterFirePacket(FriendlyByteBuf buffer) {
    fireType = buffer.readResourceLocation();
  }

  /**
   * Encodes an UnregisterFirePacket into the network buffer.
   * 
   * @param buffer {@link FriendlyByteBuf} to save data to.
   */
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(fireType);
  }

  /**
   * Handles the packet server-side.
   * 
   * @param context
   */
  public void handle(Supplier<Context> context) {
    context.get().enqueueWork(() -> DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> new SafeRunnable() { @Override public void run() { UnregisterFireClientPacket.handle(UnregisterFirePacket.this, context); } }));
    context.get().setPacketHandled(true);
  }
}
