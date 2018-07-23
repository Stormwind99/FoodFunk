package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.misc.TypeIdentifier;

import net.minecraft.item.ItemStack;

public class RotProperty extends TypeIdentifier
{
    public String key;
    public int days = ConfigHandler.DAYS_NO_ROT;

    public RotProperty()
    {
    	super();
    }
    
    public RotProperty(String _key, String _rotID, int _days)
    {
    	super(_rotID);
    	key = _key;
        days = _days;
    }

    public long getRotTime()
    {
       return getRotTimeRaw();
       
       /*
       long rotTime = getRotTimeRaw();
       
       // we add x% to sortof make up for the x% chunking of date in RotInfo.checkInitialized for stacking
       long add = (rotTime * ConfigContainer.rotting.chunkingPercentage) / 100;
       long modRotTime = rotTime + add;
       
       return modRotTime;
       */
    }
    
    public long getRotTimeRaw()
    {
        if (ConfigContainer.zdebugging.debug)
        {
            double ticksPerDay = ConfigContainer.zdebugging.rotMultiplier * ConfigHandler.TICKS_PER_DAY;
            return days * (long) ticksPerDay;
        }

        return days * ConfigHandler.TICKS_PER_DAY;
    }

    public boolean doesRot()
    {
        return (days > ConfigHandler.DAYS_NO_ROT);
    }
    
    protected ItemStack forceRot(ItemStack stack)
    {
    	int count = (stack != null) ? stack.getCount() : 1;
        return create(count);
    }
 }
