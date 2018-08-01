package com.wumple.foodfunk.rottables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.SoundType;

public class BlockStemCustom extends BlockStem
{
    public BlockStemCustom(Block crop)
    {
        super(crop);
    }
    
    /**
     * Sets the footstep sound for the block. Returns the object for convenience in constructing.
     */
    public BlockStemCustom setSoundType(SoundType sound)
    {
        super.setSoundType(sound);
        return this;
    }
}
