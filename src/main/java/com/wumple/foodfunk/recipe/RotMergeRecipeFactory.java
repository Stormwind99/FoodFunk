package com.wumple.foodfunk.recipe;

import javax.annotation.Nonnull;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.util.capability.copier.CapMergeRecipeFactory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

/**
 * RecipeFactory to merge different rot itemstacks into one itemstack
 */
public class RotMergeRecipeFactory extends CapMergeRecipeFactory<IRot>
{
    @Override
    public ResourceLocation getResourceLocation()
    { return new ResourceLocation(Reference.MOD_ID, "rot_merge_crafting"); }

    @Override
    public CapMergeRecipe newCapMergeRecipe(ResourceLocation group, ItemStack result, Object... recipe)
    {
        return new RotMergeRecipe(group, result, recipe);
    }
    
    /**
     * The actual recipe to merge different rot itemstacks into one itemstack
     */
    public class RotMergeRecipe extends CapMergeRecipe
    {
        public void log(String msg)
        {
            if (ConfigContainer.zdebugging.debug)
            {
                FoodFunk.logger.info(msg);
            }
        }

        public RotMergeRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe)
        {
            super(group, result, recipe);
        }

        @Override
        protected IRot getCap(ICapabilityProvider provider)
        {
            return IRot.getMyCap(provider);
        } 
     }
}