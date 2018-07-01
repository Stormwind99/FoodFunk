package com.wumple.foodfunk.capabilities.rot;

import net.minecraft.item.ItemStack;

/*
 * Rot capability 
 */
public interface IRot
{
	public long getDate();
	public long getTime();
	
	public void setDate(long dateIn);
	public void setTime(long timeIn);
	public void setRot(long dateIn, long timeIn);
	public void reschedule(long timeIn);
	
	public void setOwner(ItemStack ownerIn);
}