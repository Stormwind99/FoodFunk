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
            tags.setLong("rotStart", this.date);
            tags.setLong("rotTime", this.time);
        }

        return tags;
    }

    public NBTTagCompound readFromNBT(NBTTagCompound tags)
    {
        if (tags != null)
        {
            setDate(tags.getLong("rotStart"));
            setTime(tags.getLong("rotTime"));
        }

        return tags;
    }

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
        time = timeIn;
    }

    public void set(long dateIn, long timeIn)
    {
        date = dateIn;
        time = timeIn;
    }

    public void setDateSafe(long dateIn)
    {
        setDate(dateIn);
    }

    public void setTimeSafe(long timeIn)
    {
        assert (timeIn >= 0);
        setTime(timeIn);
    }

    public void setSafe(long dateIn, long timeIn)
    {
        setDateSafe(dateIn);
        setTimeSafe(timeIn);
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

    public void setRelative(int dimensionRatio, ItemStack owner)
    {
        ratioShiftInternal(ConfigHandler.DIMENSIONRATIO_DEFAULT, dimensionRatio, owner);
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

        ratioShiftInternal(fromRatio, toRatio, owner);
    }

    protected void ratioShiftInternal(int fromRatio, int toRatio, ItemStack owner)
    {
        if (toRatio == 0)
        {
            if ((fromRatio != 0) && (time != 0))
            {
                // this would loose any relative to fresh date rot info
                // but we encode any current non-zero rot time into fresh date before zeroing
                // that newdate+defaultTime = oldDate+oldTime
                long defaultTime = getDefaultTime(owner);
                long newDate = Math.max(1, date + time - defaultTime);

                // set without range checking
                set(newDate, ConfigHandler.DAYS_NO_ROT);
            }
            return;
        }

        if (isNoRot())
        {
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

    protected void ratioShiftBase(int dimensionRatioShift, ItemStack owner)
    {
        long localTime = alterTime(dimensionRatioShift, getCurTime(), date, time);

        // debug ratio was already applied to time in checkInitialized() at initialization

        // long clampedLocalTime = Longs.constrainToRange(localTime, 0, getDefaultTime(owner));
        long clampedLocalTime = (localTime < 0) ? 0 : localTime;

        setTimeSafe(clampedLocalTime);
    }
    
    public static long alterTime(int dimensionRatioShift, long now, long date, long time)
    {
        // let's alter time a bit for different dimensions
        long expirationTimeStamp = date + time;
        long left = expirationTimeStamp - now;
        long relativeLeft = shiftTime(dimensionRatioShift, left);
        long localTime = (now + relativeLeft) - date;

        return localTime;
    }
    
    public static long shiftTime(int dimensionRatioShift, long timeIn)
    {
        // skip if no change - better precision
        if (dimensionRatioShift == 0)
        {
            return timeIn;
        }

        double x = (double) dimensionRatioShift / ConfigHandler.DIMENSIONRATIO_DEFAULT;
        double y = (double) timeIn * Math.pow(2, x * -1);
        return (long) y;
    }
    
    protected void initTime(int dimensionRatio, ItemStack stack)
    {
        RotProperty rotProps = ConfigHandler.rotting.getRotProperty(stack);

        initTime(rotProps, dimensionRatio, stack);
    }
    
    protected void initTime(RotProperty rotProps, int dimensionRatio, ItemStack stack)
    {
        if ((rotProps != null) && rotProps.doesRot())
        {
            setTimeSafe(rotProps.getRotTime());
            setRelative(dimensionRatio, stack);
        }
    }

    public boolean checkInitialized(World world, ItemStack stack)
    {
        // if initialization not yet done (stack just created or was missed somehow), then do/fix it
        if (date == 0)
        {
            RotProperty rotProps = ConfigHandler.rotting.getRotProperty(stack);

            int ratio = getDimensionRatio(world);
            long curTime = getCurTime();
            long newTime = curTime;

            // chunk the start date of new items to increments of x% of local rot time
            // that way same items created close in time will usually stack because they have the same rot date and time
            if (rotProps != null)
            {
                long rotTime = rotProps.getRotTimeRaw();
                int ratioShift = ratio - ConfigHandler.DIMENSIONRATIO_DEFAULT;
                long shiftedRotTime = shiftTime(ratioShift, rotTime);
                long xPercentOfRotTime  = (shiftedRotTime * ConfigContainer.rotting.chunkingPercentage) / 100;
                long chunk = (curTime / xPercentOfRotTime) + 1;
                
                newTime = Math.max(1, chunk * xPercentOfRotTime);
            }
            
            setDateSafe(newTime);
           
            initTime(rotProps, ratio, stack);

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
        return ConfigContainer.modifiers.dimensionRatios.getOrDefault(dimensionKey, ConfigHandler.DIMENSIONRATIO_DEFAULT);
    }

    public static int getDimensionRatio(World world)
    {
        if (world == null)
        {
            return ConfigHandler.DIMENSIONRATIO_DEFAULT;
        }
            
        int dimensionId = world.provider.getDimension();
        return getDimensionRatio(dimensionId);
    }

    public boolean hasExpired()
    {
        if (isNoRot())
        {
            return false;
        }
        
        long worldTimeStamp = getCurTime();
        long relativeExpirationTimeStamp = getExpirationTimestamp();

        return (worldTimeStamp >= relativeExpirationTimeStamp);
    }
    
    public void reschedule(long timeIn)
    {
        // skip reschedule if in no rot mode - it would effectively double the amount of preservation when in a no-rot dimension
        if (!isNoRot())
        {
            long worldTimeStamp = getCurTime();
            long newDate = date + timeIn;
                                
            // don't allow items to go into negative rot aka super-fresh aka fresh date in future
            long maxDate = worldTimeStamp;
            /*
            // experiment - factor chunking into comparison since chunking could make newDate in future
            // so if date already in future (from chunking), allow it to continue into future
            long oldDiff = worldTimeStamp - date;
            if (oldDiff < 1)
            {
                // oldDiff will be negative, so this will increase maxDate
                maxDate -= oldDiff;
                maxDate += timeIn;
                // maxData probably just equals newDate now
            }
            */
            newDate = Math.min(newDate, maxDate);
            
            // don't go negative date (or even special value 0) when negative-preserving
            if (newDate < 1)
            {
                // experiment: try reducing time if date would be <= 0
                long newTime = newDate - 1 + time;
                if (newTime < 0) newTime = 0;
                time = newTime;
                newDate = 1;
            }
            
            date = newDate;
        }
    }
}