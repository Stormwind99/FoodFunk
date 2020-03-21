package com.wumple.foodfunk;

import com.wumple.foodfunk.chest.icebox.IceboxTileEntity;
import com.wumple.foodfunk.chest.icebox.IceboxTileEntityRenderer;
import com.wumple.foodfunk.chest.larder.LarderTileEntity;
import com.wumple.foodfunk.chest.larder.LarderTileEntityRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy implements IProxy
{
	@Override
	public void init()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(LarderTileEntity.class, new LarderTileEntityRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(IceboxTileEntity.class, new IceboxTileEntityRenderer());
		//ScreenManager.registerFactory(ModObjectHolder.LarderBlock_Container, LarderScreen::new);
		//ScreenManager.registerFactory(ModObjectHolder.IceboxBlock_Container, IceboxScreen::new);
	}

	@Override
	public World getClientWorld()
	{
		return Minecraft.getInstance().world;
	}

	@Override
	public PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}
}