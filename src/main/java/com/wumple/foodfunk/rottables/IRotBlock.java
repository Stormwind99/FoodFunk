package com.wumple.foodfunk.rottables;

import com.wumple.util.placeholder.IBlockPlaceholder;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IRotBlock extends IBlockPlaceholder
{
    default public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new RotTickingTileEntity();
    }
}
