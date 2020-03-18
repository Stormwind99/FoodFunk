package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.thing.ThingCapProvider;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

// TODO filter out IRot if item doesn't rot anymore?

public class RotProvider extends ThingCapProvider<IThing, IRot>
{
    public RotProvider(Capability<IRot> capability, @Nullable Direction facing, IThing thing)
    {
        super(capability, facing, thing);
    }

    public RotProvider(Capability<IRot> capability, @Nullable Direction facing, IRot instance, IThing thing)
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
        return new RotProvider(Rot.CAPABILITY, Rot.DEFAULT_FACING, thing);
    }
    
    @Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side)
	{
        if (capability == Rot.CAPABILITY)
            return lazyOptional.cast();
        return LazyOptional.empty();
	}
}
