package com.wumple.util.adapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class EntityThing implements IThing
{
	public Entity owner = null;
	
	public EntityThing(Entity ownerIn)
	{
		owner = ownerIn;
	}
	
	public World getWorld()
	{
		return owner.getEntityWorld();
	}
	
	public boolean isInvalid()
	{
		return owner.isDead;
	}
	
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
    	return owner.hasCapability(capability, facing);
    }
    
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
    	return owner.getCapability(capability, facing);
    }
    
    public void markDirty()
    {
    }
    
    public void invalidate()
    {
    	owner = null;
    }
    
    public boolean sameAs(IThing entity)
    {
    	if (entity instanceof EntityThing)
    	{
    		return owner == ((EntityThing)entity).owner;
    	}
    	return false;
    }
}