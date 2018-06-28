package com.wumple.foodfunk.capabilities.rot;

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
}