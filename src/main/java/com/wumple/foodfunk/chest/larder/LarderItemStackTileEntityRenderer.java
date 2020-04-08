package com.wumple.foodfunk.chest.larder;

import com.wumple.util.xchest2.XChestItemStackTileEntityRenderer;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class LarderItemStackTileEntityRenderer extends XChestItemStackTileEntityRenderer<LarderBlock>
{
	protected static final LarderTileEntity TE = new LarderTileEntity();

	protected TileEntity getTileEntity() { return TE; }
	protected boolean shouldRender(Block block) 
	{ 
		return (block instanceof LarderBlock);
	}
}