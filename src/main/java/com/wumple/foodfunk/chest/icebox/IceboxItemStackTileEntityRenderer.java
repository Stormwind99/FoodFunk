package com.wumple.foodfunk.chest.icebox;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import com.wumple.util.xchest2.XChestItemStackTileEntityRenderer;

public class IceboxItemStackTileEntityRenderer extends XChestItemStackTileEntityRenderer<IceboxBlock>
{
	protected static final IceboxTileEntity TE = new IceboxTileEntity();

	protected TileEntity getTileEntity() { return TE; }
	protected boolean shouldRender(Block block) 
	{ 
		return (block instanceof IceboxBlock);
	}
}