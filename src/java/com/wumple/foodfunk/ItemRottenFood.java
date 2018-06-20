package com.wumple.foodfunk;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.registries.GameData;

public class ItemRottenFood extends ItemFood
{
	public ItemRottenFood(int amount, boolean isWolfFood)
	{
		super(amount, 0.1F, isWolfFood);
		setMaxStackSize(64);
		setCreativeTab(CreativeTabs.DECORATIONS);
		// TODO setTextureName("foodfunk:rot");
		
	    String name = "foodfunk:rotten_food";
	    setRegistryName(GameData.checkPrefix(name));
	    setUnlocalizedName(name);
	}

	public ItemRottenFood(int amount)
	{
		this(amount, false);
	}
	
	public ItemRottenFood()
	{
		this(1, false);
	}
	
	@Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player)
    {
        super.onFoodEaten(stack, world, player);
    
        // TODO achievements    
        // player.addStat(EnviroAchievements.tenSecondRule, 1);
    }
		
	/*
    // TODO support rotten food as bonemeal or disgusting food
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        
        if (ItemDye.applyBonemeal(itemstack, worldIn, pos))
        {
            if (!worldIn.isRemote)
            {
                worldIn.playAuxSFX(2005, par4, par5, par6, 0);
            }

            return true;
        }
        
        return false;
    }
    
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
    	return stack;
    }
    */
}
