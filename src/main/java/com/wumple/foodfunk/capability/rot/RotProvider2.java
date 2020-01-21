package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class RotProvider2 implements ICapabilityProvider
{
	protected IThing owner = null;
	
    IRot capInstance;
    LazyOptional<IRot> cap_provider;

    public RotProvider2(Capability<IRot> capability, @Nullable Direction facing, IThing ownerIn)
    {
    	owner = ownerIn;
    	capInstance = new Rot();
    	capInstance.checkInit(owner);
    	cap_provider = LazyOptional.of(() -> capInstance);
    }

    public RotProvider2(Capability<IRot> capability, @Nullable Direction facing, IRot instance, IThing ownerIn)
    {
    	owner = ownerIn;
    	capInstance = instance;
    	capInstance.checkInit(owner);
    	cap_provider = LazyOptional.of(() -> capInstance);
    }

    @Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side)
	{
        if (capability == Rot.CAPABILITY)
            return cap_provider.cast();
        return LazyOptional.empty();
	}

    public static ICapabilityProvider createProvider(IThing thing)
    {
        return new RotProvider2(Rot.CAPABILITY, Rot.DEFAULT_FACING, thing);
    }
}
