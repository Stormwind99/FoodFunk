package com.wumple.foodfunk.rottables;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPumpkinRottable extends BlockPumpkin implements IRotBlock
{
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityPumpkinRottable();
    }
}
