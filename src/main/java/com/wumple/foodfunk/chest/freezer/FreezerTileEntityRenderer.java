package com.wumple.foodfunk.chest.freezer;

import com.wumple.foodfunk.ModObjectHolder;
import com.wumple.foodfunk.Reference;
import com.wumple.util.xchest2.XChestTileEntityRenderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;

public class FreezerTileEntityRenderer extends XChestTileEntityRenderer<FreezerTileEntity>
{
	private static ResourceLocation TEXTURE_NORMAL = new ResourceLocation(Reference.MOD_ID, "textures/entity/freezer.png");
	
	@Override
	protected ResourceLocation getTexture()
	{
	  return TEXTURE_NORMAL;
	}
	
	@Override
	protected Block getBlock()
	{
		return ModObjectHolder.FreezerBlock;
	}
}
