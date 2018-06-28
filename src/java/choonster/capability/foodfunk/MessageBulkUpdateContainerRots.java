package choonster.capability.foodfunk;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capabilities.rot.IRot;
import com.wumple.foodfunk.capabilities.rot.Rot;
import com.wumple.foodfunk.capabilities.rot.RotProvider;

import choonster.capability.MessageBulkUpdateContainerCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Updates the {@link Rot} for each slot of a {@link Container}.
 *
 * @author Choonster
 */
public class MessageBulkUpdateContainerRots extends MessageBulkUpdateContainerCapability<IRot, RotInfo> {

	@SuppressWarnings("unused")
	public MessageBulkUpdateContainerRots() {
		super(RotProvider.CAPABILITY);
	}

	public MessageBulkUpdateContainerRots(final int windowID, final NonNullList<ItemStack> items) {
		super(RotProvider.CAPABILITY, null, windowID, items);
	}

	/**
	 * Convert a capability handler instance to a data instance.
	 *
	 * @param cap The handler
	 * @return The data instance
	 */
	@Nullable
	@Override
	protected RotInfo convertCapabilityToData(final IRot cap) {
		if (cap instanceof Rot) {
			return ((Rot) cap).getInfo();
		} else {
			return null;
		}
	}

	/**
	 * Read a data instance from the buffer.
	 *
	 * @param buf The buffer
	 */
	@Override
	protected RotInfo readCapabilityData(final ByteBuf buf) {
		return MessageUpdateContainerRot.readRotInfo(buf);
	}

	/**
	 * Write a data instance to the buffer.
	 *
	 * @param buf           The buffer
	 * @param rotInfo The data instance
	 */
	@Override
	protected void writeCapabilityData(final ByteBuf buf, final RotInfo rotInfo) {
		MessageUpdateContainerRot.writeRotInfo(buf, rotInfo);
	}

	public static class Handler extends MessageBulkUpdateContainerCapability.Handler<IRot, RotInfo, MessageBulkUpdateContainerRots> {

		/**
		 * Apply the capability data from the data instance to the capability handler instance.
		 *
		 * @param rotItem The capability handler instance
		 * @param rotInfo    The data instance
		 */
		@Override
		protected void applyCapabilityData(final IRot rotItem, final RotInfo rotInfo) {
			if (rotItem instanceof Rot) {
				((Rot) rotItem).setInfo(rotInfo);
			}
		}
	}
}
