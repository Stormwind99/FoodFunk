package com.wumple.foodfunk.capabilities.preserving;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PreservingStorage implements IStorage<IPreserving>
{
	@Override 
	public NBTBase writeNBT(Capability<IPreserving> capability, IPreserving instance, EnumFacing side) 
	{ 
		NBTTagCompound tags = new NBTTagCompound();
		
		tags.setLong("rotLastCheckTime", instance.getLastCheckTime() );
		
		return tags; 
	} 

	@Override 
	public void readNBT(Capability<IPreserving> capability, IPreserving instance, EnumFacing side, NBTBase nbt) 
	{ 
		NBTTagCompound tags = (NBTTagCompound)nbt;
		
		instance.setLastCheckTime( tags.getLong("rotLastCheckTime") );
	} 
}
