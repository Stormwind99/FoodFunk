package com.wumple.foodfunk.capability.preserving;

import javax.annotation.Nullable;

import com.wumple.util.capability.SimpleCapabilityProvider;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

// TODO filter out if item doesn't preserve anymore?

public class PreservingProvider extends SimpleCapabilityProvider<IPreserving>
{
    IPreserving.IPreservingOwner owner = null;

    public PreservingProvider(Capability<IPreserving> capability, @Nullable EnumFacing facing, IPreserving.IPreservingOwner ownerIn)
    {
        super(capability, facing, (capability != null) ? capability.getDefaultInstance() : null);
        owner = ownerIn;
    }

    public PreservingProvider(Capability<IPreserving> capability, @Nullable EnumFacing facing, IPreserving instance,
    		IPreserving.IPreservingOwner ownerIn)
    {
        super(capability, facing, instance);
        owner = ownerIn;
    }

    public final IPreserving getInstance()
    {
        IPreserving cap = super.getInstance();
        cap.setOwner(owner);
        return cap;
    }
}