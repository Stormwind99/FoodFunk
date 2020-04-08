package com.wumple.foodfunk.chest.larder;

import com.wumple.foodfunk.ModObjectHolder;
import com.wumple.util.xchest2.XChestTileEntity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LarderTileEntity extends XChestTileEntity
{
	public LarderTileEntity()
	{
		super(ModObjectHolder.LarderBlock_Tile);
	}
	
	@Override
	protected ITextComponent getDefaultName()
	{
		return new TranslationTextComponent("container.foodfunk.larder");
	}
}
