package com.wumple.foodfunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wumple.foodfunk.capability.preserving.Preserving;
import com.wumple.foodfunk.capability.rot.Rot;
import com.wumple.foodfunk.chest.icebox.IceboxBlock;
import com.wumple.foodfunk.chest.icebox.IceboxItemStackTileEntityRenderer;
import com.wumple.foodfunk.chest.larder.LarderBlock;
import com.wumple.foodfunk.chest.larder.LarderItemStackTileEntityRenderer;
import com.wumple.foodfunk.chest.larder.LarderTileEntity;
import com.wumple.foodfunk.configuration.ModConfiguration;
import com.wumple.foodfunk.crafting.recipe.RotMergeRecipe;
import com.wumple.foodfunk.rotten.BiodegradableItem;
import com.wumple.foodfunk.rotten.RottedItem;
import com.wumple.foodfunk.rotten.RottenFoodItem;
import com.wumple.foodfunk.rotten.SpoiledMilkItem;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
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

	public static ModSetup setup = new ModSetup();
	
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
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
		proxy.init();
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
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
		{
			event.getRegistry().register(new LarderBlock());
			event.getRegistry().register(new IceboxBlock());
		}

		@SubscribeEvent
		public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
		{
			Item.Properties properties = new Item.Properties().group(setup.itemGroup);
			
			event.getRegistry().register(new RottenFoodItem().setRegistryName("rotten_food"));
			event.getRegistry().register(new SpoiledMilkItem().setRegistryName("spoiled_milk"));
			event.getRegistry().register(new RottedItem().setRegistryName("rotted_item"));
			event.getRegistry().register(new BiodegradableItem().setRegistryName("biodegradable_item"));

			event.getRegistry().register(new BlockItem(ModObjectHolder.LarderBlock, new Item.Properties().group(setup.itemGroup).setTEISR(() -> LarderItemStackTileEntityRenderer::new)).setRegistryName("larder"));
			event.getRegistry().register(new BlockItem(ModObjectHolder.IceboxBlock, new Item.Properties().group(setup.itemGroup).setTEISR(() -> IceboxItemStackTileEntityRenderer::new)).setRegistryName("icebox"));
		}

		
		
		@SubscribeEvent
		public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event)
		{
			event.getRegistry().register(TileEntityType.Builder.create(LarderTileEntity::new, ModObjectHolder.LarderBlock)
					.build(null).setRegistryName("larder"));
			event.getRegistry().register(TileEntityType.Builder.create(LarderTileEntity::new, ModObjectHolder.IceboxBlock)
					.build(null).setRegistryName("icebox"));
		}
		
		// --------------------------------------------------------------------
		// recipes
		
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
