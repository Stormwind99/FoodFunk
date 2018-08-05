package com.wumple.foodfunk.capability;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.Rot;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.util.container.capabilitylistener.CapabilityContainerListener;
import com.wumple.util.container.capabilitylistener.MessageBulkUpdateContainerCapability;
import com.wumple.util.container.capabilitylistener.MessageUpdateContainerCapability;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
    
    @Override
    protected void sendTo(IMessage message, EntityPlayerMP player)
    {
    	FoodFunk.network.sendTo(message, player);
    }
    
    // /*
    // for debugging
    @Override
    public void sendAllContents(final Container containerToSend, final NonNullList<ItemStack> itemsList)
    {
        if (ConfigContainer.zdebugging.debug)
        {
            FoodFunk.logger.info("CLR sending " + containerToSend + " items " + itemsList);
        }
    	super.sendAllContents(containerToSend, itemsList);
    }
    
    // for debugging
    @Override
    public void sendSlotContents(final Container containerToSend, final int slotInd,
            final ItemStack stack)
    {
        if (ConfigContainer.zdebugging.debug)
        {
            FoodFunk.logger.info("CLR sending " + containerToSend + " slot " + slotInd + " stack " + stack);
        }
    	super.sendSlotContents(containerToSend, slotInd, stack);
    }
    // */

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
    protected MessageBulkUpdateContainerCapability<IRot, ?> createBulkUpdateMessage(final int windowID,
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
    protected MessageUpdateContainerCapability<IRot, ?> createSingleUpdateMessage(final int windowID, final int slotNumber,
            final IRot icap)
    {
        return new MessageUpdateContainerRot(windowID, slotNumber, icap);
    }
}
