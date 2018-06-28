package com.wumple.foodfunk.coldchest;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.RotHandler;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;

import choonster.capability.foodfunk.MessageBulkUpdateContainerRots;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

public abstract class TileEntityColdChest extends TileEntityBaseChest implements IInventory, ITickable
{
	// ticks to wait until rot refresh of contents
	static final int slowInterval = 30;
	static final int fastInterval = 4; // when someone has chest open

	// ticks since last rot refresh of contents
	int tick = 0;
	long lastCheck = ConfigHandler.DAYS_NO_ROT;

	public TileEntityColdChest() {
		super();
	}

	/**
	 * Automatically adjust the use-by date on food items stored within the chest so they rot at half speed
	 */
	@Override
	public void update() {
		super.update();

		// Cold chest code

		if ((getWorld() == null) || getWorld().isRemote)
		{
			return;
		}

		long worldTime = getWorld().getTotalWorldTime();

		if(lastCheck <= ConfigHandler.DAYS_NO_ROT)
		{
			lastCheck = worldTime;
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

		int interval = (this.numPlayersUsing > 0) ? fastInterval : slowInterval;

		if (tick < interval)
		{
			tick++;
			return;
		}
		
		tick = 0;

		long time = worldTime - lastCheck;
		lastCheck = worldTime;
		
		final NonNullList<ItemStack> syncableItemsList = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);

		int numDirty = 0;
		
		for(int i = 0; i < this.getSizeInventory(); i++)
		{
			ItemStack stack = this.getStackInSlot(i);

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
			markDirty();

			// update each client/player that has this container open
			NonNullList<EntityPlayer> users = getPlayersUsing();
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

	abstract protected long getRotTime(long time);

	@Override
	public void readFromNBT(NBTTagCompound tags) {
		super.readFromNBT(tags);

		if(tags.hasKey("RotCheck"))
		{
			lastCheck = tags.getLong("rotLastCheck");
		} 
		else
		{
			lastCheck = ConfigHandler.DAYS_NO_ROT;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tags) {
		super.writeToNBT(tags);

		tags.setLong("rotLastCheck", lastCheck);

		return tags;
	}

}