package com.wumple.util.config;

import java.util.ArrayList;
import java.util.Map;

import com.wumple.util.Util;

import akka.japi.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DualMatchingConfig<T, U>
{
	public final MatchingConfig<T> config1;
	public final MatchingConfig<U> config2;
		
	public DualMatchingConfig(Map<String, T> config1In, T falseValue1In, Map<String, U> config2In, U falseValue2In)
	{
		config1 = new MatchingConfig<T>(config1In, falseValue1In);
		config2 = new MatchingConfig<U>(config2In, falseValue2In);
	}
	
	// ----------------------------------------------------------------------
	// Add default properties to config
	
	// --- add by String
	
    public boolean addDefaultProperty(String name, T amount1In, U amount2In)
    {
    	return Util.checkBoth(
    			config1.addDefaultProperty(name, amount1In), 
    			config2.addDefaultProperty(name, amount2In) );
    }
    
    public boolean addDefaultProperty(String[] items, T amount1In, U amount2In)
    {
    	return Util.checkBoth(
    			config1.addDefaultProperty(items, amount1In), 
    			config2.addDefaultProperty(items, amount2In) );
    }
    
    // --- add by Item
    
    public boolean addDefaultProperty(Item item, T amount1In, U amount2In)
    {
    	return Util.checkBoth(
    			config1.addDefaultProperty(item, amount1In), 
    			config2.addDefaultProperty(item, amount2In) );
    }

    public boolean addDefaultProperty(Item item, String backup, T amount1In, U amount2In)
    {
    	return Util.checkBoth(
    			config1.addDefaultProperty(item, backup, amount1In), 
    			config2.addDefaultProperty(item, backup, amount2In) );
    }
    
    // ----------------------------------------------------------------------
    // Get value for different types
    
    public Pair<T,U> getProperty(ItemStack itemStack)
    {
    	ArrayList<String> nameKeys = MatchingConfig.getItemStackNameKeys(itemStack);
    	
    	return new Pair<T,U>(config1.getProperty(nameKeys), config2.getProperty(nameKeys));
    }
    
    public Pair<T,U> getProperty(String name)
    {
    	return new Pair<T,U>(config1.getProperty(name), config2.getProperty(name));
    }
}