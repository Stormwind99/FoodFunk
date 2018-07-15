package com.wumple.foodfunk.rotten;

import com.wumple.util.RegistrationHelpers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemRottedItem extends Item
{
    public ItemRottedItem()
    {
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.MISC);

        RegistrationHelpers.nameHelper(this, "foodfunk:rotted_item");
    }
}
