package com.wumple.foodfunk.capabilities.rot;

import choonster.capability.CapabilityContainerListenerManager;
import choonster.capability.foodfunk.ContainerListenerRot;
import choonster.capability.foodfunk.RotInfo;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Rot implements IRot
{	
	public static void register()
	{
		CapabilityManager.INSTANCE.register(IRot.class, new RotStorage(), () -> new Rot() );
		
		CapabilityContainerListenerManager.registerListenerFactory(ContainerListenerRot::new);
	}
	
	protected RotInfo info = new RotInfo();

	@Override
	public long getDate()
	{
		return info.date;
	}

	@Override
	public long getTime()
	{
		return info.time;
	}

	@Override
	public void setDate(long dateIn)
	{
		info.date = dateIn;
	}

	@Override
	public void setTime(long timeIn)
	{
		info.time = timeIn;
	}

	@Override
	public void setRot(long dateIn, long timeIn)
	{
		info.date = dateIn;
		info.time = timeIn;
	}
	
	@Override
	public void reschedule(long timeIn)
	{
		info.date += timeIn;
		info.time += timeIn;
	}
	
	public RotInfo setInfo(RotInfo infoIn)
	{
		info = infoIn;
		return info;
	}
	
	public RotInfo getInfo()
	{
		return info;
	}
}
