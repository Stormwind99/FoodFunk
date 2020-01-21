package com.wumple.foodfunk.rotten;

import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class RottenFoodItem extends Item
{
	public RottenFoodItem()
	{
		super((new Item.Properties()).group(ItemGroup.FOOD).food(Foods.ROTTEN_FLESH));
	}
}
