package com.wumple.foodfunk.capability.rot;


import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.IThing;
import com.wumple.util.misc.TimeUtil;
import com.wumple.util.misc.TypeIdentifier;

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
        if (ConfigHandler.isDebugging())
        {
            double ticksPerDay = ConfigHandler.getRotMultiplier() * TimeUtil.TICKS_PER_DAY;
            return days * (long) ticksPerDay;
        }

        return days * TimeUtil.TICKS_PER_DAY;
    }

    public boolean doesRot()
    {
        return (days > ConfigHandler.DAYS_NO_ROT);
    }
    
    public IThing forceRot(IThing thing)
    {
        return createAndTransform(thing);
    }
 }
