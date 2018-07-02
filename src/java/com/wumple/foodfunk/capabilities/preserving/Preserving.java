package com.wumple.foodfunk.capabilities.preserving;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.RotHandler;
import com.wumple.foodfunk.capabilities.rot.Rot;
import com.wumple.foodfunk.capability.MessageBulkUpdateContainerRots;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
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

    // transient
    // ticks since last rot refresh of contents
    int tick = 0;
    TileEntity entity = null;
    int preservingRatio = 0;

    // persisted
    long lastCheckTime = ConfigHandler.DAYS_NO_ROT;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IPreserving.class, new PreservingStorage(), () -> new Preserving() );
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

    public void setOwner(TileEntity ownerIn)
    {
        if (entity != ownerIn)
        {
            entity = ownerIn;
            lastCheckTime = Rot.lastWorldTimestamp;
        }
    }

    // ----------------------------------------------------------------------



    /**
     * Automatically adjust the use-by date on food items stored within to slow or stop rot
     */
    public void rotUpdate()
    {
        if ( (entity.getWorld() == null) ||	entity.getWorld().isRemote )
        {
            return;
        }

        // tick of 0 represents "cache any transient data" like preserving ratio
        if (tick == 0)
        {
            preservingRatio = RotHandler.getPreservingRatio(entity);
        }

        if (tick < slowInterval)
        {
            tick++;
            return;
        }

        // reset to 1 since 0 is special "cache any transient data" state
        tick = 1;

        long worldTime = entity.getWorld().getTotalWorldTime();

        if(lastCheckTime <= ConfigHandler.DAYS_NO_ROT)
        {
            lastCheckTime = worldTime;
        }

        long time = worldTime - lastCheckTime;
        lastCheckTime = worldTime;

        rotUpdateInternal(time, worldTime);
    }

    protected void rotUpdateInternal(long time, long worldTime)
    {
        if (entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
        {
            IItemHandler capability = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            rotUpdateInternal(capability, time, worldTime);
        }
        else if (entity instanceof IInventory)
        {	
            IInventory inventory = (IInventory)entity;
            rotUpdateInternal(inventory, time, worldTime);
        }
    }

    public void rotUpdateInternal(IInventory inventory, long time, long worldTime)
    {
        final NonNullList<ItemStack> syncableItemsList = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);

        boolean dirty = false;

        ItemStack itemToSearchFor = null;
        
        for(int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);

            if ((itemToSearchFor == null) && (!stack.isEmpty()))
            {
                itemToSearchFor = stack;
            }

            dirty |= checkStackRot(stack, time, worldTime, i, syncableItemsList);
        }

        if (dirty)
        {
            entity.markDirty();

            sendContainerUpdate(entity, itemToSearchFor, syncableItemsList);
        }
    }

    public void rotUpdateInternal(IItemHandler inventory, long time, long worldTime)
    {
        final NonNullList<ItemStack> syncableItemsList = NonNullList.withSize(inventory.getSlots(), ItemStack.EMPTY);

        boolean dirty = false;
        
        ItemStack itemToSearchFor = null;

        for(int i = 0; i < inventory.getSlots(); i++)
        {
            // TODO - investigate if IItemHandler.extractItem() needed instead
            ItemStack stack = inventory.getStackInSlot(i);
            
            if ((itemToSearchFor == null) && (!stack.isEmpty()))
            {
                itemToSearchFor = stack;
            }

            dirty |= checkStackRot(stack, time, worldTime, i, syncableItemsList);
        }

        if (dirty)
        {
            entity.markDirty();

            sendContainerUpdate(entity, itemToSearchFor, syncableItemsList);
        }
    }	

    protected boolean checkStackRot(ItemStack stack, long time, long worldTime, int index, NonNullList<ItemStack> syncableItemsList)
    {
        if((stack == null) || stack.isEmpty())
        {
            return false;
        }

        ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(stack);

        if ((!ConfigContainer.enabled) || (!RotHandler.doesRot(rotProps)))
        {
            stack = RotHandler.clearRotData(stack);
        } 
        else
        {
            stack = RotHandler.rescheduleRot(stack, getRotTime(time), worldTime);
        }

        syncableItemsList.set(index, stack);
        return true;
    }


    protected void sendContainerUpdate(TileEntity entity, ItemStack itemToSearchFor, NonNullList<ItemStack> syncableItemsList)
    {
        // update each client/player that has this container open
        NonNullList<EntityPlayer> users = getPlayersWithContainerOpen(entity, itemToSearchFor);
        if (!users.isEmpty())
        {
            for (EntityPlayer player : users)
            {
                if (player instanceof EntityPlayerMP)
                {
                    Container containerToSend = player.openContainer;
                    final MessageBulkUpdateContainerRots message = new MessageBulkUpdateContainerRots(containerToSend.windowId, syncableItemsList);
                    // Don't send the message if there's nothing to update	
                    if (message.hasData())
                    {
                        FoodFunk.network.sendTo(message, (EntityPlayerMP)player);
                    }
                }
            }
        }

        // TODO: consider player.inventoryContainer
    }

    public static NonNullList<EntityPlayer> getPlayersWithContainerOpen(TileEntity container, ItemStack itemToSearchFor)
    {
        int i = container.getPos().getX();
        int j = container.getPos().getY();
        int k = container.getPos().getZ();

        NonNullList<EntityPlayer> users = NonNullList.create();

        for (EntityPlayer player : container.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F))))
        {
            boolean add = false;

            if (player.openContainer instanceof ContainerChest)
            {
                IInventory iinventory = ((ContainerChest)player.openContainer).getLowerChestInventory();

                if (iinventory == container) // || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this))
                {
                    add = true;
                }
            }
            
            // horrid hack - if container contains item we know about, it is the container we are looking for
            if (!add && (player.openContainer != null) && (itemToSearchFor != null) && (!itemToSearchFor.isEmpty()))
            {
                NonNullList<ItemStack> stack = player.openContainer.getInventory();
                
                if (stack.contains(itemToSearchFor))
                {
                    add = true;
                }
            }

            // if player.openContainer.listeners

            if (add)
            {
                users.add(player);
            }
        }

        return users;
    }

    /**
     * Automatically adjust the use-by date on food items stored within the chest so don't rot
     */
    protected long getRotTime(long time)
    {
        return (time * preservingRatio) / 100;
    }

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event)
    {
        if (entity != null)
        {
            if (entity.isInvalid())
            {
                MinecraftForge.EVENT_BUS.unregister(this);
            }
            else if (!event.world.isRemote)
            {
                rotUpdate();
            }
        }
    }
}
