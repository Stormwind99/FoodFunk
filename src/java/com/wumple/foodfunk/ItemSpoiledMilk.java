package com.wumple.foodfunk;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraftforge.registries.GameData;

public class ItemSpoiledMilk extends ItemBucketMilk
{

    public ItemSpoiledMilk()
    {
    	super();
    	setCreativeTab(CreativeTabs.DECORATIONS);
    	//setTextureName("bucket_milk");
	    String name = "foodfunk:spoiled_milk";
	    setRegistryName(GameData.checkPrefix(name));
	    setUnlocalizedName(name);
    }
    
    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        if (!worldIn.isRemote)
        {
        	// TODO: player.addPotionEffect(new PotionEffect(Potion.hunger.id, 600, 1)
        	// ItemMilk: entityLiving.curePotionEffects(stack); // FORGE - move up so stack.shrink does not turn stack into air
        }
        
        if (entityLiving instanceof EntityPlayerMP)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)entityLiving;
            CriteriaTriggers.CONSUME_ITEM.trigger(entityplayermp, stack);
            entityplayermp.addStat(StatList.getObjectUseStats(this));
        }

        if (entityLiving instanceof EntityPlayer && !((EntityPlayer)entityLiving).capabilities.isCreativeMode)
        {
            stack.shrink(1);
        }
        
        // TODO achievements
        // player.addStat(EnviroAchievements.tenSecondRule, 1);

        return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
    }
}
