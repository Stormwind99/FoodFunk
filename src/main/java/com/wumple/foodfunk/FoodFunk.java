package com.wumple.foodfunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wumple.foodfunk.capability.preserving.Preserving;
import com.wumple.foodfunk.capability.rot.Rot;
import com.wumple.foodfunk.configuration.ModConfiguration;
import com.wumple.foodfunk.crafting.recipe.RotMergeRecipe;
import com.wumple.foodfunk.rotten.BiodegradableItem;
import com.wumple.foodfunk.rotten.RottedItem;
import com.wumple.foodfunk.rotten.RottenFoodItem;
import com.wumple.foodfunk.rotten.SpoiledMilkItem;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(Reference.MOD_ID)
public class FoodFunk
{
	public static final SimpleChannel network = ModNetwork.getNetworkChannel();

	public Logger getLogger()
	{
		return LogManager.getLogger(Reference.MOD_ID);
	}

	public FoodFunk()
	{
		ModConfiguration.register(ModLoadingContext.get());

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void setup(final FMLCommonSetupEvent event)
	{
		Rot.register();
		Preserving.register();
	}

	@SubscribeEvent
	public void onFingerprintViolation(final FMLFingerprintViolationEvent event)
	{
		getLogger().warn("Invalid fingerprint detected! The file " + event.getSource().getName()
				+ " may have been tampered with. This version will NOT be supported by the author!");
		getLogger().warn("Expected " + event.getExpectedFingerprint() + " found " + event.getFingerprints().toString());
	}

	@EventBusSubscriber(bus = Bus.MOD)
	public static class RegistryEvents
	{
		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
		{
			event.getRegistry().register(new RottenFoodItem().setRegistryName("rotten_food"));
			event.getRegistry().register(new SpoiledMilkItem().setRegistryName("spoiled_milk"));
			event.getRegistry().register(new RottedItem().setRegistryName("rotted_item"));
			event.getRegistry().register(new BiodegradableItem().setRegistryName("biodegradable_item"));

		}

		public static RotMergeRecipe.Serializer<RotMergeRecipe> CRAFTING_SPECIAL_ROTMERGE;

		@SubscribeEvent
		public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event)
		{
			CRAFTING_SPECIAL_ROTMERGE = new RotMergeRecipe.Serializer<RotMergeRecipe>(RotMergeRecipe::new);
			CRAFTING_SPECIAL_ROTMERGE.setRegistryName("rot_merge_crafting");
			event.getRegistry().registerAll(CRAFTING_SPECIAL_ROTMERGE);
		}

	}
}
