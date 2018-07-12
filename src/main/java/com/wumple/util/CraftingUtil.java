package com.wumple.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class CraftingUtil
{
    public static boolean isItemBeingCraftedBy(ItemStack stack, Entity entity)
    {
        boolean beingCrafted = false;

        EntityPlayer player = (EntityPlayer) (entity);
        if (player != null)
        {
            if (player.openContainer != null)
            {
                Slot slot = player.openContainer.getSlot(0);
                if ((slot != null) && (slot instanceof SlotCrafting) && (slot.getStack() == stack))
                {
                    beingCrafted = true;
                }
            }
        }

        return beingCrafted;
    }
}
