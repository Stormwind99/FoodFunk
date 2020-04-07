package com.wumple.foodfunk.chest.icebox;

import com.wumple.foodfunk.ModObjectHolder;
import com.wumple.foodfunk.Reference;
import com.wumple.util.xchest.XChestTileEntityRenderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;

public class IceboxTileEntityRenderer extends XChestTileEntityRenderer<IceboxTileEntity>
{
	private static ResourceLocation TEXTURE_NORMAL = new ResourceLocation(Reference.MOD_ID, "textures/entity/icebox.png");
	
	@Override
	protected ResourceLocation getTexture()
	{
		return TEXTURE_NORMAL;
	}
	
	@Override
	protected Block getBlock()
	{
		return ModObjectHolder.IceboxBlock;
	}
	
	@Override
	protected ChestType getChestType(BlockState blockstate)
	{
		return ChestType.SINGLE;
	}
}
