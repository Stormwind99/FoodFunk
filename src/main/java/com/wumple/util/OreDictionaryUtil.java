package com.wumple.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryUtil
{
    public static boolean hasOreName(ItemStack stack, String oreName)
    {
        if (stack.isEmpty())
            return false;

        if (stack.getItem() == null)
        {
            return false;
        }

        int id = OreDictionary.getOreID(oreName);
        for (int i : OreDictionary.getOreIDs(stack))
        {
            if (i == id)
                return true;
        }
        return false;
    }
    
    // from https://github.com/MinecraftModDevelopment/Modding-Resources/blob/master/dev_pins.md
    public static boolean oreDictMatches(ItemStack stack1, ItemStack stack2){
        if (OreDictionary.itemMatches(stack1, stack2, true)){
            return true;
        }
        else {
            int[] oreIds = OreDictionary.getOreIDs(stack1);
            for (int i = 0; i < oreIds.length; i ++){
                if (OreDictionary.containsMatch(true, OreDictionary.getOres(OreDictionary.getOreName(oreIds[i])), stack2)){
                    return true;
                }
            }
        }
        return false;
    }
}
