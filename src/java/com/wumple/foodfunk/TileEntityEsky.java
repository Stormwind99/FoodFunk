package com.wumple.foodfunk;

import net.minecraft.inventory.IInventory;

public class TileEntityEsky extends TileEntityColdChestBase implements IInventory
{
	// TODO render ModelChest with texture "textures/models/blocks/esky_model.png"
	
	public TileEntityEsky()
	{
	}
	
	/**
	 * Automatically adjust the use-by date on food items stored within the chest so they rot at half speed
	 */
	@Override
	public void update()
	{
		super.update();
        
        // Esky Code
		
	    // TODO: playSoundEffect foodfunk:eskyclose or foodfunk:eskyopen
	}
	
	protected long getRotTime(long time)
	{
		return time/2;
	}
	
	public String getRealName() {
	    return "container.foodfunk.esky";
	}
}
