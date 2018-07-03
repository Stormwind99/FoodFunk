package com.wumple.foodfunk.capability.preserving;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.capability.MessageBulkUpdateContainerRots;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.Rot;
import com.wumple.foodfunk.capability.rot.RotCapHelper;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.misc.ContainerUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@Mod.EventBusSubscriber
public class Preserving implements IPreserving
{
    // ticks to wait until rot refresh of contents
    static public final int slowInterval = 90;
    static public final int fastInterval = 4; // when someone has chest open

    // transient data
    // ticks since last rot refresh of contents
    protected int tick = 0;
    protected TileEntity entity = null;
    protected int preservingRatio = 0;

    // persisted data
    long lastCheckTime = ConfigHandler.DAYS_NO_ROT;

    // ----------------------------------------------------------------------
    // Init

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IPreserving.class, new PreservingStorage(), () -> new Preserving());
    }

    Preserving()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    Preserving(TileEntity owner)
    {
        entity = owner;
        MinecraftForge.EVENT_BUS.register(this);
    }

    // ----------------------------------------------------------------------
    // IPreserving

    public long getLastCheckTime()
    {
        return lastCheckTime;
    }

    public void setLastCheckTime(long time)
    {
        lastCheckTime = time;
    }

    /*
     * Set the owner of this capability, and init based on that owner
     */
    public void setOwner(TileEntity ownerIn)
    {
        if (entity != ownerIn)
        {
            entity = ownerIn;
            lastCheckTime = Rot.getLastWorldTimestamp();
        }
    }

    /**
     * Automatically adjust the use-by date on food items stored within to slow or stop rot
     */
    public void freshenContents()
    {
        if ((entity.getWorld() == null) || entity.getWorld().isRemote)
        {
            return;
        }

        // tick of 0 represents "cache any transient data" like preserving ratio
        if (tick == 0)
        {
            preservingRatio = ConfigHandler.Preserving.getPreservingRatio(entity);
        }

        if (tick < slowInterval)
        {
            tick++;
            return;
        }

        // reset to 1 since 0 is special "cache any transient data" state
        tick = 1;

        long worldTime = entity.getWorld().getTotalWorldTime();

        if (lastCheckTime <= ConfigHandler.DAYS_NO_ROT)
        {
            lastCheckTime = worldTime;
        }

        long time = worldTime - lastCheckTime;
        lastCheckTime = worldTime;

        freshenContentsAny(time, worldTime);
    }

    // ----------------------------------------------------------------------
    // Internal

    /**
     * Automatically adjust the use-by date on food items stored within to slow or stop rot
     */
    protected void freshenContentsAny(long time, long worldTime)
    {
        if (entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
        {
            IItemHandler capability = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            freshenTheseContents(capability, time, worldTime);
        }
        else if (entity instanceof IInventory)
        {
            IInventory inventory = (IInventory) entity;
            freshenTheseContents(inventory, time, worldTime);
        }
    }

    protected void freshenTheseContents(IInventory inventory, long time, long worldTime)
    {
        final NonNullList<ItemStack> syncableItemsList = NonNullList.withSize(inventory.getSizeInventory(),
                ItemStack.EMPTY);

        boolean dirty = false;

        ItemStack itemToSearchFor = null;

        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if ((itemToSearchFor == null) && (!stack.isEmpty()))
            {
                itemToSearchFor = stack;
            }

            dirty |= freshenStack(stack, time, worldTime, i, syncableItemsList);
        }

        if (dirty)
        {
            entity.markDirty();

            sendContainerUpdate(entity, itemToSearchFor, syncableItemsList);
        }
    }

    protected void freshenTheseContents(IItemHandler inventory, long time, long worldTime)
    {
        final NonNullList<ItemStack> syncableItemsList = NonNullList.withSize(inventory.getSlots(), ItemStack.EMPTY);

        boolean dirty = false;

        ItemStack itemToSearchFor = null;

        for (int i = 0; i < inventory.getSlots(); i++)
        {
            // TODO - investigate if IItemHandler.extractItem() needed instead
            ItemStack stack = inventory.getStackInSlot(i);

            if ((itemToSearchFor == null) && (!stack.isEmpty()))
            {
                itemToSearchFor = stack;
            }

            // TODO move to Rot
            dirty |= freshenStack(stack, time, worldTime, i, syncableItemsList);
        }

        // TODO move to Rot, hopefully ContainerListenerRot will eliminate this
        if (dirty)
        {
            entity.markDirty();

            sendContainerUpdate(entity, itemToSearchFor, syncableItemsList);
        }
    }

    protected boolean freshenStack(ItemStack stack, long time, long worldTime, int index,
            NonNullList<ItemStack> syncableItemsList)
    {
        if ((stack == null) || stack.isEmpty())
        {
            return false;
        }

        IRot cap = RotCapHelper.getRot(stack);

        if (cap != null)
        {
            cap.reschedule(time);
            syncableItemsList.set(index, stack);
        }

        syncableItemsList.set(index, stack);
        return true;
    }

    // TODO have Rot or ContainerListenerRot do this instead
    protected void sendContainerUpdate(TileEntity entity, ItemStack itemToSearchFor,
            NonNullList<ItemStack> syncableItemsList)
    {
        // update each client/player that has this container open
        NonNullList<EntityPlayer> users = ContainerUtil.getPlayersWithContainerOpen(entity, itemToSearchFor);
        if (!users.isEmpty())
        {
            for (EntityPlayer player : users)
            {
                if (player instanceof EntityPlayerMP)
                {
                    Container containerToSend = player.openContainer;
                    final MessageBulkUpdateContainerRots message = new MessageBulkUpdateContainerRots(
                            containerToSend.windowId, syncableItemsList);
                    // Don't send the message if there's nothing to update
                    if (message.hasData())
                    {
                        FoodFunk.network.sendTo(message, (EntityPlayerMP) player);
                    }
                }
            }
        }

        // TODO: consider player.inventoryContainer
    }

    /**
     * Automatically adjust the use-by date on food items stored within the chest so don't rot
     */
    protected long getRotTime(long time)
    {
        return (time * preservingRatio) / 100;
    }

    protected void handleOnTick(World world)
    {
        if (entity != null)
        {
            if (entity.isInvalid())
            {
                MinecraftForge.EVENT_BUS.unregister(this);
                entity = null;
            }
            else if (!world.isRemote)
            {
                freshenContents();
            }
        }
    }

    // ----------------------------------------------------------------------
    // Event Handlers

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event)
    {
        handleOnTick(event.world);
    }
}
