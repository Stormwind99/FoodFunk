package com.wumple.foodfunk.capability;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.Rot;

import choonster.capability.CapabilityContainerListener;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/**
 * Syncs the {@link IHiddenBlockRevealer} capability for items in {@link Container}s.
 *
 * @author Choonster
 */
public class ContainerListenerRot extends CapabilityContainerListener<IRot>
{

    public ContainerListenerRot(final EntityPlayerMP player)
    {
        super(player, Rot.CAPABILITY, Rot.DEFAULT_FACING);
    }

    /**
     * Create an instance of the bulk update message.
     *
     * @param windowID
     *            The window ID of the Container
     * @param items
     *            The items list
     * @return The bulk update message
     */
    @Override
    protected MessageBulkUpdateContainerRots createBulkUpdateMessage(final int windowID,
            final NonNullList<ItemStack> items)
    {
        return new MessageBulkUpdateContainerRots(windowID, items);
    }

    /**
     * Create an instance of the single update message.
     *
     * @param windowID
     *            The window ID of the Container
     * @param slotNumber
     *            The slot's index in the Container
     * @param lastUseTime
     *            The capability handler instance
     * @return The single update message
     */
    @Override
    protected MessageUpdateContainerRot createSingleUpdateMessage(final int windowID, final int slotNumber,
            final IRot icap)
    {
        return new MessageUpdateContainerRot(windowID, slotNumber, icap);
    }
}
