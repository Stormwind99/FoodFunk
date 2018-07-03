package com.wumple.foodfunk.capability.rot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RotHelper
{
    public static ItemStack evaluateRot(World world, ItemStack stack)
    {
        IRot cap = RotCapHelper.getRot(stack);

        return (cap != null) ? cap.evaluateRot(world, stack) : stack;
    }

    /*
     * Set rot on crafted items dependent on the ingredients
     */
    public static void handleCraftedRot(World world, IInventory craftMatrix, ItemStack crafting)
    {
        IRot ccap = RotCapHelper.getRot(crafting);

        if (ccap != null)
        {
            ccap.handleCraftedRot(world, craftMatrix, crafting);
        } // else crafted item doesn't rot

        return;
    }
}
