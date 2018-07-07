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

        // default ""
        @Name("Rotten ID")
        @Config.Comment("Rots into this item.  Set blank to rot into nothing")
        public HashMap<String, String> rotID = new HashMap<String, String>();
    }

    @Name("Preserving")
    @Config.Comment("Set preserving rations for containers.")
    public static Preserving preserving = new Preserving();

    public static class Preserving
    {
        @Name("Preserving ratio")
        @Config.Comment("Contents will rot normally at 0, half speed at 50, and never at 100")
        @RangeInt(min = 0, max = 100)
        public HashMap<String, Integer> ratios = new HashMap<String, Integer>();
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