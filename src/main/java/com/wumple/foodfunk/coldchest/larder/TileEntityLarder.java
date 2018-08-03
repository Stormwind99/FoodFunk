package com.wumple.foodfunk.coldchest.larder;

import com.wumple.foodfunk.ObjectHandler;
import com.wumple.foodfunk.coldchest.TileEntityColdChest;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;

public class TileEntityLarder extends TileEntityColdChest implements IInventory, ITickable
{
    public TileEntityLarder()
    {
    }

    /**
     * Automatically adjust the use-by date on food items stored within the chest so they rot at half speed
     */
    protected long getRotTime(long time)
    {
        return time / 2;
    }

    public String getRealName()
    {
        return "container.foodfunk.larder";
    }

    public SoundEvent getOpenSoundEvent()
    {
        return ObjectHandler.larder_open;
    }

    public SoundEvent getCloseSoundEvent()
    {
        return ObjectHandler.larder_close;
    }
}
