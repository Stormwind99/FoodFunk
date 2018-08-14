package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.rottables.RotTickingTileEntity;
import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.copier.CapCopier;
import com.wumple.util.capability.eventtimed.IEventTimedThingCap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface RotCapCopier extends CapCopier<IEventTimedThingCap<IThing, RotInfo>>
{
    default TileEntity getNewTE(World world)
    {
        return new RotTickingTileEntity(world);
    }
}
