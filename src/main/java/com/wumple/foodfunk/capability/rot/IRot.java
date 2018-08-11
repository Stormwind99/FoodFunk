package com.wumple.foodfunk.capability.rot;

import java.util.List;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.eventtimed.IEventTimedThingCap;
import com.wumple.util.container.capabilitylistener.CapabilityUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * Rot capability 
 */
public interface IRot extends IEventTimedThingCap<IThing, RotInfo>
{
    default void doTooltipAddon(ItemStack stack, EntityPlayer entity, boolean advanced, List<String> tips) { }
        
    /**
     * Get the {@link IRot} from the specified provider's capabilities, if any.
     *
     * @param provider
     * @return The IRot, or null if there isn't one
     */
    @Nullable
    static IRot getMyCap(@Nullable ICapabilityProvider provider)
    {
        return CapabilityUtils.fetchCapability(provider, Rot.CAPABILITY, Rot.DEFAULT_FACING);
    }
}