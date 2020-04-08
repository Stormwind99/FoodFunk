package com.wumple.foodfunk.chest.esky;

import com.wumple.util.xchest2.XChestItemStackTileEntityRenderer;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class EskyItemStackTileEntityRenderer extends XChestItemStackTileEntityRenderer<EskyBlock>
{
	protected static final EskyTileEntity TE = new EskyTileEntity();

	protected TileEntity getTileEntity() { return TE; }
	protected boolean shouldRender(Block block) 
	{ 
		return (block instanceof EskyBlock);
	}
}