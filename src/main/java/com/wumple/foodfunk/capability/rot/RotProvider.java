package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.thing.ThingCapProvider;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

// TODO filter out IRot if item doesn't rot anymore?

public class RotProvider extends ThingCapProvider<IThing, IRot>
{
    public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, IThing thing)
    {
        super(capability, facing, thing);
    }

    public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, IRot instance, IThing thing)
    {
        super(capability, facing, instance, thing);
    }
    
    /**
     * Create a provider for the default {@link IRot} instance.
     *
     * @return The provider
     */
    public static ICapabilityProvider createProvider(IThing thing)
    {
        // return new SimpleCapabilityProvider<>(Rot.CAPABILITY,
        // Rot.DEFAULT_FACING);
    
        return new RotProvider(Rot.CAPABILITY, Rot.DEFAULT_FACING, thing);
    }
}
