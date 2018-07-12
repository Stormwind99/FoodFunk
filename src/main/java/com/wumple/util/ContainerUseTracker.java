package com.wumple.util;

import com.wumple.foodfunk.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ContainerUseTracker
{
    public static TileEntity lastUsedTileEntity = null;
    public static Entity lastUsedBy = null;
    public static Container lastUsedContainer = null;
    
    public static void forget()
    {
        lastUsedTileEntity = null;
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
        if ((event.getFace() != null) && (event.getSide() == Side.CLIENT))
        {
            BlockPos pos = event.getPos();
            TileEntity entity = event.getWorld().getTileEntity(pos);
            lastUsedTileEntity = entity;
            if (entity != null)
            {
                lastUsedBy = event.getEntity();
            }
        }        
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onOpen(PlayerContainerEvent.Open event)
    {
        if ((lastUsedBy == event.getEntity()))
        {
            lastUsedContainer = event.getContainer();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onClose(PlayerContainerEvent.Close event)
    { 
        if ((lastUsedBy == event.getEntity()) && (lastUsedContainer == event.getContainer()))
        {
            forget();
        }
        // TODO else probably a bug       
    }
    
    @SideOnly(Side.CLIENT)
    public static TileEntity getUsedContainer(Entity player)
    {
        if (lastUsedBy == player)
        {
            // MAYBE if (lastUsedContainer == player.openContainer)
            return lastUsedTileEntity;
        }
        
        return null;
    }
    
    /*
     * Get the currently open container just used by the player, with stack as a hint to contents
     */
    @SideOnly(Side.CLIENT)
    public static TileEntity getUsedOpenContainer(Entity player, ItemStack stack)
    {
        return GuiUtil.isSlotUnderMouse(stack) ? getUsedContainer(player) : null;
    }
}
