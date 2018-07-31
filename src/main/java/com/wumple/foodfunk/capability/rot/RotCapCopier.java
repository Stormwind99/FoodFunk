package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.rottables.RotTickingTileEntity;
import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.eventtimed.IEventTimedThingCap;
import com.wumple.util.tileentity.placeholder.CapCopier;

import net.minecraft.tileentity.TileEntity;

public interface RotCapCopier extends CapCopier<IEventTimedThingCap<IThing,RotInfo>>
{
    default TileEntity getNewTE()
    {
        return new RotTickingTileEntity();
    }   
}
