package com.wumple.foodfunk.recipe;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.configuration.ConfigContainer;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * RecipeFactory to merge different rot itemstacks into one itemstack
 */
public class RotMergeRecipeFactory implements IRecipeFactory
{
    public static void log(String msg)
    {
        if (ConfigContainer.zdebugging.debug)
        {
            FoodFunk.logger.info(msg);
        }
    }
    
    /**
     * hook for JSON to be able to use this recipe
     * 
     * @see _factories.json
     * @see filled_map_transcribe.json
     */
    @Override
    public IRecipe parse(JsonContext context, JsonObject json)
    {
        log("RotMergeRecipeFactory parse");
        ShapelessOreRecipe recipe = ShapelessOreRecipe.factory(context, json);

        return new RotMergeRecipe(new ResourceLocation(Reference.MOD_ID, "rot_merge_crafting"), recipe.getRecipeOutput());
    }

    /**
     * The actual recipe to merge different rot itemstacks into one itemstack
     */
    public static class RotMergeRecipe extends ShapelessOreRecipe
    {

        public RotMergeRecipe(ResourceLocation group, @Nonnull ItemStack result, Object... recipe)
        {
            super(group, result, recipe);
            /*
            // register for events so received onCrafting event can handle map transcription
            MinecraftForge.EVENT_BUS.register(this);
            */
        } 
        
        public class CraftingSearchResults
        {
            public ArrayList<ItemStack> stacks;
            public int count;
            public ItemStack exampleStack;
            public Item exampleItem;
            
            public CraftingSearchResults(ArrayList<ItemStack> stacksIn, int countIn)
            {
                stacks = stacksIn;
                count = countIn;
                
                exampleStack = stacks.get(0);
                exampleItem = exampleStack.getItem();
            }
            
            public ItemStack create()
            {   
                /*
                if (results.destItemStack().hasDisplayName())
                {
                    itemstack2.setStackDisplayName(results.destItemStack().getDisplayName());
                }

                if (results.destItemStack().hasTagCompound())
                {
                    itemstack2.setTagCompound(results.destItemStack().getTagCompound());
                }
                */
                
                //int newCount = Math.min(count, exampleItem.getItemStackLimit(exampleStack));
                int newCount = Math.min(stacks.size(), exampleItem.getItemStackLimit(exampleStack));
                ItemStack newStack = new ItemStack(exampleItem, newCount, exampleStack.getMetadata());
                return newStack;
            }
        }

        /**
         * return the items involved in this recipe, or null if not present
         * 
         * @param inv
         *            inventory to check for tems
         * @return dest and src items found, or null if not found
         */
        protected CraftingSearchResults getStuff(InventoryCrafting inv)
        {
            ItemStack firstStack = ItemStack.EMPTY.copy();
            ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
            int count = 0;
            int rottables = 0;

            for (int j = 0; j < inv.getSizeInventory(); ++j)
            {
                final ItemStack itemstack1 = inv.getStackInSlot(j);

                if (!itemstack1.isEmpty())
                {
                    IRot rot = IRot.getRot(itemstack1);
                    
                    if (rot != null)
                    {
                        if (firstStack.isEmpty())
                        {
                            firstStack = itemstack1;
                        }
                    }
                        
                    if (ItemStack.areItemsEqual(firstStack, itemstack1))
                    {
                        stacks.add(itemstack1);
                        count += itemstack1.getCount();
                        if (rot != null)
                        {
                            rottables++;
                        }
                    }
                    else
                    {
                        return null;
                    }
                }
            }
            
            // must have at least 2 items to stack
            // must have at least one rottable version of the item
            if ((stacks.size() < 2) || (rottables < 1))
            {
                return null;
            }

            return new CraftingSearchResults(stacks, count);
        }

        @Override
        public boolean matches(InventoryCrafting inv, World worldIn)
        {
            log("recipeMatches begin");
            final CraftingSearchResults results = this.getStuff(inv);

            boolean doesMatch = (results != null);

            log("doesMatch " + doesMatch);
            log("recipeMatches end");

            return doesMatch;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv)
        {
            final CraftingSearchResults results = this.getStuff(inv);

            if (results != null)
            {
                ItemStack itemstack2 = results.create();
                
                log("getCraftingResults result " + itemstack2);

                return itemstack2;
            }
            else
            {
                log("getCraftingResults no results");

                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack getRecipeOutput()
        {
            return ItemStack.EMPTY;
        }

        /*
        @Override
        public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
        {
            NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack> withSize(inv.getSizeInventory(), ItemStack.EMPTY);

            // return all unused items
            for (int i = 0; i < nonnulllist.size(); ++i)
            {
                ItemStack itemstack = inv.getStackInSlot(i);
                ItemStack newStack = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
                
                IRot srcCap = RotHandler.getInstance().getCap(itemstack);
                IRot destCap = RotHandler.getInstance().getCap(newStack);
                
                if ((destCap != null) && (srcCap != null))
                {
                    destCap.copyFrom(srcCap);
                }
                
                nonnulllist.set(i, newStack);
            }

            return nonnulllist;
        }
        */

        @Override
        public boolean isDynamic()
        {
            return true;
        }

        /**
         * Used to determine if this recipe can fit in a grid of the given width/height
         */
        @Override
        public boolean canFit(int width, int height)
        {
            return (width * height) >= 2;
        }

        /*
        @SubscribeEvent
        public void onCrafting(final ItemCraftedEvent event)
        {
            final IInventory craftMatrix = event.craftMatrix;
            if (!(craftMatrix instanceof InventoryCrafting)
                    || !craftMatrix.getName().equals("container.crafting"))
            {
                return;
            }

            final InventoryCrafting inv = (InventoryCrafting) craftMatrix;

            final CraftingSearchResults results = this.getStuff(inv);

            if (this.matches(inv, event.player.world))
            {
                // only transcribe on server, and let it send updated map data to client
                if (!event.player.world.isRemote)
                {
                    MapTranscription.transcribeMap(event.crafting, results.srcItemStack(), event.player.world);
                }

                for (int i = craftMatrix.getSizeInventory() - 1; i >= 0; i--)
                {
                    final ItemStack slot = craftMatrix.getStackInSlot(i);

                    if ((slot == null) || slot.isEmpty())
                    {
                        continue;
                    }
                    else if (slot == results.srcItemStack())
                    {
                        // increment stack size by 1 so when decreased automatically by 1 there is still 1 there
                        slot.grow(1);
                    }
                }
            }
        }
        */
    }
}