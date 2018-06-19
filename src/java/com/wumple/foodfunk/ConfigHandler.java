package com.wumple.foodfunk;

import java.util.HashMap;

import javax.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MOD_ID)
public class ConfigHandler
{
	@Name("Enable item rot")
	@Config.Comment("Will configured items rot over time.")
	public static boolean rotEnabled = true;

	/*
	// default rot time now handled by "minecraft:food" Rotting data
	@Name("Rot time")
	@Config.Comment("Default rot time (days).")
	@RangeInt(min = 0)
	public static int defaultRotTime = 7;	
	*/
	
    @Name("Debug mode")
    @Config.Comment("Enable for debugging help.")
    public boolean debugMode = false;

    @Name("Rotting")
    @Config.Comment("Set rot days and id for items.")
    public static Rotting rotting = new Rotting();
    
    public static class Rotting
    {
        @Name("Days to rot")
        @Config.Comment("Set this to -1 to disable rotting on this item.")
        @RangeInt(min = -1)
        // default 7, also see DAYS_NO_ROT = -1
        public HashMap<String, Integer> rotDays = new HashMap<String, Integer>();
    	    	
    	@Name("Rotten ID")
    	@Config.Comment("Set blank to rot into nothing")
    	// default ""
    	public HashMap<String, String> rotID = new HashMap<String, String>();;
    }
    
	@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
	private static class EventHandler {
		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Reference.MOD_ID)) {
				ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}
    
	// ----------------------------------------------------------------------	
    // Internals
    
    public static final int DAYS_NO_ROT = -1;
    public static final long TICKS_PER_DAY = 24000L;

    public static void addDefaultRotProperty(String name, @Nullable Item rotItem, int days)
    {
    	String rotID = null;
    	if (rotItem != null) {
    		rotID = Item.REGISTRY.getNameForObject(rotItem).toString();
    	}
    	rotting.rotDays.putIfAbsent(name, days);
    	rotting.rotID.putIfAbsent(name, rotID);
    }
    
    public static void addDefaultRotProperty(Item item, @Nullable Item rotItem, int days)
    {
    	String name = Item.REGISTRY.getNameForObject(item).toString();
    	addDefaultRotProperty(name, rotItem, days);
    }
  
    public static void addDefaultRotProperty(Item[] items, Item rotItem, int _days)
    {
    	for (Item item: items)
    	{
    		addDefaultRotProperty(item, rotItem, _days);
    	}
    }
    
    public static void init()
    {
    	// handle all food with a "default" entry
    	addDefaultRotProperty("minecraft:food", ObjectHandler.rotten_food, 7);
    	addDefaultRotProperty(Items.ROTTEN_FLESH, null, DAYS_NO_ROT);
    	addDefaultRotProperty(ObjectHandler.rotten_food, null, DAYS_NO_ROT);
    	// TODO addDefaultRotProperty(ObjectHandler.rotted_item, null, DAYS_NO_ROT);
    	addDefaultRotProperty(Items.MILK_BUCKET, ObjectHandler.spoiled_milk, 7);
    	addDefaultRotProperty(Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, 7);
    	addDefaultRotProperty(
    			new Item[] {Items.FERMENTED_SPIDER_EYE, Items.BEEF, Items.CHICKEN, Items.PORKCHOP, Items.FISH, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_PORKCHOP, Items.COOKED_FISH},
    			Items.ROTTEN_FLESH, 7);
    }
    
    static class RotProperty
    {
    	public String id;
    	public String rotID = null;
    	int days = ConfigHandler.DAYS_NO_ROT;
    	
    	RotProperty()
    	{
    	}
    	
    	RotProperty(String _id, int _days)
    	{
    		id = _id;
    		days = _days;
    	}
    	
    	RotProperty(String _id, String _rotID)
    	{
    		id = _id;
    		rotID = _rotID;
    	}
    	
    	long getRotTime()
    	{
    		return days * ConfigHandler.TICKS_PER_DAY;
    	}
    	
    	boolean doesRot()
    	{
    		return (days < 0);
    	}
    }

    @Nullable 
    public static RotProperty getRotPropertyBase(String key1)
    {
		RotProperty rotProp = null;
		
		if (rotting.rotDays.containsKey(key1))
		{
			rotProp = new RotProperty(key1, rotting.rotDays.get(key1));
		}
		
		if (rotting.rotID.containsKey(key1))
		{
			String rotID = rotting.rotID.get(key1);
			if (rotProp == null)
			{
				rotProp = new RotProperty(key1, rotID);
			}
			else
			{
				rotProp.rotID = rotID;
			}
		}
				
    	return rotProp;
    }

    
    @Nullable 
    public static RotProperty getRotPropertyBase(ItemStack itemStack)
    {
		String key1 = "" + Item.REGISTRY.getNameForObject(itemStack.getItem());
		// WAS : look up a backup key with item meta data?
		// String key2 = "" + Item.REGISTRY.getNameForObject(itemStack.getItem()) + "," + itemStack.getItemDamage();
		
		return getRotPropertyBase(key1);
    }
    
    @Nullable 
    public static RotProperty getRotProperty(ItemStack itemStack)
    {
    	RotProperty prop = getRotPropertyBase(itemStack);
    	
    	// hack-ish: handle default "minecraft:food" since a official tag for food doesn't exist (at least yet)
    	if ((prop == null) && (itemStack.getItem() instanceof ItemFood))
    	{
    		prop = getRotPropertyBase("minecraft:food");
    	}
    	
    	// TODO: tag support
    	
    	return prop;
    }
}