package com.wumple.foodfunk.configuration;

import javax.annotation.Nullable;

import com.wumple.foodfunk.ObjectHandler;
import com.wumple.foodfunk.Reference;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

public class ConfigHandler
{
	public static final int DAYS_NO_ROT = -1;
	public static final long TICKS_PER_DAY = 24000L;
	
	public static boolean addDefaultPreservingProperty(String name, int ratio)
	{
		if (name == null)
		{
			name = "";
		}

		ConfigContainer.preserving.ratios.putIfAbsent(name, ratio);
		
		return true;
	}

	public static boolean addDefaultRotProperty(String name, String rotID, int days)
	{
		if (name == null)
		{
			name = "";
		}

		if (rotID == null)
		{
			rotID = "";
		}

		ConfigContainer.rotting.rotDays.putIfAbsent(name, days);
		ConfigContainer.rotting.rotID.putIfAbsent(name, rotID);
		
		return true;
	}

	public static boolean addDefaultRotProperty(String name, @Nullable Item rotItem, int days)
	{
		String rotID = null;

		if (rotItem != null)
		{
			ResourceLocation resLoc = Item.REGISTRY.getNameForObject(rotItem);
			if (resLoc != null)
			{
				rotID = resLoc.toString();
			}
		}

		return addDefaultRotProperty(name, rotID, days);
	}

	public static boolean addDefaultRotProperty(Item item, @Nullable Item rotItem, int days)
	{
		// check for null Item in case another mod removes a vanilla item
		if (item != null)
		{
			ResourceLocation resLoc = Item.REGISTRY.getNameForObject(item);
			if (resLoc != null)
			{
				String name = resLoc.toString();
				return addDefaultRotProperty(name, rotItem, days);
			}
		}
		
		return false;
	}
	
	public static boolean addDefaultRotProperty(Item item, String backup, @Nullable Item rotItem, int days)
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
		
		return addDefaultRotProperty(name, rotItem, days);
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
		addDefaultRotProperty(Items.ROTTEN_FLESH, "minecraft:rotten_flesh", null, DAYS_NO_ROT);
		addDefaultRotProperty(ObjectHandler.rotten_food, "foodfunk:rotten_food", null, DAYS_NO_ROT);
		// TODO addDefaultRotProperty(ObjectHandler.rotted_item, null, DAYS_NO_ROT);
		addDefaultRotProperty(Items.MILK_BUCKET, "minecraft:milk_bucket", ObjectHandler.spoiled_milk, 7);
		addDefaultRotProperty(Items.SPIDER_EYE, "minecraft:spider_eye", Items.FERMENTED_SPIDER_EYE, 7);
		addDefaultRotProperty(Items.FERMENTED_SPIDER_EYE, "minecraft:fermented_spider_eye", Items.ROTTEN_FLESH, 7);
		addDefaultRotProperty(Items.BEEF, "minecraft:beef", Items.ROTTEN_FLESH, 7);
		addDefaultRotProperty(Items.CHICKEN, "minecraft:chicken", Items.ROTTEN_FLESH, 7);
		addDefaultRotProperty(Items.PORKCHOP, "minecraft:porkchop", Items.ROTTEN_FLESH, 7);
		addDefaultRotProperty(Items.FISH, "minecraft:fish", Items.ROTTEN_FLESH, 7);
		addDefaultRotProperty(Items.COOKED_BEEF, "minecraft:cooked_beef", Items.ROTTEN_FLESH, 7);
		addDefaultRotProperty(Items.COOKED_CHICKEN, "minecraft:cooked_chicken", Items.ROTTEN_FLESH, 7);
		addDefaultRotProperty(Items.COOKED_PORKCHOP, "minecraft:cooked_porkchop", Items.ROTTEN_FLESH, 7);
		addDefaultRotProperty(Items.COOKED_FISH, "minecraft:cooked_fish", Items.ROTTEN_FLESH, 7);

		addDefaultPreservingProperty("foodfunk:esky", 50);
		addDefaultPreservingProperty("foodfunk:freezer", 100);
		addDefaultPreservingProperty("cookingforblockheads:fridge", 50);
		// Doubt this next one will work until cookingforblockheads does the CM 1.13 flattening
		addDefaultPreservingProperty("cookingforblockheads:ice_unit", 100);
		addDefaultPreservingProperty("cfm:esky", 50);
		addDefaultPreservingProperty("cfm:freezer", 100);
		
		ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
	}

	public static class RotProperty
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

		public long getRotTime()
		{
			if (ConfigContainer.zdebugging.debug)
			{
				double ticksPerDay = ConfigContainer.zdebugging.rotMultiplier * ConfigHandler.TICKS_PER_DAY;
				return days * (long)ticksPerDay;
			}
			
			return days * ConfigHandler.TICKS_PER_DAY;
		}

		public boolean doesRot()
		{
			return (days > DAYS_NO_ROT);
		}
	}

	@Nullable 
	public static RotProperty getRotPropertyBase(String key1)
	{
		RotProperty rotProp = null;

		if (ConfigContainer.rotting.rotDays.containsKey(key1))
		{
			rotProp = new RotProperty(key1, ConfigContainer.rotting.rotDays.get(key1));
		}

		if (ConfigContainer.rotting.rotID.containsKey(key1))
		{
			String rotID = ConfigContainer.rotting.rotID.get(key1);
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
	public static RotProperty getRotPropertyBase(Item item)
	{
		String key1 = "" + Item.REGISTRY.getNameForObject(item);
		// WAS : look up a backup key with item meta data?
		// String key2 = "" + Item.REGISTRY.getNameForObject(item) + "," + itemStack.getItemDamage();

		return getRotPropertyBase(key1);
	}

	@Nullable 
	public static RotProperty getRotProperty(Item item)
	{
		RotProperty prop = getRotPropertyBase(item);

		// hack-ish: handle default "minecraft:food" since a official tag for food doesn't exist (at least yet)
		if ((prop == null) && (item instanceof ItemFood))
		{
			prop = getRotPropertyBase("minecraft:food");
		}

		// TODO: tag support

		return prop;
	}
	
	@Nullable 
	public static RotProperty getRotProperty(ItemStack itemStack)
	{
		return getRotProperty(itemStack.getItem());
	}
}