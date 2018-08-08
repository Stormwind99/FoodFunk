package com.wumple.foodfunk.rotten;

import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class ItemRottenFood extends ItemFood
{
    public ItemRottenFood()
    {
        super(ConfigContainer.rotten.foodHealAmount, (float)ConfigContainer.rotten.foodSaturation, false);
        setMaxStackSize(64);
        setCreativeTab(CreativeTabs.MISC);
        
        Potion potion =  Potion.getPotionFromResourceLocation(ConfigContainer.rotten.mobEffect);
        
        if (potion != null)
        {
            setPotionEffect(new PotionEffect(potion, ConfigContainer.rotten.mobEffectDuration), (float)ConfigContainer.rotten.mobEffectProbability);
        }
        
        RegistrationHelpers.nameHelper(this, "foodfunk:rotten_food");
    }
}
