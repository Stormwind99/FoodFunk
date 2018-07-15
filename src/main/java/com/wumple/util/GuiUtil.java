package com.wumple.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiUtil
{
    /*
     * Is the gui slot under the mouse holding the given itemstack?
     */
    @SideOnly(Side.CLIENT)
    public static boolean isOpenContainerSlotUnderMouse(ItemStack stack)
    {
        GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
        if (guiscreen instanceof GuiContainer)
        {
            GuiContainer guichest = (GuiContainer)guiscreen;
            Slot slot = (guichest != null) ? guichest.getSlotUnderMouse() : null;
            // does the slot hold the hinted stack?
            // and is the slot not a player inventory slot (if inventory not open)?
            // if inventory open, then:
            //    guiscreen.inventorySlots instanceof ContainerPlayer
        	//    slot.inventory instanceof InventoryPlayer
            if ((slot != null) && (slot.getStack() == stack) && 
            		(
            				!(slot.inventory instanceof InventoryPlayer) // ||
            				// ((guichest.inventorySlots instanceof ContainerPlayer) && (slot.inventory instanceof InventoryPlayer))
            		) )
            {
                return true;
            }
        }
        
        return false;        
    }
}
