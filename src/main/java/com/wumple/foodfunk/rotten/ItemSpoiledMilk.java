package com.wumple.foodfunk.rotten;

import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemSpoiledMilk extends ItemBucketMilk
{

    public ItemSpoiledMilk()
    {
        super();
        setCreativeTab(CreativeTabs.MISC);
        
        Potion potion =  Potion.getPotionFromResourceLocation(ConfigContainer.rotten.mobEffect);
        
        if (potion != null)
        {
            setPotionEffect(new PotionEffect(potion, ConfigContainer.rotten.mobEffectDuration), (float)ConfigContainer.rotten.mobEffectProbability);
        }
        
        RegistrationHelpers.nameHelper(this, "foodfunk:spoiled_milk");
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using the Item before the action is complete.
     */
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if (!worldIn.isRemote)
        {
        	onFoodEaten(stack, worldIn, entityLiving);
        }

        if (entityLiving instanceof EntityPlayerMP)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP) entityLiving;
            CriteriaTriggers.CONSUME_ITEM.trigger(entityplayermp, stack);
            entityplayermp.addStat(StatList.getObjectUseStats(this));
        }

        if (entityLiving instanceof EntityPlayer && !((EntityPlayer) entityLiving).capabilities.isCreativeMode)
        {
            stack.shrink(1);
        }

        return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
    }
    
    // ----------------------------------------------------------------------
    // Potion effect support (from ItemFood)
    
    /** represents the potion effect that will occurr upon eating this food. Set by setPotionEffect */
    private PotionEffect potionId;
    /** probably of the set potion effect occurring */
    private float potionEffectProbability;
    
    public ItemSpoiledMilk setPotionEffect(PotionEffect effect, float probability)
    {
        this.potionId = effect;
        this.potionEffectProbability = probability;
        return this;
    }
    
    protected void onFoodEaten(ItemStack stack, World worldIn, EntityLivingBase player)
    {
        if (!worldIn.isRemote && this.potionId != null && worldIn.rand.nextFloat() < this.potionEffectProbability)
        {
            player.addPotionEffect(new PotionEffect(this.potionId));
        }
    }
}
