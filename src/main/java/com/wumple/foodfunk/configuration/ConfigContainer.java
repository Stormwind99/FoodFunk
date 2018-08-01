package com.wumple.foodfunk.configuration;

import java.util.HashMap;

import com.wumple.foodfunk.Reference;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
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

        @Name("Replace melons")
        @Config.Comment("Allows melons (and pumpkins) to rot by replacing them with custom versions.")
        public boolean replaceMelons = true;
        
        // default ""
        @Name("Rotten ID")
        @Config.Comment("Rots into this item.  Set blank to rot into nothing")
        public HashMap<String, String> rotID = new HashMap<String, String>();
        
        @Name("Chunking percentage")
        @Config.Comment("Allows stacking of items created around same time.  Higher values will increase stacking at cost of strange initial rot percentage.")
        @RangeInt(min=0, max=100)
        public int chunkingPercentage = 1;
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
        public double rotMultiplier = 1.0F;
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