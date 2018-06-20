package com.wumple.foodfunk;

import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.SoundEvent;

public class TileEntityFreezer extends TileEntityColdChestBase implements IInventory
{	
	// TODO render ModelChest with texture "textures/models/blocks/freezer_model.png"
	
	public TileEntityFreezer()
	{
	}
	
	/**
	 * Automatically adjust the use-by date on food items stored within the chest so they rot at half speed
	 */
	@Override
	public void update()
	{
		super.update();
		
        // Freezer Code
        
        // TODO: playSoundEffect foodfunk:freezeropen or foodfunk:freezeropen
	}
	
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
