package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.capability.eventtimed.Expiration;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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

    public RotInfo(NBTTagCompound tags)
    {
        this.readFromNBT(tags);
    }

    public int getChunkingPercent()
    {
        return ConfigContainer.rotting.chunkingPercentage;
    }

    protected long getTimerLength(ItemStack owner)
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
        return ConfigContainer.modifiers.dimensionRatios.getOrDefault(dimensionKey, ConfigHandler.DIMENSIONRATIO_DEFAULT);
    }
    
    public static int getADimensionRatio(int dimensionId)
    {
        String dimensionKey = Integer.toString(dimensionId);
        return getADimensionRatio(dimensionKey);
    }
}