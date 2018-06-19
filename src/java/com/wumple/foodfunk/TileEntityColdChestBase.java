package com.wumple.foodfunk;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityColdChestBase extends TileEntityChestBase implements IInventory {

	int tick = 0;
	int interval = 30;
	long lastCheck = ConfigHandler.DAYS_NO_ROT;

	public TileEntityColdChestBase() {
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
		
		if(lastCheck <= ConfigHandler.DAYS_NO_ROT)
		{
			lastCheck = getWorld().getTotalWorldTime();
		}
		
		if(tick >= interval)
		{
			tick = 0;
			
			long time = getWorld().getTotalWorldTime() - lastCheck;
			lastCheck = getWorld().getTotalWorldTime();
			
			for(int i = 0; i < this.getSizeInventory(); i++)
			{
				ItemStack stack = this.getStackInSlot(i);
				
				if((stack == null) || stack.isEmpty())
				{
					continue;
				}
				
				ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(stack);
				
				if ((!ConfigHandler.rotEnabled) || (rotProps == null) || (!rotProps.doesRot()))
				{
					RotHandler.clearRotData(stack);
				} 
				else
				{
					RotHandler.rescheduleRot(stack, getRotTime(time));
				}
			}
			
			this.markDirty();
		} else
		{
			tick++;
		}
	}
	
	abstract protected long getRotTime(long time);

	@Override
	public void readFromNBT(NBTTagCompound tags) {
	    super.readFromNBT(tags);
	    
	    if(tags.hasKey("RotCheck"))
	    {
	    	this.lastCheck = tags.getLong("RotCheck");
	    } else
	    {
	    	this.lastCheck = ConfigHandler.DAYS_NO_ROT;
	    }
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tags) {
		super.writeToNBT(tags);
	
	    tags.setLong("RotCheck", this.lastCheck);
	    
	    return tags;
	}

}