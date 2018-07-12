package com.wumple.foodfunk.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.wumple.foodfunk.ObjectHandler;
import com.wumple.foodfunk.Reference;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.oredict.OreDictionary;

public class ConfigHandler
{
    public static final int DAYS_NO_ROT = -1;
    public static final long TICKS_PER_DAY = 24000L;
    public static final String FOOD_TAG = "minecraft:food";

    public static void init()
    {
        // handle all food with a "default" entry
        Rotting.addDefaultRotProperty("minecraft:food", ObjectHandler.rotten_food, 7);
        Rotting.addDefaultRotProperty(Items.ROTTEN_FLESH, "minecraft:rotten_flesh", null, DAYS_NO_ROT);
        Rotting.addDefaultRotProperty(ObjectHandler.rotten_food, "foodfunk:rotten_food", null, DAYS_NO_ROT);
        // TODO Rotting.addDefaultRotProperty(ObjectHandler.rotted_item, null, DAYS_NO_ROT);
        Rotting.addDefaultRotProperty(Items.MILK_BUCKET, "minecraft:milk_bucket", ObjectHandler.spoiled_milk, 7);
        Rotting.addDefaultRotProperty(Items.SPIDER_EYE, "minecraft:spider_eye", Items.FERMENTED_SPIDER_EYE, 7);
        Rotting.addDefaultRotProperty(Items.FERMENTED_SPIDER_EYE, "minecraft:fermented_spider_eye", Items.ROTTEN_FLESH,
                7);
        Rotting.addDefaultRotProperty(Items.BEEF, "minecraft:beef", Items.ROTTEN_FLESH, 7);
        Rotting.addDefaultRotProperty(Items.CHICKEN, "minecraft:chicken", Items.ROTTEN_FLESH, 7);
        Rotting.addDefaultRotProperty(Items.PORKCHOP, "minecraft:porkchop", Items.ROTTEN_FLESH, 7);
        Rotting.addDefaultRotProperty(Items.FISH, "minecraft:fish", Items.ROTTEN_FLESH, 7);
        Rotting.addDefaultRotProperty(Items.COOKED_BEEF, "minecraft:cooked_beef", Items.ROTTEN_FLESH, 7);
        Rotting.addDefaultRotProperty(Items.COOKED_CHICKEN, "minecraft:cooked_chicken", Items.ROTTEN_FLESH, 7);
        Rotting.addDefaultRotProperty(Items.COOKED_PORKCHOP, "minecraft:cooked_porkchop", Items.ROTTEN_FLESH, 7);
        Rotting.addDefaultRotProperty(Items.COOKED_FISH, "minecraft:cooked_fish", Items.ROTTEN_FLESH, 7);

        Preserving.addDefaultPreservingProperty("foodfunk:esky", 50);
        Preserving.addDefaultPreservingProperty("foodfunk:freezer", 100);
        Preserving.addDefaultPreservingProperty("cookingforblockheads:fridge", 50);
        // Doubt this next one will work until cookingforblockheads does the MC 1.13 flattening
        Preserving.addDefaultPreservingProperty("cookingforblockheads:ice_unit", 100);
        Preserving.addDefaultPreservingProperty("cfm:esky", 50);
        Preserving.addDefaultPreservingProperty("minecraft:cfmesky", 50);
        Preserving.addDefaultPreservingProperty("cfm:freezer", 100);
        Preserving.addDefaultPreservingProperty("minecraft:cfmfridge", 100);
        Preserving.addDefaultPreservingProperty("minecraft:cfmfreezer", 100);

        ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
    }

    // ----------------------------------------------------------------------
    // Rotting

    public static class Rotting
    {
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
            for (Item item : items)
            {
                addDefaultRotProperty(item, rotItem, _days);
            }
        }

        public static boolean doesRot(ItemStack stack)
        {
            RotProperty rotProps = getRotProperty(stack);
            return (rotProps == null) ? false : rotProps.doesRot();
        }

        public static class RotProperty
        {
            public String id;
            public String rotID = null;
            public int days = ConfigHandler.DAYS_NO_ROT;
            public Integer rotMeta = null;

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
                setRotID(_rotID);
            }

            public long getRotTime()
            {
                if (ConfigContainer.zdebugging.debug)
                {
                    double ticksPerDay = ConfigContainer.zdebugging.rotMultiplier * ConfigHandler.TICKS_PER_DAY;
                    return days * (long) ticksPerDay;
                }

                return days * ConfigHandler.TICKS_PER_DAY;
            }

            public boolean doesRot()
            {
                return (days > DAYS_NO_ROT);
            }

            public void setRotID(String key)
            {
                // metadata support - class:name@metadata
                int length = (key != null) ? key.length() : 0;
                if ((length >= 2) && (key.charAt(length - 2) == '@'))
                {
                    String metastring = key.substring(length - 1);
                    rotMeta = Integer.valueOf(metastring);
                    rotID = key.substring(0, length - 2);
                }
                else if ((length >= 3) && (key.charAt(length - 3) == '@'))
                {
                    String metastring = key.substring(length - 2);
                    rotMeta = Integer.valueOf(metastring);
                    rotID = key.substring(0, length - 3);
                }
                else
                {
                    rotID = key;
                    rotMeta = null;
                }
            }
        }

        @Nullable
        protected static RotProperty getRotPropertyBase(String key1)
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
                    rotProp.setRotID(rotID);
                }
            }

            return rotProp;
        }

        @Nullable
        protected static RotProperty getRotPropertyBase(List<String> keys)
        {
            RotProperty rotProp = null;

            for (String key : keys)
            {
                rotProp = getRotPropertyBase(key);
                if (rotProp != null)
                {
                    break;
                }
            }

            return rotProp;
        }

        @Nullable
        protected static RotProperty getRotPropertyBase(Item item)
        {
            String key1 = "" + Item.REGISTRY.getNameForObject(item);
            // WAS : look up a backup key with item meta data?
            // String key2 = "" + Item.REGISTRY.getNameForObject(item) + "," +
            // itemStack.getItemDamage();

            return getRotPropertyBase(key1);
        }

        @Nullable
        public static RotProperty getRotProperty(Item item)
        {
            RotProperty prop = getRotPropertyBase(item);

            // hack-ish: handle default "minecraft:food" since a official tag for food
            // doesn't exist (at least yet)
            if ((prop == null) && (item instanceof ItemFood))
            {
                prop = getRotPropertyBase(FOOD_TAG);
            }

            return prop;
        }

        @Nullable
        public static RotProperty getRotProperty(ItemStack itemStack)
        {
            if (itemStack == null)
            {
                return null;
            }

            ArrayList<String> nameKeys = new ArrayList<String>();

            Item item = itemStack.getItem();

            String key2 = "" + Item.REGISTRY.getNameForObject(item);

            nameKeys.add(key2 + "@" + itemStack.getMetadata());
            nameKeys.add(key2);

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

            return getRotPropertyBase(nameKeys);
        }
    }

    // ----------------------------------------------------------------------
    // Preserving

    public static class Preserving
    {
        public static boolean addDefaultPreservingProperty(String name, int ratio)
        {
            if (name == null)
            {
                name = "";
            }

            ConfigContainer.preserving.ratios.putIfAbsent(name, ratio);

            return true;
        }

        public static boolean doesPreserve(TileEntity it)
        {
            ResourceLocation loc = (it == null) ? null : TileEntity.getKey(it.getClass());
            String key = (loc == null) ? null : loc.toString();
            boolean preserving = false;

            if ((key != null) && ConfigContainer.preserving.ratios.containsKey(key))
            {
                int ratio = ConfigContainer.preserving.ratios.get(key);
                if (ratio != 0)
                {
                    preserving = true;
                }
            }

            return preserving;
        }

        public static int getPreservingRatio(TileEntity it)
        {
            ResourceLocation loc = (it == null) ? null : TileEntity.getKey(it.getClass());
            String key = (loc == null) ? null : loc.toString();

            int ratio = 0;
            if ((key != null) && ConfigContainer.preserving.ratios.containsKey(key))
            {
                ratio = ConfigContainer.preserving.ratios.get(key);

            }

            return ratio;
        }
    }
}