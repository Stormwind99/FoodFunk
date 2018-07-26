package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.capability.CapabilityUtils;
import com.wumple.util.capability.eventtimed.IEventTimedItemStackCap;

import net.minecraft.item.ItemStack;

/**
 * Rot capability 
 */
public interface IRot extends IEventTimedItemStackCap<RotInfo>
{
    /**
     * Get the {@link IRot} from the specified {@link ItemStack}'s capabilities, if any.
     *
     * @param itemStack
     *            The ItemStack
     * @return The IRot, or null if there isn't one
     */
    @Nullable
    static IRot getRot(@Nullable ItemStack itemStack)
    {
        return CapabilityUtils.fetchCapability(itemStack, Rot.CAPABILITY, Rot.DEFAULT_FACING);
    }
}