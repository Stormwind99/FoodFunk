package com.wumple.foodfunk.capability;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.Rot;
import com.wumple.foodfunk.capability.rot.RotInfo;
import com.wumple.util.capability.listener.network.UpdateContainerCapabilityMessage;

import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Updates the {@link IRotFinite} for a single slot of a {@link Container}.
 *
 * @author Choonster
 */
public class MessageUpdateContainerRot extends UpdateContainerCapabilityMessage<IRot, RotInfo>
{
	public MessageUpdateContainerRot(@Nullable final Direction facing, final int windowID, final int slotNumber,
			final IRot capability)
	{
		super(Rot.CAPABILITY, facing, windowID, slotNumber, capability, RotFunctions::convert);
	}

	private MessageUpdateContainerRot(@Nullable final Direction facing, final int windowID, final int slotNumber,
			final RotInfo rotInfo)
	{
		super(Rot.CAPABILITY, facing, windowID, slotNumber, rotInfo);
	}

	public static MessageUpdateContainerRot decode(final PacketBuffer buffer)
	{
		return UpdateContainerCapabilityMessage.<IRot, RotInfo, MessageUpdateContainerRot>decode(buffer,
				RotFunctions::decode, MessageUpdateContainerRot::new);
	}

	public static void encode(final MessageUpdateContainerRot message, final PacketBuffer buffer)
	{
		UpdateContainerCapabilityMessage.encode(message, buffer, RotFunctions::encode);
	}

	public static void handle(final MessageUpdateContainerRot message, final Supplier<NetworkEvent.Context> ctx)
	{
		UpdateContainerCapabilityMessage.handle(message, ctx, RotFunctions::apply);
	}
}

/*
@Override
protected void applyCapabilityData(final IRot cap, final RotInfo info)
{
    if (cap instanceof Rot)
    {
        Rot rcap = (Rot) cap;
        rcap.setInfo(info);
    }
}
*/
