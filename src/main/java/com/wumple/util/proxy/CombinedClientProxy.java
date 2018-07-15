package com.wumple.util.proxy;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * 
 * @author Choonster
 *
 */
public class CombinedClientProxy implements ISidedProxy
{

    private final Minecraft MINECRAFT = Minecraft.getMinecraft();

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
    }

    @Override
    public void doClientRightClick()
    {
        // Press the Use Item keybinding
        KeyBinding.onTick(MINECRAFT.gameSettings.keyBindUseItem.getKeyCode());
    }

    @Nullable
    @Override
    public EntityPlayer getClientPlayer()
    {
        return MINECRAFT.player;
    }

    @Nullable
    @Override
    public World getClientWorld()
    {
        return MINECRAFT.world;
    }

    @Override
    public IThreadListener getThreadListener(final MessageContext context)
    {
        if (context.side.isClient())
        {
            return MINECRAFT;
        }
        else
        {
            return context.getServerHandler().player.getServer();
        }
    }

    @Override
    public EntityPlayer getPlayer(final MessageContext context)
    {
        if (context.side.isClient())
        {
            return MINECRAFT.player;
        }
        else
        {
            return context.getServerHandler().player;
        }
    }
}
