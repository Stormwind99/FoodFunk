package com.wumple.foodfunk;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup
{
	public ItemGroup itemGroup = new ItemGroup("foodfunk")
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ModObjectHolder.rotten_food);
		}
	};

	public void init()
	{
		//MinecraftForge.EVENT_BUS.register(new ForgeEventHandlers());
		//itemGroup.Networking.registerMessages();
	}
}