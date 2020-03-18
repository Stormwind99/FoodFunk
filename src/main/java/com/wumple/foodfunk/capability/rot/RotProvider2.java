package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class RotProvider2 implements ICapabilitySerializable<INBT>
{
	protected IThing owner = null;
	
    IRot capInstance;
    LazyOptional<IRot> cap_provider;
    
    @CapabilityInject(IRot.class)
    public static final Capability<IRot> CAPABILITY = null;
    
    public RotProvider2(Capability<IRot> capability, @Nullable Direction facing, IThing ownerIn)
    {
    	owner = ownerIn;
    	capInstance = new Rot();
    	//capInstance.checkInit(owner);
    	capInstance.setOwner(owner);
    	cap_provider = LazyOptional.of(() -> capInstance);
    }

    public RotProvider2(Capability<IRot> capability, @Nullable Direction facing, IRot instance, IThing ownerIn)
    {
    	owner = ownerIn;
    	capInstance = instance;
    	capInstance.checkInit(owner);
    	//capInstance.setOwner(owner);
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
  
    @Override
    public INBT serializeNBT()
    {
        return CAPABILITY.writeNBT(capInstance, Rot.DEFAULT_FACING);
    }

    @Override
    public void deserializeNBT(INBT nbt)
    {
    	CAPABILITY.readNBT(capInstance, Rot.DEFAULT_FACING, nbt);
    }
}
