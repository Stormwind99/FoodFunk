package com.wumple.foodfunk.capability;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capabilities.rot.IRot;
import com.wumple.foodfunk.capabilities.rot.Rot;
import com.wumple.foodfunk.capabilities.rot.RotProvider;

import choonster.capability.MessageUpdateContainerCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Updates the {@link Rot} for a single slot of a {@link Container}.
 *
 * @author Choonster
 */
public class MessageUpdateContainerRot extends MessageUpdateContainerCapability<IRot, RotInfo> {

	@SuppressWarnings("unused")
	public MessageUpdateContainerRot() {
		super(RotProvider.CAPABILITY);
	}

	public MessageUpdateContainerRot(final int windowID, final int slotNumber, final IRot cap) {
		super(RotProvider.CAPABILITY, RotProvider.DEFAULT_FACING, windowID, slotNumber, cap);
	}

	/**
	 * Convert the capability handler instance to a data instance.
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
	 * Read the capability data from the buffer.
	 *
	 * @param buf The buffer
	 * @return The data instance
	 */
	@Override
	protected RotInfo readCapabilityData(final ByteBuf buf) {
		return readRotInfo(buf);
	}

	/**
	 * Write the capability data to the buffer.
	 *
	 * @param buf           The buffer
	 * @param fluidTankInfo The data instance
	 */
	@Override
	protected void writeCapabilityData(final ByteBuf buf, final RotInfo cap) {
		writeRotInfo(buf, cap);
	}

	/**
	 * Read a {@link RotInfo} from the buffer
	 *
	 * @param buf The buffer
	 * @return The RotInfo
	 */
	static RotInfo readRotInfo(final ByteBuf buf) {
		final NBTTagCompound tagCompound = ByteBufUtils.readTag(buf);

		return new RotInfo(tagCompound);
	}

	/**
	 * Write a {@link RotInfo} to the buffer
	 *
	 * @param buf           The buffer
	 * @param fluidTankInfo The RotInfo
	 */
	static void writeRotInfo(final ByteBuf buf, final RotInfo info) {
		final NBTTagCompound tagCompound = new NBTTagCompound();

		info.writeToNBT(tagCompound);
		ByteBufUtils.writeTag(buf, tagCompound);
	}

	public static class Handler extends MessageUpdateContainerCapability.Handler<IRot, RotInfo, MessageUpdateContainerRot> {

		/**
		 * Apply the capability data from the data instance to the capability handler instance.
		 *
		 * @param cap The capability handler instance
		 * @param fluidTankInfo    The data
		 */
		@Override
		protected void applyCapabilityData(final IRot cap, final RotInfo info) {
			if (cap instanceof Rot) {
			    Rot rcap = (Rot)cap;
				rcap.setInfo(info);
			}
		}
	}
}
