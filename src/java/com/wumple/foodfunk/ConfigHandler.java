package com.wumple.foodfunk;

import javax.annotation.Nullable;

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

	public static void addDefaultRotProperty(String name, String rotID, int days)
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
	}

	public static void addDefaultRotProperty(String name, @Nullable Item rotItem, int days)
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

		addDefaultRotProperty(name, rotID, days);
	}

	public static void addDefaultRotProperty(Item item, @Nullable Item rotItem, int days)
	{
		// check for null Item in case another mod removes a vanilla item
		if (item != null)
		{
			ResourceLocation resLoc = Item.REGISTRY.getNameForObject(item);
			if (resLoc != null)
			{
				String name = resLoc.toString();
				addDefaultRotProperty(name, rotItem, days);
			}
		}
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
		//addDefaultRotProperty(ObjectHandler.rotten_food, null, DAYS_NO_ROT);
		addDefaultRotProperty("foodfunk:rotten_food", "", DAYS_NO_ROT);
		// TODO addDefaultRotProperty(ObjectHandler.rotted_item, null, DAYS_NO_ROT);
		addDefaultRotProperty(Items.MILK_BUCKET, ObjectHandler.spoiled_milk, 7);
		addDefaultRotProperty(Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, 7);
		addDefaultRotProperty(
				new Item[] {Items.FERMENTED_SPIDER_EYE, Items.BEEF, Items.CHICKEN, Items.PORKCHOP, Items.FISH, Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_PORKCHOP, Items.COOKED_FISH},
				Items.ROTTEN_FLESH, 7);

		ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
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