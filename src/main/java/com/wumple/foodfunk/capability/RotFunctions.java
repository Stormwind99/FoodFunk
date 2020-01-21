package com.wumple.foodfunk.capability;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.RotInfo;

import net.minecraft.network.PacketBuffer;

/**
 * Functions used by the capability update messages.
 */
class RotFunctions
{
	static RotInfo convert(final IRot capability)
	{
		return new RotInfo(capability.getDate(), capability.getTime());
	}

	static RotInfo decode(final PacketBuffer buffer)
	{
		final long date = buffer.readLong();
		final long time = buffer.readLong();

		return new RotInfo(date, time);
	}

	static void encode(final RotInfo RotInfo, final PacketBuffer buffer)
	{
		buffer.writeLong(RotInfo.getDate());
		buffer.writeLong(RotInfo.getTime());
	}

	static void apply(final IRot capability,
			final RotInfo rotInfo)
	{
		if (capability instanceof IRot)
		{
			((IRot) capability).setInfo(rotInfo);
		}
	}
}
