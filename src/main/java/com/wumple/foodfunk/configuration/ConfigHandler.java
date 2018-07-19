package com.wumple.foodfunk.configuration;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.wumple.foodfunk.ObjectHandler;
import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.rot.RotProperty;
import com.wumple.util.config.MatchingConfig;
import com.wumple.util.config.StringMatchingDualConfig;

import akka.japi.Pair;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;

public class ConfigHandler
{
    // ----------------------------------------------------------------------
    // Preserving
    
    public static final int NO_PRESERVING = 0;
    public static final int DIMENSIONRATIO_DEFAULT = 100;
	public static final String ID_NO_ROT = "";
    public static final int DAYS_NO_ROT = -1;
    public static final long TICKS_PER_DAY = 24000L;

    public static MatchingConfig<Integer> preserving = new MatchingConfig<Integer>(ConfigContainer.modifiers.ratios, NO_PRESERVING);
    public static MatchingConfig<Integer> dimensions = new MatchingConfig<Integer>(ConfigContainer.modifiers.dimensionRatios, DIMENSIONRATIO_DEFAULT);
    public static Rotting rotting = new Rotting();
    		
    public static void init()
    {
        // handle all food with a "default" entry
        rotting.addDefaultProperty(MatchingConfig.FOOD_TAG, ObjectHandler.rotten_food, 7);
        rotting.addDefaultProperty(Items.ROTTEN_FLESH, "minecraft:rotten_flesh", ID_NO_ROT, DAYS_NO_ROT);
        rotting.addDefaultProperty(ObjectHandler.rotten_food, "foodfunk:rotten_food", ID_NO_ROT, DAYS_NO_ROT);
        rotting.addDefaultProperty(ObjectHandler.rotted_item, "foodfunk:rotted_item", ID_NO_ROT, DAYS_NO_ROT);
        rotting.addDefaultProperty(ObjectHandler.biodegradable_item, "foodfunk:biodegradable_item", ID_NO_ROT, DAYS_NO_ROT);
        rotting.addDefaultProperty(Items.MILK_BUCKET, "minecraft:milk_bucket", ObjectHandler.spoiled_milk, 7);
        rotting.addDefaultProperty(Items.CAKE, "minecraft:cake", ObjectHandler.rotten_food, 10);
        rotting.addDefaultProperty(Items.SPIDER_EYE, "minecraft:spider_eye", Items.FERMENTED_SPIDER_EYE, 5);
        rotting.addDefaultProperty(Items.FERMENTED_SPIDER_EYE, "minecraft:fermented_spider_eye", Items.ROTTEN_FLESH, 10);
        rotting.addDefaultProperty(Items.BEEF, "minecraft:beef", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.CHICKEN, "minecraft:chicken", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.PORKCHOP, "minecraft:porkchop", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.FISH, "minecraft:fish", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.COOKED_BEEF, "minecraft:cooked_beef", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.COOKED_CHICKEN, "minecraft:cooked_chicken", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.COOKED_PORKCHOP, "minecraft:cooked_porkchop", Items.ROTTEN_FLESH, 7);
        rotting.addDefaultProperty(Items.COOKED_FISH, "minecraft:cooked_fish", Items.ROTTEN_FLESH, 7);

        preserving.addDefaultProperty("foodfunk:esky", 50);
        preserving.addDefaultProperty("foodfunk:freezer", 100);
        preserving.addDefaultProperty("composter:compost_bin", -100);
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

        ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
    }

    // ----------------------------------------------------------------------
    // Rotting
    
    public static class Rotting extends StringMatchingDualConfig<Integer>
    {
    	public Rotting()
    	{
    		super(ConfigContainer.rotting.rotID, ID_NO_ROT, ConfigContainer.rotting.rotDays, DAYS_NO_ROT);    
    	}

		public boolean doesRot(ItemStack stack)
        {
            RotProperty rotProps = getRotProperty(stack);
            return (rotProps == null) ? false : rotProps.doesRot();
        }

        @Nullable
        public RotProperty getRotProperty(ItemStack itemStack)
        {
            if (itemStack == null)
            {
                return null;
            }

            ArrayList<String> nameKeys = MatchingConfig.getItemStackNameKeys(itemStack);
            
            RotProperty rotProp = null;
            
            for (String key : nameKeys)
            {
                Pair<String,Integer> pair = this.getProperty(key);

                if ((pair.first() != null) || (pair.second() != null))
                {
                	rotProp = new RotProperty(key, pair.first(), pair.second());
                	break;
                }
            }
            
            return rotProp;
        }
    }
}