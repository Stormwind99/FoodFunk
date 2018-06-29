package com.wumple.foodfunk.capabilities.preserving;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.RotHandler;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;

import choonster.capability.foodfunk.MessageBulkUpdateContainerRots;
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

@Mod.EventBusSubscriber
public class Preserving implements IPreserving
{
	// ticks to wait until rot refresh of contents
	static final int slowInterval = 90;
	static final int fastInterval = 4; // when someone has chest open

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
		entity = ownerIn;
	}
	
	// ----------------------------------------------------------------------
	


	/**
	 * Automatically adjust the use-by date on food items stored within the chest so they rot at half speed
	 */
	public void rotUpdate()
	{
		// Cold chest code

		if ( (entity.getWorld() == null) ||	entity.getWorld().isRemote ||
				!(entity instanceof IInventory) )
		{
			return;
		}
		
		IInventory inventory = (IInventory)entity;		

		long worldTime = entity.getWorld().getTotalWorldTime();

		if(lastCheckTime <= ConfigHandler.DAYS_NO_ROT)
		{
			lastCheckTime = worldTime;
		}

		/*
		 * MAYBE small bug - when chest open and tooltip up, rot can decrease.  Closing/re-opening chest fixes it.
		 * Tried below: refresh more often when this.numPlayersUsing > 0
		 * 
		 * Other ideas:
		 * Could be chest tick rate vs login/logout time, or tooltip update time vs rot refresh
		 * Might need to fix lastCheck with persisted fraction upon load
		 * Maybe even just contained ItemStack's NBT data not getting refreshed when chest open
		 */

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

		long time = worldTime - lastCheckTime;
		lastCheckTime = worldTime;
		
		final NonNullList<ItemStack> syncableItemsList = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);

		int numDirty = 0;
		
		for(int i = 0; i < inventory.getSizeInventory(); i++)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if((stack == null) || stack.isEmpty())
			{
				continue;
			}

			ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(stack);

			if ((!ConfigContainer.rotEnabled) || (!RotHandler.doesRot(rotProps)))
			{
				stack = RotHandler.clearRotData(stack);
			} 
			else
			{
				stack = RotHandler.rescheduleRot(stack, getRotTime(time));
			}
			
			syncableItemsList.set(i, stack);
			numDirty++;
		}
		
		if (numDirty > 0)
		{
			entity.markDirty();

			// update each client/player that has this container open
			NonNullList<EntityPlayer> users = getPlayersWithContainerOpen(entity);
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
		}	
	}
	
	public static NonNullList<EntityPlayer> getPlayersWithContainerOpen(TileEntity entity)
	{
		int i = entity.getPos().getX();
		int j = entity.getPos().getY();
		int k = entity.getPos().getZ();
		
		NonNullList<EntityPlayer> users = NonNullList.create();
		
		for (EntityPlayer entityplayer : entity.getWorld().getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F))))
		{
			if (entityplayer.openContainer instanceof ContainerChest)
			{
				IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();

				if (iinventory == entity) // || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this))
				{
					users.add(entityplayer);
				}
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
