package com.wumple.foodfunk.rotten;

import com.wumple.util.RegistrationHelpers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;

public class ItemRottenFood extends ItemFood
{
    public ItemRottenFood()
    {
        super(4, 0.1F, false);
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.MISC);
        
        setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), 0.6F);

        RegistrationHelpers.nameHelper(this, "foodfunk:rotten_food");
    }
}
