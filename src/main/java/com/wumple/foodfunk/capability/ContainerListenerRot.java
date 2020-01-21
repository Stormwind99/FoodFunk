package com.wumple.foodfunk.capability;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.Rot;
import com.wumple.util.capability.listener.CapabilityContainerListener;
import com.wumple.util.capability.listener.network.BulkUpdateContainerCapabilityMessage;
import com.wumple.util.capability.listener.network.UpdateContainerCapabilityMessage;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;

/**
 * Syncs the capability for items in {@link Container}s.
 */
public class ContainerListenerRot extends CapabilityContainerListener<IRot>
{

	public ContainerListenerRot(final ServerPlayerEntity player)
	{
		super(player, Rot.CAPABILITY, Rot.DEFAULT_FACING);
	}

	@Override
	public <MSG> void send(PacketTarget target, MSG message)
	{
		FoodFunk.network.send(target, message);
	}

	// ------------------------------------------------------

	@Override
	protected BulkUpdateContainerCapabilityMessage<IRot, ?> createBulkUpdateMessage(final int windowID,
			final NonNullList<ItemStack> items)
	{
		return new MessageBulkUpdateContainerRots(Rot.DEFAULT_FACING, windowID, items);
	}

	@Override
	protected UpdateContainerCapabilityMessage<IRot, ?> createSingleUpdateMessage(final int windowID,
			final int slotNumber, final IRot cap)
	{
		return new MessageUpdateContainerRot(Rot.DEFAULT_FACING, windowID, slotNumber, cap);
	}
}
