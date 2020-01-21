package com.wumple.foodfunk.capability;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.Rot;
import com.wumple.foodfunk.capability.rot.RotInfo;
import com.wumple.util.capability.listener.network.BulkUpdateContainerCapabilityMessage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Updates the capability for each slot of a {@link Container}.
 */
public class MessageBulkUpdateContainerRots extends BulkUpdateContainerCapabilityMessage<IRot, RotInfo>
{
	public MessageBulkUpdateContainerRots(@Nullable final Direction facing, final int windowID,
			final NonNullList<ItemStack> items)
	{
		super(Rot.CAPABILITY, facing, windowID, items, RotFunctions::convert);
	}

	private MessageBulkUpdateContainerRots(@Nullable final Direction facing, final int windowID,
			final Int2ObjectMap<RotInfo> capabilityData)
	{
		super(Rot.CAPABILITY, facing, windowID, capabilityData);
	}

	public static MessageBulkUpdateContainerRots decode(final PacketBuffer buffer)
	{
		return BulkUpdateContainerCapabilityMessage.<IRot, RotInfo, MessageBulkUpdateContainerRots>decode(buffer,
				RotFunctions::decode, MessageBulkUpdateContainerRots::new);
	}

	public static void encode(final MessageBulkUpdateContainerRots message, final PacketBuffer buffer)
	{
		BulkUpdateContainerCapabilityMessage.encode(message, buffer, RotFunctions::encode);
	}

	public static void handle(final MessageBulkUpdateContainerRots message, final Supplier<NetworkEvent.Context> ctx)
	{
		BulkUpdateContainerCapabilityMessage.handle(message, ctx, RotFunctions::apply);
	}
}
