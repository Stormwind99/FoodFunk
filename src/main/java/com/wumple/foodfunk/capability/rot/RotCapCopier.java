package com.wumple.foodfunk.capability.rot;

import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.copier.CapCopier;
import com.wumple.util.capability.eventtimed.IEventTimedThingCap;

public interface RotCapCopier extends CapCopier<IEventTimedThingCap<IThing, RotInfo>>
{
	/*
	// PORT
    default TileEntity getNewTE(World world)
    {
        return new RotTickingTileEntity(world);
    }
    */
}
