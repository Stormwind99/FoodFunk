package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.misc.TimeUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Wrapper class used to encapsulate information about an IRot.
 */
public final class RotInfo
{
    /*
     * The timestamp at which the stack is considered at 0% rot - creation time at first, but advanced by preserving containers aka the fresh date
     */
    public long date;
    /*
     * The amount of time the item takes to rot. The time at which becomes rotten is date + time
     */
    public long time;

    public RotInfo()
    {
    }

    public RotInfo(long date, long time)
    {
        set(date, time);
    }

    public RotInfo(IRot tank)
    {
        set(tank.getDate(), tank.getTime());
    }

    public RotInfo(RotInfo other)
    {
        set(other.date, other.time);
    }

    public RotInfo(NBTTagCompound tags)
    {
        this.readFromNBT(tags);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tags)
    {
        if (tags != null)
        {
            tags.setLong("rotStartTimestamp", this.date);
            tags.setLong("rotLengthTime", this.time);
        }

        return tags;
    }

    public NBTTagCompound readFromNBT(NBTTagCompound tags)
    {
        if (tags != null)
        {
            setDate( tags.getLong("rotStartTimestamp") );
            setTime( tags.getLong("rotLengthTime") );
        }

        return tags;
    }

    // from old RotTime

    public long getDate()
    {
        return date;
    }

    public void setDate(long dateIn)
    {
        date = dateIn;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long timeIn)
    {
        assert(timeIn >= 0);
        time = timeIn;
    }

    public void set(long dateIn, long timeIn)
    {
        assert(timeIn >= 0);
        date = dateIn;
        time = timeIn;
    }

    public long getCurTime()
    {
        return TimeUtil.getLastWorldTimestamp();
    }

    public long getExpirationTimestamp()
    {
        return date + time;
    }

    public int getPercent()
    {
        // make sure percent >= 0
        return Math.max(0, MathHelper.floor((double) (getCurTime() - date) / time * 100D));
    }

    public int getDaysLeft()
    {
        return Math.max(0, MathHelper.floor((double) (getCurTime() - date) / ConfigHandler.TICKS_PER_DAY));
    }

    public int getDaysTotal()
    {
        return MathHelper.floor((double) time / ConfigHandler.TICKS_PER_DAY);
    }

    public int getUseBy()
    {
        return MathHelper.floor((double) (date + time) / ConfigHandler.TICKS_PER_DAY);
    }

    public boolean isSet()
    {
        return (date > 0);
    }

    public boolean isNoRot()
    {
        return (time == ConfigHandler.DAYS_NO_ROT);
    }

    public void setRelative(World worldIn, ItemStack owner)
    {
        // ratio is misnomer - actually is more like dimension numerator over 50
        int dimensionRatio = getDimensionRatio(worldIn);

        setRelative(dimensionRatio, owner);
    }
    
    public void setRelative(int dimensionRatio, ItemStack owner)
    {
        // ratio is misnomer - actually is more like dimension numerator over 50
        ratioShiftBase(dimensionRatio-ConfigHandler.DIMENSIONRATIO_DEFAULT, owner);
    }
    
    protected static long getDefaultTime(ItemStack owner)
    {
        long defaultTime = 0;
        RotProperty rotProps = ConfigHandler.rotting.getRotProperty(owner);

        if ((rotProps != null) && rotProps.doesRot())
        {
            defaultTime = rotProps.getRotTime();
        }
        return defaultTime;
    }
    
    public void ratioShift(int fromRatio, int toRatio, ItemStack owner)
    {
        // if fromRatio is 0 then time value info was lost, so restore from props and apply toRatio
        if ((fromRatio == 0) && (toRatio != 0))
        {
            initTime(toRatio, owner);
            return;
        }
               
        if (toRatio == 0)
        {
            if ((fromRatio != 0) && (time != 0))
            {
                // this will loose any relative to fresh date rot info
                // TODO: encode any current non-zero rot time into fresh date before zeroing
                // that newdate+defaultTime = oldDate+oldTime
                long defaultTime = getDefaultTime(owner);
                long newDate = date + time - defaultTime;
                set(newDate, 0);
            }
            return;
        }
        
        int dimensionRatioShift = toRatio - fromRatio;

        // skip if no change - better precision than integer math
        if (dimensionRatioShift == 0)
        {
            return;
        }
        
        ratioShiftBase(dimensionRatioShift, owner);
    }
    
    public static long getDimensionLocalTime(int dimensionRatio, long timeIn)
    {
        return shiftTime(dimensionRatio-ConfigHandler.DIMENSIONRATIO_DEFAULT, timeIn);
    }
    
    public static double log2(double d) {
        return Math.log(d)/Math.log(2.0);
     }
    
    public static long shiftTime(int dimensionRatioShift, long timeIn)
    {
        // skip if no change - better precision
        if (dimensionRatioShift == 0)
        {
            return timeIn;
        }
        
        double x = (double)dimensionRatioShift/ConfigHandler.DIMENSIONRATIO_DEFAULT;
        double y = (double)timeIn * Math.pow(2, x * -1);
        return (long)y;
    }
    
    public void ratioShiftBase(int dimensionRatioShift, ItemStack owner)
    {
        // let's alter time a bit for different dimensions
        long worldTimestamp = getCurTime();
        long expirationTimeStamp = getExpirationTimestamp();
        long left = expirationTimeStamp - worldTimestamp;
        long relativeLeft = shiftTime(dimensionRatioShift , left);
        long localTime = (worldTimestamp + relativeLeft) - date;

        // debug ratio was already applied to time in checkInitialized() at initialization
        
        setTime(localTime);
    }

    public void setRelative(World worldIn, ItemStack owner, long date, long time)
    {   
        set(date, time);
        setRelative(worldIn, owner);
    }
    
    protected void initTime(int dimensionRatio, ItemStack stack)
    {
        RotProperty rotProps = ConfigHandler.rotting.getRotProperty(stack);

        if ((rotProps != null) && rotProps.doesRot())
        {
            setTime(rotProps.getRotTime());
            setRelative(dimensionRatio, stack);
        }
    }  

    public boolean checkInitialized(World world, ItemStack stack)
    {
        // if initialization not yet done (stack just created or was missed somehow), then do/fix it
        if (date == 0)
        {
            setDate( getCurTime() );
            
            int ratio = getDimensionRatio(world);

            initTime(ratio, stack);
            
            return false;
        }
        
        return true;
    }
    
    public static int getDimensionRatio(int dimensionId)
    {
        String dimensionKey = Integer.toString(dimensionId);
        return getDimensionRatio(dimensionKey);
    }

    public static int getDimensionRatio(String dimensionKey)
    {
        return ConfigContainer.preserving.dimensionRatios.getOrDefault(dimensionKey, 0);
    }
    
    public static int getDimensionRatio(World world)
    {
        int dimensionId = world.provider.getDimension();
        return getDimensionRatio(dimensionId);
    }

    /*
     * Stretch time via dimensionRatio, no change if ratio == ConfigHandler.DIMENSIONRATIO_DEFAULT
     */
    public static long getDimensionLocalTime(World world, long timeIn)
    {
        // ratio is misnomer - actually is more like dimension numerator over ConfigHandler.DIMENSIONRATIO_DEFAULT
        int dimensionRatio = getDimensionRatio(world);

        return getDimensionLocalTime(dimensionRatio, timeIn);
    }

    public boolean hasExpired()
    {
        long worldTimeStamp = getCurTime();
        long relativeExpirationTimeStamp = getExpirationTimestamp();

        return (worldTimeStamp >= relativeExpirationTimeStamp);
    }
}