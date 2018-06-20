package com.wumple.foodfunk;

import java.util.HashMap;

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

	@Name("Rotting")
    @Config.Comment("Set rot days and id for items.")
    public static Rotting rotting = new Rotting();
    
    public static class Rotting
    {
        // default 7, also see DAYS_NO_ROT = -1
    	@Name("Days to rot")
        @Config.Comment("Set this to -1 to disable rotting on this item.")
        @RangeInt(min = -1)
        public HashMap<String, Integer> rotDays = new HashMap<String, Integer>();

    	// default ""
    	@Name("Rotten ID")
    	@Config.Comment("Set blank to rot into nothing")
    	public HashMap<String, String> rotID = new HashMap<String, String>();
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
}