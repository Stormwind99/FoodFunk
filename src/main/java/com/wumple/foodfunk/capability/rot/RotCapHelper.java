package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import choonster.capability.CapabilityUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class RotCapHelper
{
    /**
     * Get the {@link IRot} from the specified {@link ItemStack}'s capabilities, if any.
     *
     * @param itemStack
     *            The ItemStack
     * @return The IRot, or null if there isn't one
     */
    @Nullable
    public static IRot getRot(@Nullable ItemStack itemStack)
    {
        return CapabilityUtils.getCapability(itemStack, Rot.CAPABILITY, Rot.DEFAULT_FACING);
    }

    /**
     * Create a provider for the default {@link IRot} instance.
     *
     * @return The provider
     */
    public static ICapabilityProvider createProvider(ItemStack stack)
    {
        // return new SimpleCapabilityProvider<>(Rot.CAPABILITY,
        // Rot.DEFAULT_FACING);

        return new RotProvider(Rot.CAPABILITY, Rot.DEFAULT_FACING, stack);
    }

    /**
     * Create a provider for the specified {@link IRot} instance.
     *
     * @param Rot
     *            The IRot
     * @return The provider
     */
    public static ICapabilityProvider createProvider(IRot capability, ItemStack stack)
    {
        // return new SimpleCapabilityProvider<>(Rot.CAPABILITY,
        // Rot.DEFAULT_FACING, capability);

        return new RotProvider(Rot.CAPABILITY, Rot.DEFAULT_FACING, capability, stack);
    }
}
