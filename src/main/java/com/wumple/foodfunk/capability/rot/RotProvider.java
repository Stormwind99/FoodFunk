package com.wumple.foodfunk.capability.rot;

import javax.annotation.Nullable;

import com.wumple.util.capability.SimpleCapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

// TODO filter out IRot if item doesn't rot anymore?

public class RotProvider extends SimpleCapabilityProvider<IRot>
{
    ItemStack owner = null;

    public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, ItemStack stack)
    {
        super(capability, facing, (capability != null) ? capability.getDefaultInstance() : null);
        owner = stack;
    }

    public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, IRot instance, ItemStack stack)
    {
        super(capability, facing, instance);
        owner = stack;
    }

    @Override
    public IRot getInstance()
    {
        IRot cap = super.getInstance();
        cap.setOwner(owner);
        return cap;
    }
}
