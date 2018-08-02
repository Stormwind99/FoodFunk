package com.wumple.foodfunk.rottables;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPumpkinRottable extends BlockPumpkin implements IRotBlock
{
    public BlockPumpkinRottable()
    {
        super();
        setHardness(1.0F);
        setSoundType(SoundType.WOOD);
        setTranslationKey("pumpkin");
    }
    
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityPumpkinRottable();
    }
}
