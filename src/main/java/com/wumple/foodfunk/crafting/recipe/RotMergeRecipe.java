package com.wumple.foodfunk.crafting.recipe;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.capability.copier.CapMergeRecipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

/**
 * RecipeFactory to merge different rot itemstacks into one itemstack
 */
public class RotMergeRecipe extends CapMergeRecipe<IRot>
{
	public void log(String msg)
	{
	}

	public RotMergeRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn,
			NonNullList<Ingredient> recipeItemsIn)
	{
		super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn)
	{
		return ConfigHandler.isRotMergeRecipeEnabled() && super.matches(inv, worldIn);
	}

	@Override
	protected LazyOptional<? extends IRot> getCap(ICapabilityProvider provider)
	{
		return IRot.getMyCap(provider);
	}
}
