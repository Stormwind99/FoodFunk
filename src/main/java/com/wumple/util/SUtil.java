package com.wumple.util;

import java.util.AbstractList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

public class SUtil
{
	static public boolean isEmpty(ItemStack stack)
	{
		return ((stack == null) || (stack.isEmpty()));
	}
	
	/**
	 * shrink the itemStack in itemStacks[currentItemSlot] by amount
	 * If itemStack is a contained item and used up, leave behind empty container
	 * @return ItemStack in itemStacks[currentItemSlot] afterward
	 */
	static public ItemStack shrink(AbstractList<ItemStack> itemStacks, int currentItemSlot, int amount)
	{
		ItemStack itemStack = itemStacks.get(currentItemSlot);
        
        if (itemStack.getCount() == 1)
        {
        	// returns new containeritem if item has one, or empty stack if not
        	ItemStack newStack = ForgeHooks.getContainerItem(itemStack);
            itemStacks.set(currentItemSlot, newStack);
            itemStack.shrink(1);
            itemStack = newStack;
        }
        else
        {
        	itemStack.shrink(1);
        }
        
        // TODO what about excess empty containers in case the filled container stacked?
        // Could return list of excess empty items and let caller deal with it as desired (could dump on ground, or place in player inventory)
        
        return itemStack;
	}
}
