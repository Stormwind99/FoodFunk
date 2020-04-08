package com.wumple.foodfunk.chest.esky;

import com.wumple.foodfunk.ModObjectHolder;
import com.wumple.util.xchest2.XChestTileEntity;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EskyTileEntity extends XChestTileEntity
{
	public EskyTileEntity()
	{
		super(ModObjectHolder.EskyBlock_Tile);
	}
	
	@Override
	protected ITextComponent getDefaultName()
	{
		return new TranslationTextComponent("container.foodfunk.esky");
	}
	
	@Override
    protected SoundEvent getOpenSoundEvent()
    {
        return ModObjectHolder.esky_open;
    }

	@Override
    protected SoundEvent getCloseSoundEvent()
    {
        return ModObjectHolder.esky_close;
    }
}
