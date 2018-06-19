package com.wumple.foodfunk;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
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
	public static Block esky = null;
	
	@ObjectHolder("foodfunk:freezer")
	public static Block freezer = null;
			
	// ----------------------------------------------------------------------
	// Items
	
	@ObjectHolder("foodfunk:rotten_food")
	public static Item rotten_food = null;
	
	@ObjectHolder("foodfunk:spoiled_milk")
	public static Item spoiled_milk = null;
	
	// TODO
	// @ObjectHolder("foodfunk:rotted_item")
	// public status Item rotted_item = null;
				
	// ----------------------------------------------------------------------
	// Events
	
    @EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {
    	@SubscribeEvent
    	public void registerBlocks(RegistryEvent.Register<Block> event) {
    		final IForgeRegistry<Block> registry = event.getRegistry();
        
    		regHelper(registry, new BlockEsky(), "foodfunk:esky");
    		regHelper(registry, new BlockFreezer(), "foodfunk:freezer");
    	}

    	@SubscribeEvent
    	public void registerItems(RegistryEvent.Register<Item> event) {
    		final IForgeRegistry<Item> registry = event.getRegistry();

    	    regHelper(registry, new ItemRottenFood(), "foodfunk:rotten_food");
    	    regHelper(registry, new ItemSpoiledMilk(), "foodfunk:spoiled_milk");

    		regHelper(registry, new ItemBlock(esky), "foodfunk:esky");
    		regHelper(registry, new ItemBlock(freezer), "foodfunk:freezer");
    		
    		registerTileEntities();
    	}
    	
    	@SuppressWarnings("deprecation")
    	public void registerTileEntities() {
    		GameRegistry.registerTileEntity(TileEntityEsky.class, "foodfunk:esky");
    		GameRegistry.registerTileEntity(TileEntityFreezer.class, "foodfunk:freezer");
    	}
	
		// ----------------------------------------------------------------------
		// Utility
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
	        registry.register(thing);
	        return thing;
	    }
    }
}
