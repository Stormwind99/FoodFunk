package com.wumple.foodfunk.configuration;

import java.util.ArrayList;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.wumple.foodfunk.ModObjectHolder;
import com.wumple.foodfunk.capability.rot.RotProperty;
import com.wumple.util.adapter.IThing;
import com.wumple.util.config.MatchingConfig;
import com.wumple.util.config.NameKeys;
import com.wumple.util.config.SimpleMatchingConfig;
import com.wumple.util.config.StringMatchingDualConfig;

import net.minecraft.item.Items;

public class ConfigHandler
{
	public static boolean isEnabled()
	{
		return ModConfiguration.General.enabled.get();
	}
	
	public static boolean isDebugging()
	{
		return ModConfiguration.Debugging.debug.get();
	}
	
	public static double getRotMultiplier()
	{
		return ModConfiguration.Debugging.rotMultiplier.get();
	}
	
	public static int getChunkingPercentage()
	{
		return ModConfiguration.General.chunkingPercentage.get();
	}
	
	public static int getEvaluationInterval()
	{
		return ModConfiguration.General.evaluationInterval.get();
	}
	
	public static boolean isRotMergeRecipeEnabled()
	{
		return true;
	}
	
    // ----------------------------------------------------------------------
    // Preserving
    
    public static final int NO_PRESERVING = 0;
    public static final int DIMENSIONRATIO_DEFAULT = 100;
	public static final String ID_NO_ROT = "";
    public static final int DAYS_NO_ROT = -1;

    public static MatchingConfig<Integer> preserving = new MatchingConfig<Integer>(NO_PRESERVING);
    public static SimpleMatchingConfig<Integer> dimensions = new SimpleMatchingConfig<Integer>(DIMENSIONRATIO_DEFAULT);
    public static Rotting rotting = new Rotting();
    
    public static void init()
    {
    	preserving.clear();
    	dimensions.clear();
    	rotting.clear();
    }
    
    public static void postinit()
    {
    	addDefaults();
    }
    
    public static void addDefaults()
    {
        // handle all food with a "default" entry
        rotting.addDefaultProperty(NameKeys.foodSpecial, ModObjectHolder.rotten_food, 7);
 
        rotting.addDefaultProperty(ModObjectHolder.rotten_food, "foodfunk:rotten_food", ID_NO_ROT, DAYS_NO_ROT);
        rotting.addDefaultProperty(ModObjectHolder.rotted_item, "foodfunk:rotted_item", ID_NO_ROT, DAYS_NO_ROT);
        rotting.addDefaultProperty(ModObjectHolder.biodegradable_item, "foodfunk:biodegradable_item", ID_NO_ROT, DAYS_NO_ROT);
        rotting.addDefaultProperty("foodfunk:rottable", ModObjectHolder.rotten_food, 7);
        
        // seed foods
        rotting.addDefaultProperty("minecraft:carrot", ModObjectHolder.rotten_food, 10);
        rotting.addDefaultProperty("minecraft:potato", ModObjectHolder.rotten_food, 10);
        // PORT rotting.addDefaultProperty(ModObjectHolder.RegistrationHandler.Ids.listAllSeedFoods, ModObjectHolder.rotten_food, 10);
                
        // melons
        rotting.addDefaultProperty(Items.CAKE, "minecraft:cake", ModObjectHolder.rotten_food, 10);
        rotting.addDefaultProperty("minecraft:melon_block", ModObjectHolder.rotten_food, 14);
        rotting.addDefaultProperty("minecraft:pumpkin", ModObjectHolder.rotten_food, 14);
        // PORT rotting.addDefaultProperty(ModObjectHolder.RegistrationHandler.Ids.listAllMelons, ModObjectHolder.rotten_food, 14);
        
        // more vanilla items
        rotting.addDefaultProperty(Items.ROTTEN_FLESH, "minecraft:rotten_flesh", ID_NO_ROT, DAYS_NO_ROT);
        rotting.addDefaultProperty(Items.MILK_BUCKET, "minecraft:milk_bucket", ModObjectHolder.spoiled_milk, 7);
        // PORT rotting.addDefaultProperty(Items.SPECKLED_MELON, "minecraft:speckled_melon", ModObjectHolder.rotten_food, 28);
        rotting.addDefaultProperty(Items.GOLDEN_APPLE, "minecraft:golden_apple", ModObjectHolder.rotten_food, 28);
        rotting.addDefaultProperty(Items.SPIDER_EYE, "minecraft:spider_eye", Items.FERMENTED_SPIDER_EYE, 5);
        rotting.addDefaultProperty(Items.FERMENTED_SPIDER_EYE, "minecraft:fermented_spider_eye", Items.ROTTEN_FLESH, 10);
        rotting.addDefaultProperty(Items.BEEF, "minecraft:beef", Items.ROTTEN_FLESH, 7);
        // minecraft:chicken also matches entity chicken, causing hang on many world startups
        // PORT rotting.addDefaultProperty(Items.CHICKEN, "minecraft:chicken", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty("#forge:chicken_meat", ModObjectHolder.rotten_food, 14);
        rotting.addDefaultProperty(Items.PORKCHOP, "minecraft:porkchop", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty("#minecraft:fishes", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.COOKED_BEEF, "minecraft:cooked_beef", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.COOKED_CHICKEN, "minecraft:cooked_chicken", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.COOKED_PORKCHOP, "minecraft:cooked_porkchop", Items.ROTTEN_FLESH, 7);

        preserving.addDefaultProperty("foodfunk:esky", 50);
        preserving.addDefaultProperty("foodfunk:freezer", 100);
        preserving.addDefaultProperty("foodfunk:larder", 50);
        preserving.addDefaultProperty("foodfunk:icebox", 100);
        
        preserving.addDefaultProperty("composter:compost_bin", -5000);
        preserving.addDefaultProperty("cookingforblockheads:fridge", 50);
        // Doubt this next one will work until cookingforblockheads does the MC 1.13 flattening
        preserving.addDefaultProperty("cookingforblockheads:ice_unit", 100);
        preserving.addDefaultProperty("cfm:esky", 50);
        preserving.addDefaultProperty("minecraft:cfmesky", 50);
        preserving.addDefaultProperty("cfm:freezer", 100);
        preserving.addDefaultProperty("minecraft:cfmfridge", 100);
        preserving.addDefaultProperty("minecraft:cfmfreezer", 100);
        
        dimensions.addDefaultProperty("-1", 200); // Nether - double rot speed
        dimensions.addDefaultProperty("0", 100); // Overworld - normal rot speed
        dimensions.addDefaultProperty("1", 0); // The End - no rot
        // MAYBE
        // dimensions.addDefaultProperty("7", 150); // Twilight Forest
        // dimensions.addDefaultProperty("20", 300); // Betweenlands
    }

    // ----------------------------------------------------------------------
    // Rotting
    
    public static class Rotting extends StringMatchingDualConfig<Integer>
    {
    	public Rotting()
    	{
    		super(ID_NO_ROT, DAYS_NO_ROT);    
    	}

        public boolean doesRot(IThing thing)
        {
            RotProperty rotProps = getRotProperty(thing);
            return (rotProps == null) ? false : rotProps.doesRot();
        }

        @Nullable
        public RotProperty getRotProperty(IThing thing)
        {
            ArrayList<String> nameKeys = thing.getNameKeys();
            return getRotProperty(nameKeys);
        }

        @Nullable
        public RotProperty getRotProperty(ArrayList<String> nameKeys)
        {     
            RotProperty rotProp = null;
            
            if (nameKeys != null)
            {                
                for (String key : nameKeys)
                {
                    Pair<String,Integer> pair = this.getProperty(key);
                    
                    // beware NPE when unboxing null Integer!
                    String first = (pair != null) ? pair.getLeft() : null;
                    Integer second = (pair != null) ? pair.getRight() : null;
                    int value = (second != null) ? second.intValue() : 0;
    
                    if ((first != null) || (second != null))
                    {
                    	rotProp = new RotProperty(key, first, value);
                    	break;
                    }
                }
            }
            
            return rotProp;
        }
    }
}