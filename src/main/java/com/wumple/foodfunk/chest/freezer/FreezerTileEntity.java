package com.wumple.foodfunk.chest.freezer;

import com.wumple.foodfunk.ModObjectHolder;
import com.wumple.util.xchest2.XChestTileEntity;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FreezerTileEntity extends XChestTileEntity
{
	public FreezerTileEntity()
	{
		super(ModObjectHolder.FreezerBlock_Tile);
	}
	
	@Override
	protected ITextComponent getDefaultName()
	{
		return new TranslationTextComponent("container.foodfunk.freezer");
	}
	
	@Override
    protected SoundEvent getOpenSoundEvent()
    {
        return ModObjectHolder.freezer_open;
    }

	@Override
    protected SoundEvent getCloseSoundEvent()
    {
        return ModObjectHolder.freezer_close;
    }
}
