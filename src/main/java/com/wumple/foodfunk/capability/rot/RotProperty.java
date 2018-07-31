package com.wumple.foodfunk.capability.rot;


import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.EntityThing;
import com.wumple.util.adapter.IThing;
import com.wumple.util.misc.TypeIdentifier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
       if (ConfigContainer.rotting.chunkingPercentage != 0)
       {
           long rotTime = getRotTimeRaw();
           
           // we add x% to sortof make up for the x% chunking of date in RotInfo.checkInitialized for stacking
           long add = (rotTime * ConfigContainer.rotting.chunkingPercentage) / 100;
           long modRotTime = rotTime + add;
           
           return modRotTime;
       }
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

    protected IThing transform(IThing thing, IThing newthing)
    {
        // if source was tileentity or entity, then result should be ItemEntity
        if (thing.is(Entity.class) || thing.is(TileEntity.class))
        {
            BlockPos pos = thing.getPos();
            World world = thing.getWorld();
            
            // if source was tileentity, then set the tile to air since it rotted.  Entities just die.
            thing.invalidate();
           
            // if an itemstack resulted from rotting, put it into an EntityItem at source's location (now empty)
            ItemStack newstack = (newthing != null) ? newthing.as(ItemStack.class) : null;
            if (newstack != null)
            {
                EntityItem entity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), newstack);
                world.spawnEntity(entity);
                return new EntityThing(entity);
            }
            // else just return the new ItemStackThing, which is likely null
            else
            {
                return newthing;
            }
        }
        // else just return the new ItemStackThing
        else
        {
            return newthing;
        }

    }
    
    protected IThing forceRot(IThing thing)
    {
        // create the rot result 
    	int count = (thing != null) ? thing.getCount() : 1;
    	IThing newthing = createThing(count);
    	return transform(thing, newthing);
    }
 }
