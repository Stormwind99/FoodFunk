package com.wumple.foodfunk.chest.freezer;

import com.wumple.util.xchest2.XChestItemStackTileEntityRenderer;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class FreezerItemStackTileEntityRenderer extends XChestItemStackTileEntityRenderer<FreezerBlock>
{
	protected static final FreezerTileEntity TE = new FreezerTileEntity();

	protected TileEntity getTileEntity() { return TE; }
	protected boolean shouldRender(Block block) 
	{ 
		return (block instanceof FreezerBlock);
	}
}