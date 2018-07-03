package com.wumple.foodfunk.capability.preserving;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capability.preserving.IPreserving;
import com.wumple.foodfunk.capability.preserving.PreservingProvider;

import choonster.capability.CapabilityUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class PreservingHelper
{
    /**
     * Get the {@link IPreserving} from the specified {@link TileEntity}'s capabilities, if any.
     *
     * @param it
     *            The TileEntity
     * @return The IPreserving, or null if there isn't one
     */
    @Nullable
    public static IPreserving getPreserving(@Nullable TileEntity it)
    {
        return CapabilityUtils.getCapability(it, PreservingProvider.CAPABILITY, PreservingProvider.DEFAULT_FACING);
    }

    /**
     * Create a provider for the default {@link IPreserving} instance.
     *
     * @return The provider
     */
    public static ICapabilityProvider createProvider(TileEntity owner)
    {
        // return new SimpleCapabilityProvider<>(RotProvider.CAPABILITY,
        // RotProvider.DEFAULT_FACING);

        return new PreservingProvider(PreservingProvider.CAPABILITY, PreservingProvider.DEFAULT_FACING, owner);
    }

    /**
     * Create a provider for the specified {@link IPreserving} instance.
     *
     * @param capability
     *            The IPreserving
     * @return The provider
     */
    public static ICapabilityProvider createProvider(IPreserving capability, TileEntity owner)
    {
        // return new SimpleCapabilityProvider<>(PreservingProvider.CAPABILITY,
        // PreservingProvider.DEFAULT_FACING, capability);

        return new PreservingProvider(PreservingProvider.CAPABILITY, PreservingProvider.DEFAULT_FACING, capability,
                owner);
    }
}
