package com.wumple.foodfunk.coldchest.icebox;

import com.wumple.foodfunk.ObjectHandler;
import com.wumple.foodfunk.coldchest.TileEntityColdChest;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;

public class TileEntityIcebox extends TileEntityColdChest implements IInventory, ITickable
{
    public TileEntityIcebox()
    {
    }

    /**
     * Automatically adjust the use-by date on food items stored within the chest so don't rot
     */
    protected long getRotTime(long time)
    {
        return time;
    }

    public String getRealName()
    {
        return "container.foodfunk.icebox";
    }

    public SoundEvent getOpenSoundEvent()
    {
        return ObjectHandler.icebox_open;
    }

    public SoundEvent getCloseSoundEvent()
    {
        return ObjectHandler.icebox_close;
    }
}
