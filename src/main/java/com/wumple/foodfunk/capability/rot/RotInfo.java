package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.eventtimed.Expiration;

import net.minecraft.nbt.CompoundNBT;

/**
 * Wrapper class used to encapsulate information about an IRot.
 */
public final class RotInfo extends Expiration
{
    public RotInfo()
    {
        super();
    }

    public RotInfo(long date, long time)
    {
        super(date, time);
    }

    public RotInfo(IRot tank)
    {
        super();
        set(tank.getDate(), tank.getTime());
    }

    public RotInfo(RotInfo other)
    {
        super(other);
    }

    public RotInfo(CompoundNBT tags)
    {
        this.readFromNBT(tags);
    }

    public int getChunkingPercent()
    {
        return ConfigHandler.getChunkingPercentage();
    }

    protected long getTimerLength(IThing owner)
    {
        long defaultTime = NO_EXPIRATION;
        RotProperty rotProps = ConfigHandler.rotting.getRotProperty(owner);

        if ((rotProps != null) && rotProps.doesRot())
        {
            defaultTime = rotProps.getRotTime();
        }
        return defaultTime;
    }

    @Override
    public int getDimensionRatio(String dimensionKey)
    {
        return getADimensionRatio(dimensionKey);
    }
    
    public static int getADimensionRatio(String dimensionKey)
    {
        return ConfigHandler.dimensions.getProperty(dimensionKey);
    }
    
    public static int getADimensionRatio(int dimensionId)
    {
        String dimensionKey = Integer.toString(dimensionId);
        return getADimensionRatio(dimensionKey);
    }
}