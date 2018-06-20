package com.wumple.foodfunk;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@ObjectHolder("foodfunk")
public class ObjectHandler
{
	// ----------------------------------------------------------------------
	// Blocks
	
	@ObjectHolder("foodfunk:esky")
	public static final Block esky = null;
	
	@ObjectHolder("foodfunk:freezer")
	public static final Block freezer = null;
			
	// ----------------------------------------------------------------------
	// Items
	
	@ObjectHolder("foodfunk:rotten_food")
	public static final Item rotten_food = null;
	
	@ObjectHolder("foodfunk:spoiled_milk")
	public static final Item spoiled_milk = null;
		
	// ----------------------------------------------------------------------
	// SoundEvents
	
	@ObjectHolder("foodfunk:esky_open")
	public static final SoundEvent esky_open = null;

	@ObjectHolder("foodfunk:esky_close")
	public static final SoundEvent esky_close = null;

	@ObjectHolder("foodfunk:freezer_open")
	public static final SoundEvent freezer_open = null;

	@ObjectHolder("foodfunk:freezer_close")
	public static final SoundEvent freezer_close = null;	
	
	// TODO
	// @ObjectHolder("foodfunk:rotted_item")
	// public static final Item rotted_item = null;
				
	// ----------------------------------------------------------------------
	// Events
	
    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {
    	@SubscribeEvent
    	public static void registerBlocks(RegistryEvent.Register<Block> event) {
    		FoodFunk.logger.debug("registerBlocks");
    				
    		final IForgeRegistry<Block> registry = event.getRegistry();
        
    		regHelper(registry, new BlockEsky());
    		regHelper(registry, new BlockFreezer());
    	}

    	@SubscribeEvent
    	public static void registerItems(RegistryEvent.Register<Item> event) {
    		FoodFunk.logger.debug("registerItems");
    		
    		final IForgeRegistry<Item> registry = event.getRegistry();

    	    regHelper(registry, new ItemRottenFood());
    	    regHelper(registry, new ItemSpoiledMilk());

    		regHelper(registry, new ItemBlock(esky), "foodfunk:esky");
    		regHelper(registry, new ItemBlock(freezer), "foodfunk:freezer");
    		
    		registerTileEntities();
    	}
    	
    	@SubscribeEvent
    	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
    		FoodFunk.logger.debug("registerSoundEvents");
    		
    		final IForgeRegistry<SoundEvent> registry = event.getRegistry();
        
    		regHelper(registry, new SoundEvent(new ResourceLocation("foodfunk", "esky_open")), "foodfunk:esky_open");
    		regHelper(registry, new SoundEvent(new ResourceLocation("foodfunk", "esky_close")), "foodfunk:esky_close");
    		regHelper(registry, new SoundEvent(new ResourceLocation("foodfunk", "freezer_open")), "foodfunk:freezer_open");
    		regHelper(registry, new SoundEvent(new ResourceLocation("foodfunk", "freezer_close")), "foodfunk:freezer_close");
    	}
    	
    	@SuppressWarnings("deprecation")
    	public static void registerTileEntities() {
    		FoodFunk.logger.debug("registerTileEntities");
    		
    		GameRegistry.registerTileEntity(TileEntityEsky.class, "foodfunk:esky");
    		GameRegistry.registerTileEntity(TileEntityFreezer.class, "foodfunk:freezer");
    	}
	
		// ----------------------------------------------------------------------
		// Utility
    	public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing)
	    {
	        registry.register(thing);
	        return thing;
	    }
    	
	    public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing, String name)
	    {
	        thing.setRegistryName(GameData.checkPrefix(name));
	        
	        if (thing instanceof Block)
	        {
	        	Block block = (Block)thing;
	        	block.setUnlocalizedName(name);
	        }
	        else if (thing instanceof Item)
	        {
	        	Item item = (Item)thing;
	        	item.setUnlocalizedName(name);
	        }
	        
	        return regHelper(registry, thing);
	    }
    }
}
