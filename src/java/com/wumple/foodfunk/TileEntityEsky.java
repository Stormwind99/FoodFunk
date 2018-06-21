package com.wumple.foodfunk;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;

public class TileEntityEsky extends TileEntityColdChest implements IInventory, ITickable
{
	public TileEntityEsky()
	{
	}

	/**
	 * Automatically adjust the use-by date on food items stored within the chest so they rot at half speed
	 */
	protected long getRotTime(long time)
	{
		return time/2;
	}

	public String getRealName() {
		return "container.foodfunk.esky";
	}

	public SoundEvent getOpenSoundEvent()
	{
		return ObjectHandler.esky_open;
	}

	public SoundEvent getCloseSoundEvent()
	{
		return ObjectHandler.esky_close;
	}
}
