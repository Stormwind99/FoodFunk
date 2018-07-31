package com.wumple.foodfunk.rottables;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.util.adapter.TUtil;
import com.wumple.util.tileentity.placeholder.TickingTileEntityPlaceholder;

import net.minecraft.world.World;

public class RotTickingTileEntity extends TickingTileEntityPlaceholder
{
    public void doIt(World world)
    {
        IRot cap = IRot.getRot(this);
        if (cap != null)
        {
            cap.evaluate(world, TUtil.to(this));
        }
    }
}
