package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.configuration.ConfigHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

/**
 * Wrapper class used to encapsulate information about an IRot.
 */
public final class RotInfo
{
    /*
     * The timestamp at which the stack is considered at 0% rot - creation time at first, but advanced by preserving containers
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
        this.date = date;
        this.time = time;
    }

    public RotInfo(IRot tank)
    {
        this.date = tank.getDate();
        this.time = tank.getTime();
    }

    public RotInfo(RotInfo other)
    {
        this.date = other.date;
        this.time = other.time;
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
            this.date = tags.getLong("rotStartTimestamp");
            this.time = tags.getLong("rotLengthTime");
        }

        return tags;
    }

    // from old RotTime

    public long getDate()
    {
        return date;
    }

    public long getTime()
    {
        return time;
    }

    public long getCurTime()
    {
        return Rot.getLastWorldTimestamp();
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

}