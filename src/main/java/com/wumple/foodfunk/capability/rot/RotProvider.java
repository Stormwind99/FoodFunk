package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.capability.itemstack.ItemStackCapProvider;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

// TODO filter out IRot if item doesn't rot anymore?

public class RotProvider extends ItemStackCapProvider<IRot>
{
    public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, ItemStack stack)
    {
        super(capability, facing, stack);
    }

    public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, IRot instance, ItemStack stack)
    {
        super(capability, facing, instance, stack);
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
}
