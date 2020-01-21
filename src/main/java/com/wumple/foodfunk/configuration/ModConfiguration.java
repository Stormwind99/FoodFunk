package com.wumple.foodfunk.configuration;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.wumple.foodfunk.Reference;
import com.wumple.util.config.ConfigUtil;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

// See
// https://github.com/McJty/YouTubeModding14/blob/master/src/main/java/com/mcjty/mytutorial/Config.java
// https://wiki.mcjty.eu/modding/index.php?title=Tut14_Ep6

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfiguration
{
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec CLIENT_CONFIG;

	public static final String CATEGORY_GENERAL = "General";
	public static final String CATEGORY_DEBUGGING = "Debugging";

	public static class General
	{
		public static ForgeConfigSpec.BooleanValue enabled;
		public static ForgeConfigSpec.IntValue chunkingPercentage;
		public static ForgeConfigSpec.IntValue evaluationInterval;

		public static ForgeConfigSpec.ConfigValue<Config> rotID;
		public static ForgeConfigSpec.ConfigValue<Config> rotDays;
		public static ForgeConfigSpec.ConfigValue<Config> preserving;
		public static ForgeConfigSpec.ConfigValue<Config> dimensions;

		private static void setupConfig()
		{
			COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);

			//@Name("Enable rot")
			enabled = COMMON_BUILDER.comment("Configured items rot over time.").define("enabled", true);

			// @Name("Chunking percentage")
			chunkingPercentage = COMMON_BUILDER.comment(
					"Allows stacking of items created around same time.  Higher values will increase stacking at cost of strange initial rot percentage.")
					.defineInRange("chunkingPercentage", 1, 0, 100);

			// @Name("Ticks between evaluation")
			evaluationInterval = COMMON_BUILDER.comment(
					"Ticks between evaluating for rot and preservation.  Increase to reduce CPU expense, in exchange for longer delays seeing rot and preservation.")
					.defineInRange("evaluationInterval", 90, 1, Integer.MAX_VALUE);

			// @Name("Rotten ID")
			rotID = ConfigUtil.buildSet(COMMON_BUILDER, "rotID", "Rots into this item.  Set blank to rot into nothing");

			// default 7, also see DAYS_NO_ROT = -1
			// @Name("Days to rot")
			rotDays = ConfigUtil.buildSet(COMMON_BUILDER, "rotDays",
					"Days to rot. -1 disables rotting on this item. (min = -1)");

			// @Name("Preserving ratio")
			preserving = ConfigUtil.buildSet(COMMON_BUILDER, "preserving",
					"When in listed container, contents will rot normally at 0, half speed at 50, and never at 100 (min = -100, max = 100)");

			// @Name("Dimension ratio")
			dimensions = ConfigUtil.buildSet(COMMON_BUILDER, "dimensions",
					"When in listed dimension, contents will rot double speed at 200, normally at 100, never at 0, and half speed at -100 (min = -1600, max = 1600)");

			COMMON_BUILDER.pop();
		}
	}

	public static class Debugging
	{
		public static ForgeConfigSpec.BooleanValue debug;
		public static ForgeConfigSpec.DoubleValue rotMultiplier;

		private static void setupConfig()
		{
			// @Config.Comment("Debugging options")
			COMMON_BUILDER.comment("Debugging settings").push(CATEGORY_DEBUGGING);

			// @Name("Debug mode")
			debug = COMMON_BUILDER.comment("Enable general debug features, display extra debug info").define("debug",
					false);

			// @Name("Rot time multiplier")
			rotMultiplier = COMMON_BUILDER.comment("Speed or slow all rot. < 1 faster, > 1 slower.")
					.defineInRange("rotMultiplier", 1.0D, Double.MIN_VALUE, Double.MAX_VALUE);

			COMMON_BUILDER.pop();
		}
	}

	static
	{
		General.setupConfig();
		Debugging.setupConfig();

		COMMON_CONFIG = COMMON_BUILDER.build();
		CLIENT_CONFIG = CLIENT_BUILDER.build();
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path)
	{

		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave()
				.writingMode(WritingMode.REPLACE).build();

		configData.load();
		spec.setConfig(configData);
	}

	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent)
	{
	}

	@SubscribeEvent
	public static void onReload(final ModConfig.ConfigReloading configEvent)
	{
	}

	public static void register(final ModLoadingContext context)
	{
		context.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
		context.registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);

		loadConfig(CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID + "-client.toml"));
		loadConfig(COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Reference.MOD_ID + "-common.toml"));
	}

	// ------------------------------------------------------------------------

	@SubscribeEvent
	public static void modConfig(ModConfig.ModConfigEvent event)
	{
		ModConfig config = event.getConfig();
		if (config.getSpec() != COMMON_CONFIG)
			return;

		ConfigHandler.init();
		
		ConfigUtil.handleConfigSet(General.rotID.get(), c -> {
		}, ConfigHandler.rotting.get1().getMap());
		ConfigUtil.handleConfigSet(General.rotDays.get(), c -> {
		}, ConfigHandler.rotting.get2().getMap());
		ConfigUtil.handleConfigSet(General.dimensions.get(), c -> {
		}, ConfigHandler.dimensions.getMap());
		ConfigUtil.handleConfigSet(General.preserving.get(), c -> {
		}, ConfigHandler.preserving.getMap());

		ConfigHandler.postinit();
	}
}
