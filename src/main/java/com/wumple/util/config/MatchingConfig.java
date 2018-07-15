package com.wumple.util.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.wumple.util.TypeIdentifier;
import com.wumple.util.Util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

/*
 * Wrapper around Forge HashMap<String, T> configs for (itemstack, item, entity, string)->value configs
 */
public class MatchingConfig<T>
{
	protected final Map<String, T> config;
	public final T FALSE_VALUE;
    public static final String FOOD_TAG = "minecraft:food";
    public static final String PLAYER_TAG = "entity:player";

	public MatchingConfig(Map<String, T> configIn, T falseValueIn)
	{
		config = configIn;
		FALSE_VALUE = falseValueIn;
	}
	
	/*
	 *  If using this class and including default config properties, in mod postInit do:
	 *  - add all defaults using addDefaultProperty, then
	 *  - Call ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
	 */
	
	// ----------------------------------------------------------------------
	// Utility
	
	/**
	 * @see TypeIdentifier for opposite direction but similiar code
	 * @param itemStack for which to get namekeys for lookup
	 * @return namekeys to search config for, in order
	 */
    static public ArrayList<String> getItemStackNameKeys(ItemStack itemStack)
    {
        ArrayList<String> nameKeys = new ArrayList<String>();
        
        if (itemStack == null)
        {
        	return nameKeys;
        }

        Item item = itemStack.getItem();

        ResourceLocation loc = Item.REGISTRY.getNameForObject(item);
        
        if (loc != null)
        {
        	String key2 = loc.toString();

        	nameKeys.add(key2 + "@" + itemStack.getMetadata());
        	nameKeys.add(key2);
        }

        if (!itemStack.isEmpty())
        {
            int oreIds[] = OreDictionary.getOreIDs(itemStack);
            for (int oreId : oreIds)
            {
                nameKeys.add(OreDictionary.getOreName(oreId));
            }
        }

        if (item instanceof ItemFood)
        {
            nameKeys.add(FOOD_TAG);
        }
        
        return nameKeys;
    }

    static public ArrayList<String> getEntityNameKeys(Entity entity)
    {
        ArrayList<String> nameKeys = new ArrayList<String>();
        
        if (entity == null)
        {
        	return nameKeys;
        }

    	String name = (entity == null) ? null : EntityList.getEntityString(entity);

    	if (name != null)
    	{
    		nameKeys.add(name);
    	}
    	
        if (entity instanceof EntityPlayer)
        {
        	nameKeys.add(PLAYER_TAG);
        }
        
        return nameKeys;
    }
    
	// ----------------------------------------------------------------------
	// Add default properties to config
	
	// --- add by String
	
    public boolean addDefaultProperty(String name, T amountIn)
    {
        if (name == null)
        {
            name = "";
        }

        config.putIfAbsent(name, amountIn);

        return true;
    }
    
    public boolean addDefaultProperty(String[] items, T amountIn)
    {
    	boolean success = true;
    	
        for (String item : items)
        {
            success &= addDefaultProperty(item, amountIn);
        }
        
        return success;
    }
    
    // --- add by Item
    
    public boolean addDefaultProperty(Item item, T amount)
    {
        // check for null Item in case another mod removes a vanilla item
        if (item != null)
        {
            ResourceLocation resLoc = Item.REGISTRY.getNameForObject(item);
            if (resLoc != null)
            {
                String name = resLoc.toString();
                return addDefaultProperty(name, amount);
            }
        }

        return false;
    }

    public boolean addDefaultProperty(Item item, String backup, T amount)
    {
        String name = backup;

        // check for null Item in case another mod removes a vanilla item
        if (item != null)
        {
            ResourceLocation resLoc = Item.REGISTRY.getNameForObject(item);
            if (resLoc != null)
            {
                name = resLoc.toString();
            }
        }

        return addDefaultProperty(name, amount);
    }
    
    // ----------------------------------------------------------------------
    // Get value for different types
 
    protected T getProperty(String key)
    {
        T amount = null;
        
        if ((key != null) && config.containsKey(key))
        {
            amount = config.get(key);
        }

        return amount;
    }
           
    @Nullable
    protected T getProperty(List<String> keys)
    {
        T amount = null;

        for (String key : keys)
        {
            amount = getProperty(key);
            if (amount != null)
            {
                break;
            }
        }

        return amount;
    }
    
    /**
     * Get the highest priority value we match for stack
     * Checks all keys for stack - expands to multiple keys in defined order: id@meta, id, minecraft:food
     * @return highest priority value for stack, or null if key not found (not FALSE_VALUE)
     */
    @Nullable
    public T getProperty(ItemStack itemStack)
    {
        return getProperty(getItemStackNameKeys(itemStack));
    }
    
    public T getProperty(Entity entity)
    {
    	return getProperty(getEntityNameKeys(entity));
    }
    
    public T getProperty(ResourceLocation loc)
    {
	   String key = (loc == null) ? null : loc.toString();
       return getProperty(key);	
    }
    
    public T getProperty(TileEntity it)
    {
        ResourceLocation loc = (it == null) ? null : TileEntity.getKey(it.getClass());
        return getProperty(loc);
    }
    
    // ----------------------------------------------------------------------
    // get value for different types
    
    /*
     * Get the highest priority value we match for stack
     * Checks all keys for stack - expands to multiple keys in defined order: id@meta, id, minecraft:food
     * @return highest priority value for stack, or FALSE_VALUE if key not found
     */
    public T getValue(ItemStack stack)
    {
    	return Util.getValueOrDefault(getProperty(stack), FALSE_VALUE);
    }

    public T getValue(Entity entity)
    {
    	return Util.getValueOrDefault(getProperty(entity), FALSE_VALUE);
    }

    public T getValue(TileEntity entity)
    {
    	return Util.getValueOrDefault(getProperty(entity), FALSE_VALUE);
    }
    
    public T getValue(ResourceLocation loc)
    {
    	return Util.getValueOrDefault(getProperty(loc), FALSE_VALUE);
    }
    
    // ----------------------------------------------------------------------
    // check for non-FALSE_VALUE for different types
   
    /**
     * Does stack not match FALSE_VALUE?  
     * aka does stack have no entry or the default value as the entry?
     * @returns true if stack doesn't match FALSE_VALUE, false if it does
     */
    public boolean doesIt(ItemStack stack)
    {
        return getValue(stack) != FALSE_VALUE;
    } 
    
    public boolean doesIt(Entity entity)
    {
        return getValue(entity) != FALSE_VALUE;
    } 

    public boolean doesIt(TileEntity entity)
    {
        return getValue(entity) != FALSE_VALUE;
    } 

    public boolean doesIt(ResourceLocation loc)
    {
        return getValue(loc) != FALSE_VALUE;
    }
}