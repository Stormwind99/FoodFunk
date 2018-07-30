package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.adapter.IThing;
import com.wumple.util.adapter.ItemStackThing;
import com.wumple.util.capability.thing.ThingCapProvider;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

// TODO filter out IRot if item doesn't rot anymore?

public class RotProvider extends ThingCapProvider<IThing, IRot>
{
    public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, ItemStack stack)
    {
        super(capability, facing, new ItemStackThing(stack));
    }

    public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, IRot instance, ItemStack stack)
    {
        super(capability, facing, instance, new ItemStackThing(stack));
    }
    
    // TODO support IThing - Entity and TileEntity

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
