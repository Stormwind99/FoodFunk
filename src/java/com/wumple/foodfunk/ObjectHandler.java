package com.wumple.foodfunk;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
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
	
	@ObjectHolder("foodfunk:esky")
	public static final Item esky_item = null;
	
	@ObjectHolder("foodfunk:freezer")
	public static final Item freezer_item = null;
			
		
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
    		final IForgeRegistry<Block> registry = event.getRegistry();
        
    		regHelper(registry, new BlockEsky());
    		regHelper(registry, new BlockFreezer());
    	}

    	@SubscribeEvent
    	public static void registerItems(RegistryEvent.Register<Item> event) {
    		final IForgeRegistry<Item> registry = event.getRegistry();

    	    regHelper(registry, new ItemRottenFood());
    	    regHelper(registry, new ItemSpoiledMilk());

    		registerItemBlock(registry, esky);
    		registerItemBlock(registry, freezer);
    		
    		registerTileEntities();
    	}
    	
    	@SubscribeEvent
    	public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
    		final IForgeRegistry<SoundEvent> registry = event.getRegistry();
        
    		registerSound(registry, "foodfunk:esky_open");
    		registerSound(registry, "foodfunk:esky_close");
    		registerSound(registry, "foodfunk:freezer_open");
    		registerSound(registry, "foodfunk:freezer_close");
    	}
    	
    	@SuppressWarnings("deprecation")
    	public static void registerTileEntities() {
    		GameRegistry.registerTileEntity(TileEntityEsky.class, "foodfunk:esky");
    		GameRegistry.registerTileEntity(TileEntityFreezer.class, "foodfunk:freezer");
    	}
    	
    	@SubscribeEvent
    	public static void registerRenders(ModelRegistryEvent event) {
    		registerRender(rotten_food);
    		registerRender(spoiled_milk);
    		registerRender(esky_item);
    		registerRender(freezer_item);
    	}
    		
		// ----------------------------------------------------------------------
		// Utility
    	
    	private static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing)
	    {
	        registry.register(thing);
	        return thing;
	    }
    	
    	private static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing, String name)
	    {        
        	nameHelper(thing, name);
	        
	        return regHelper(registry, thing);
	    }
    	
    	private static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing, ResourceLocation loc)
	    {        
        	nameHelper(thing, loc);
	        
	        return regHelper(registry, thing);
	    }
    	
    	public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, ResourceLocation loc)
    	{
	        thing.setRegistryName(loc);
	        String dotname = loc.getResourceDomain() + "." + loc.getResourcePath();
	        
	        if (thing instanceof Block)
	        {
	        	((Block)thing).setUnlocalizedName(dotname);
	        }
	        else if (thing instanceof Item)
	        {
	        	((Item)thing).setUnlocalizedName(dotname);
	        }
    	}
    	
    	public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, String name)
    	{
    		ResourceLocation loc = GameData.checkPrefix(name);
    		nameHelper(thing, loc);
        }
    	
    	private static void registerSound(IForgeRegistry<SoundEvent> registry, String name)
    	{
    		ResourceLocation loc = GameData.checkPrefix(name);
    		
    		regHelper(registry, new SoundEvent(loc), loc);
    	}
    	
    	private static void registerRender(Item item)
    	{
    		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
    	}
    	
    	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block)
    	{
    		regHelper(registry, new ItemBlock(block), block.getRegistryName());
    		//regHelper(registry, Item.getItemFromBlock(block), block.getRegistryName());
    	}

    }
}
