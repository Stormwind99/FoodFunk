package com.wumple.foodfunk.rottables;

import net.minecraft.block.BlockPotato;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPotatoRottable extends BlockPotato implements IRotBlock
{
    public BlockPotatoRottable()
    {
        super();
        setTranslationKey("potatoes");
    }
    
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityPotatoRottable(worldIn);
    }
}