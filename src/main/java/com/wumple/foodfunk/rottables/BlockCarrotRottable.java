package com.wumple.foodfunk.rottables;

import net.minecraft.block.BlockCarrot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCarrotRottable extends BlockCarrot implements IRotBlock
{
    public BlockCarrotRottable()
    {
        super();
        setTranslationKey("carrots");
    }
    
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityCarrotRottable(worldIn);
    }
}
