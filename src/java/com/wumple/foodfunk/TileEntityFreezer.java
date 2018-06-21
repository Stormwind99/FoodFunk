package com.wumple.foodfunk;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;

public class TileEntityFreezer extends TileEntityColdChest implements IInventory, ITickable
{	
	public TileEntityFreezer()
	{
	}

	/**
	 * Automatically adjust the use-by date on food items stored within the chest so don't rot
	 */
	protected long getRotTime(long time)
	{
		return time;
	}

	public String getRealName() {
		return "container.foodfunk.freezer";
	}	

	public SoundEvent getOpenSoundEvent()
	{
		return ObjectHandler.freezer_open;
	}

	public SoundEvent getCloseSoundEvent()
	{
		return ObjectHandler.freezer_close;
	}
}
