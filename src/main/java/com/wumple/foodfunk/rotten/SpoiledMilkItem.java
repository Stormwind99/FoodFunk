package com.wumple.foodfunk.rotten;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.world.World;

public class SpoiledMilkItem extends MilkBucketItem
{
	public SpoiledMilkItem()
	{
		super((new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC));
		innerFood = Foods.ROTTEN_FLESH;
	}

	// ------------------------------------------------------------------------
	// Adapted from MilkBucketItem
	
	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not
	 * called when the player stops using the Item before the action is complete.
	 */
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving)
	{
		if (!worldIn.isRemote)
		{
			applyFoodEffects(worldIn, entityLiving);
		}

		if (entityLiving instanceof ServerPlayerEntity)
		{
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entityLiving;
			CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
			serverPlayerEntity.addStat(Stats.ITEM_USED.get(this));
		}

		if (entityLiving instanceof PlayerEntity && !((PlayerEntity) entityLiving).abilities.isCreativeMode)
		{
			stack.shrink(1);
		}

		return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
	}

	// ------------------------------------------------------------------------
	// adapted from Item

	@Nullable
	protected final Food innerFood;

	public boolean hasInnerFood()
	{
		return this.innerFood != null;
	}

	@Nullable
	public Food getInnerFood()
	{
		return this.innerFood;
	}

	// ------------------------------------------------------------------------
	// adapted from LivingEntity

	protected void applyFoodEffects(World worldIn, LivingEntity livingIn)
	{
		if (hasInnerFood())
		{
			for (Pair<EffectInstance, Float> pair : getInnerFood().getEffects())
			{
				if (!worldIn.isRemote && pair.getLeft() != null && worldIn.rand.nextFloat() < pair.getRight())
				{
					livingIn.addPotionEffect(new EffectInstance(pair.getLeft()));
				}
			}
		}

	}
}
