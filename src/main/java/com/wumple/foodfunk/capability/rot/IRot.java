package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.eventtimed.IEventTimedThingCap;
import com.wumple.util.container.capabilitylistener.CapabilityUtils;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Rot capability 
 */
public interface IRot extends IEventTimedThingCap<IThing, RotInfo>
{
    /**
     * Get the {@link IRot} from the specified provider's capabilities, if any.
     *
     * @param provider
     * @return The IRot, or null if there isn't one
     */
    @Nullable
    static IRot getRot(@Nullable ICapabilityProvider provider)
    {
        return CapabilityUtils.fetchCapability(provider, Rot.CAPABILITY, Rot.DEFAULT_FACING);
    }
}