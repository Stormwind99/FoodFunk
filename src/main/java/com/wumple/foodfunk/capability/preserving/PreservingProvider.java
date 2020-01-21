package com.wumple.foodfunk.capability.preserving;

import javax.annotation.Nullable;

import com.wumple.util.capability.thing.ThingCapProvider;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

// TODO filter out if item doesn't preserve anymore?

public class PreservingProvider extends ThingCapProvider<IPreserving.IPreservingOwner, IPreserving>
{
    public PreservingProvider(Capability<IPreserving> capability, @Nullable Direction facing, IPreserving.IPreservingOwner ownerIn)
    {
        super(capability, facing, (capability != null) ? capability.getDefaultInstance() : null, ownerIn);
    }

    public PreservingProvider(Capability<IPreserving> capability, @Nullable Direction facing, IPreserving instance,
    		IPreserving.IPreservingOwner ownerIn)
    {
        super(capability, facing, instance, ownerIn);
    }
    
    public static PreservingProvider createProvider(IPreserving.IPreservingOwner ownerIn)
    {
        return new PreservingProvider(Preserving.CAPABILITY, Preserving.DEFAULT_FACING, ownerIn);
    }
}