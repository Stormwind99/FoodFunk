package com.wumple.foodfunk.capabilities.rot;

import javax.annotation.Nullable;

import com.wumple.foodfunk.Reference;

import choonster.capability.SimpleCapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

// TODO filter out IRot if item doesn't rot anymore?

public class RotProvider extends SimpleCapabilityProvider<IRot> 
{ 
	// The {@link Capability} instance
	@CapabilityInject(IRot.class) 
	public static final Capability<IRot> CAPABILITY = null;
	public static final EnumFacing DEFAULT_FACING = null;
	
	// IDs of the capability
	public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "rot");
	
	ItemStack owner = null;
	
	public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, ItemStack stack) {
		super(capability, facing, (capability != null) ? capability.getDefaultInstance() : null);
		owner = stack;
	}

	public RotProvider(Capability<IRot> capability, @Nullable EnumFacing facing, IRot instance, ItemStack stack) {
		super(capability, facing, instance);
		owner = stack;
	}
	
	@Override
	public IRot getInstance() {
		IRot cap = super.getInstance();
		cap.setOwner(owner);
		return cap;
	}
}
