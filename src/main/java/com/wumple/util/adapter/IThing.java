package com.wumple.util.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public interface IThing
{
	public World getWorld();
	public boolean isInvalid();
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing);
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing);
    public void markDirty();
    public void invalidate();        
    public boolean sameAs(IThing entity);
}