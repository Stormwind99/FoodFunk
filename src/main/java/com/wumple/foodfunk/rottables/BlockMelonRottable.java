package com.wumple.foodfunk.rottables;

import net.minecraft.block.BlockMelon;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMelonRottable extends BlockMelon implements IRotBlock
{
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityMelonRottable();
    }
}
