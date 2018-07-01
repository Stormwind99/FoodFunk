package com.wumple.foodfunk.capabilities.rot;

import com.wumple.foodfunk.RotHandler;

import choonster.capability.CapabilityContainerListenerManager;
import choonster.capability.foodfunk.ContainerListenerRot;
import choonster.capability.foodfunk.RotInfo;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Rot implements IRot
{	
    public static long lastWorldTimestamp = 0;
    
	public static void register()
	{
		CapabilityManager.INSTANCE.register(IRot.class, new RotStorage(), () -> new Rot() );
		
		CapabilityContainerListenerManager.registerListenerFactory(ContainerListenerRot::new);
	}
	
	protected RotInfo info = new RotInfo();
	ItemStack owner = null;
	
	public Rot()
	{
		
	}
	
	public Rot(Rot other)
	{
		info = other.info;
	}

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
	
	public void setOwner(ItemStack ownerIn)
	{
		if (ownerIn != owner)
		{
			owner = ownerIn;
			RotHandler.setDefaults(owner, this);
			info.date = lastWorldTimestamp;
		}
	}
	
	public ItemStack getOwner()
	{
	    return owner;
	}
}
