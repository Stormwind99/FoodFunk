package com.wumple.foodfunk.capabilities.preserving;

import net.minecraft.tileentity.TileEntity;

interface IPreserving
{
	long getLastCheckTime();
	
	void setLastCheckTime(long time);
	
	void setOwner(TileEntity ownerIn);
}
