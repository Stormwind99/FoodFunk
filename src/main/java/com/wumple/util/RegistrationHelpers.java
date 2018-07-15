package com.wumple.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistrationHelpers
{
    public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing)
    {
    	assert(thing != null);

        registry.register(thing);
        return thing;
    }

    public static Item regHelperOre(IForgeRegistry<Item> registry, Item thing, String[] oreNames)
    {
    	assert(thing != null);
    	
        registry.register(thing);
        
        for (String oreName : oreNames)
        {
        	OreDictionary.registerOre(oreName, thing);
        }
        
        return thing;
    }
    
    public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing,
            String name)
    {
    	assert(thing != null);
    	assert(name != null);

        nameHelper(thing, name);

        return regHelper(registry, thing);
    }

    public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing,
            ResourceLocation loc)
    {
    	assert(thing != null);
    	assert(loc != null);
    	
        nameHelper(thing, loc);

        return regHelper(registry, thing);
    }

    public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, ResourceLocation loc)
    {
    	assert(thing != null);
    	assert(loc != null);

        thing.setRegistryName(loc);
        String dotname = loc.getNamespace() + "." + loc.getPath();

        if (thing instanceof Block)
        {
            ((Block) thing).setTranslationKey(dotname);
        }
        else if (thing instanceof Item)
        {
            ((Item) thing).setTranslationKey(dotname);
        }
    }

    public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, String name)
    {
    	assert(thing != null);
    	assert(name != null);
    	
        ResourceLocation loc = GameData.checkPrefix(name);
        nameHelper(thing, loc);
    }

    public static SoundEvent registerSound(IForgeRegistry<SoundEvent> registry, String name)
    {
    	assert(name != null);
    	
        ResourceLocation loc = GameData.checkPrefix(name);

        SoundEvent event = new SoundEvent(loc);

        regHelper(registry, event, loc);

        return event;
    }

    @SideOnly(Side.CLIENT)
    public static void registerRender(Item item)
    {
    	assert(item != null);
        ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, loc);
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerRender(Block block)
    {
    	assert(block != null);
    	Item item = Item.getItemFromBlock(block);
    	registerRender(block, item);
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerRender(Block block, Item item)
    {
    	assert(block != null);
    	assert(item != null);
        ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, loc);
    }

    public static ItemBlock registerItemBlock(IForgeRegistry<Item> registry, Block block)
    {
    	assert(block != null);
        ItemBlock item = new ItemBlock(block);
        regHelper(registry, item, block.getRegistryName());
        // regHelper(registry, Item.getItemFromBlock(block), block.getRegistryName());

        return item;
    }
    
    public static ItemBlock registerItemBlockOre(IForgeRegistry<Item> registry, Block block, String[] oreNames)
    {
    	assert(block != null);
        ItemBlock item = registerItemBlock(registry, block);
        
        for (String oreName : oreNames)
        {
        	OreDictionary.registerOre(oreName, item);
        	OreDictionary.registerOre(oreName, block);
        }

        return item;
    }

    public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String key)
    {
    	assert(key != null);
        ResourceLocation loc = GameData.checkPrefix(key);

        assert(loc != null);
        GameRegistry.registerTileEntity(tileEntityClass, loc);
    }
}
