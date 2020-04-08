package com.wumple.foodfunk.chest.icebox;

import com.wumple.foodfunk.ModObjectHolder;
import com.wumple.util.xchest2.XChestTileEntity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IceboxTileEntity extends XChestTileEntity
{
	public IceboxTileEntity()
	{
		super(ModObjectHolder.IceboxBlock_Tile);
	}

	@Override
	protected ITextComponent getDefaultName()
	{
		return new TranslationTextComponent("container.foodfunk.icebox");
	}
}
