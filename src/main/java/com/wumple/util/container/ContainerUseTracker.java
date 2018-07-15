package com.wumple.util.container;

import javax.annotation.Nullable;

import com.wumple.util.GuiUtil;
import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.CapabilityUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * Horrible hack to have access to TileEntity AND Container used by EntityPlayer at once
 * during Tooltips, etc.
 * 
 * Only usable on client.  Server will have empty info since we don't track multiple players.
 */

@Mod.EventBusSubscriber
public class ContainerUseTracker
{
    public static TileEntity lastUsedTileEntity = null;
    public static Entity lastUsedEntity = null;
    public static Entity lastUsedBy = null;
    public static Container lastUsedContainer = null;
    // MAYBE lastIItemHandler
    // MAYBE lastIInventory
    
    public static void forget()
    {
        lastUsedTileEntity = null;
        lastUsedEntity = null;
        lastUsedBy = null;
        lastUsedContainer = null;
    }
    
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        forget();
    }
    
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event)
    {
        forget();
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onInteract(PlayerInteractEvent event)
    {
        if (event.getSide() == Side.CLIENT)
        {
        	if (event.getFace() != null)
        	{
        		BlockPos pos = event.getPos();
        		TileEntity entity = event.getWorld().getTileEntity(pos);
        		lastUsedTileEntity = entity;
        		lastUsedEntity = null;
        		if (entity != null)
        		{
        			lastUsedBy = event.getEntity();
        		}
        	}
        }     
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onEntityInteract(EntityInteract event)
    {
        if (event.getSide() == Side.CLIENT)
        {
        	Entity target = event.getTarget();
        	if (target != null)
        	{
        		lastUsedEntity = target;
        		lastUsedTileEntity = null;
        		if (target != null)
        		{
        			lastUsedBy = event.getEntity();
        		}
        	}
        }     
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onOpen(PlayerContainerEvent.Open event)
    {
        if (lastUsedBy == event.getEntity())
        {
            lastUsedContainer = event.getContainer();
        }
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onGuiOpen(GuiOpenEvent event)
    {
    	GuiScreen screen = event.getGui();
    	if(screen instanceof GuiContainer)
    	{
    		GuiContainer gui = (GuiContainer)event.getGui();
            if (gui.inventorySlots instanceof ContainerPlayer)
            {
                lastUsedContainer = gui.inventorySlots;  
                lastUsedBy = Minecraft.getMinecraft().player; // gui.inventorySlots.player is private
            	lastUsedEntity = lastUsedBy;
            }
    	}
    	else if (screen == null)
    	{
    		forget();
    	}
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onClose(PlayerContainerEvent.Close event)
    { 
    	forget();
    }
    
    @SideOnly(Side.CLIENT)
    @Nullable
    public static <T> T getContainerCapability(EntityPlayer entity, ItemStack stack, Capability<T> capability, @Nullable EnumFacing facing)
    {
    	T cap = null;
    	
    	IThing thing = ContainerUtil.getContainedBy(stack, entity, null);
    	if (thing != null)
    	{
    		cap = thing.getCapability(capability, facing);
    	}
    	
        if (lastUsedBy == entity)
        {
        	if (GuiUtil.isOpenContainerSlotUnderMouse(stack))
        	{
        		// check Entities, such as for MinecartChest
        		if (cap == null)
        		{
        			// Client doesn't have container contents. so we don't check if 
        			//    ContainerUtil.doesContain(lastUsedEntity, stack)
        			cap = CapabilityUtils.getCapability(lastUsedEntity, capability, facing);
        		}
        		
        		// check TileEntities, such as for Chest
        		if (cap == null)
    			{
        			// Client doesn't have container contents. so wwe don't check if 
        			//    ContainerUtil.doesContain(lastUsedTileEntity, stack)
    				cap = CapabilityUtils.getCapability(lastUsedTileEntity, capability, facing);
    			}
        	}
        }
           
        return cap;
    }

}
