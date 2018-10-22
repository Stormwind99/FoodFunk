package com.wumple.foodfunk.configuration;

import java.util.HashMap;

import com.wumple.foodfunk.Reference;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ConfigContainer
{
    @Name("Enable rot")
    @Config.Comment("Configured items rot over time.")
    public static boolean enabled = true;

    @Name("Ticks between evaluation")
    @Config.Comment("Ticks between evaluating for rot and preservation.  Increase to reduce CPU expense, in exchange for longer delays seeing rot and preservation.")
    @RangeInt(min=1)
    public static int evaluationInterval = 90;
    
    @Name("Rotting")
    @Config.Comment("Set rot days and id for items.")
    public static Rotting rotting = new Rotting();

    public static class Rotting
    {
        // default 7, also see DAYS_NO_ROT = -1
        @Name("Days to rot")
        @Config.Comment("-1 disables rotting on this item.")
        @RangeInt(min = -1)
        public HashMap<String, Integer> rotDays = new HashMap<String, Integer>();

        @Name("Replace special vanilla things")
        @Config.Comment("Allows non-item melons, pumpkins, potatos, and carrots to rot by replacing them with custom versions.")
        @RequiresMcRestart
        public boolean replaceSpecialThings = true;
              
        @Name("Chunking percentage")
        @Config.Comment("Allows stacking of items created around same time.  Higher values will increase stacking at cost of strange initial rot percentage.")
        @RangeInt(min=0, max=100)
        public int chunkingPercentage = 1;

        @Name("Planted rottables refresh on growth")
        @Config.Comment("When a planted rottable grows a stage, rot is reset")
        public boolean refreshOnGrowth = true;
        
        @Name("Rottable merge recipe")
        @Config.Comment("Allows merging rottable items with different rot times")
        public boolean rotMergeRecipe = true;        
        
        // default ""
        @Name("Rotten ID")
        @Config.Comment("Rots into this item.  Set blank to rot into nothing")
        public HashMap<String, String> rotID = new HashMap<String, String>();
    }
    
    @Name("Modifiers")
    @Config.Comment("Set values that modify rot speed for preserving containers and dimensions.")
    public static Modifiers modifiers = new Modifiers();

    public static class Modifiers
    {
        @Name("Preserving ratio")
        @Config.Comment("When in listed container, contents will rot normally at 0, half speed at 50, and never at 100")
        @RangeInt(min = -100, max = 100)
        public HashMap<String, Integer> ratios = new HashMap<String, Integer>();
        
        @Name("Dimension ratio")
        @Config.Comment("When in listed dimension, contents will rot double speed at 200, normally at 100, never at 0, and half speed at -100")
        @RangeInt(min = -1600, max = 1600)
        public HashMap<String, Integer> dimensionRatios = new HashMap<String, Integer>();
    }
    
    @Name("Rotten")
    @Config.Comment("Set values about rotten items.")
    public static Rotten rotten = new Rotten();
    
    public static class Rotten
    {
        @Name("Food heal amount")
        @Config.Comment("Food heal amount for the foodfunk:rotten_food item.")
        @RangeInt(min = 0)
        @RequiresMcRestart
        public int foodHealAmount = 1;
        
        @Name("Food saturation amount")
        @Config.Comment("Food saturation for the foodfunk:rotten_food item.")
        @RangeDouble(min = 0.0)
        @RequiresMcRestart
        public double foodSaturation = 0.1;

        @Name("Food mob effect probability")
        @Config.Comment("Probability of mob effect being applied when rotten item eaten.")
        @RangeDouble(min=0.0, max=1.0)
        @RequiresMcRestart
        public double mobEffectProbability = 0.6;

        @Name("Food mob effect")
        @Config.Comment("Mob effect aka potion effect for eating rotten item.")
        @RequiresMcRestart
        public String mobEffect = "minecraft:hunger";
        
        @Name("Food mob effect duration")
        @Config.Comment("Duration of mob effect aka potion effect for eating rotten item.")
        @RangeInt(min = 0)
        @RequiresMcRestart
        public int mobEffectDuration = 600;        
    }

    @Name("Debugging")
    @Config.Comment("Debugging options")
    public static Debugging zdebugging = new Debugging();

    public static class Debugging
    {
        @Name("Debug mode")
        @Config.Comment("Enable debug features on this menu, display extra debug info.")
        public boolean debug = false;

        @Name("Rot time multiplier")
        @Config.Comment("Speed or slow all rot. < 1 faster, > 1 slower.")
        public double rotMultiplier = 1.0;
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    private static class EventHandler
    {
        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         *
         * @param event
         *            The event
         */
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(Reference.MOD_ID))
            {
                ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}