package com.wumple.foodfunk.rotten;

import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBiodegradableItem extends Item
{
    public ItemBiodegradableItem()
    {
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.MISC);

        RegistrationHelpers.nameHelper(this, "foodfunk:biodegradable_item");
    }
}
