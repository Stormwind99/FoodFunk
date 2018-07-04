package com.wumple.misc;

import com.wumple.foodfunk.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
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
 * Only usable on client.  Server will have empty info since it can't track multiple players.
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
    
    public static TileEntity getUsedContainer(Entity player)
    {
        if (lastUsedBy == player)
        {
            // MAYBE if (lastUsedContainer == player.openContainer)
            return lastUsedTileEntity;
        }
        
        return null;
    }
    
    public static TileEntity getUsedContainer(Entity player, ItemStack stack)
    {
        TileEntity tileentity = getUsedContainer(player);
        
        // Wish we could check that tileentity contained stack and slot held stack
        
        GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
        if (guiscreen instanceof GuiChest)
        {
            GuiChest guichest = (GuiChest)guiscreen;
            Slot slot = (guichest != null) ? guichest.getSlotUnderMouse() : null;
            if ((slot != null) && !(slot.inventory instanceof InventoryPlayer))
            {
                return tileentity;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return tileentity;
        }
    }
}
