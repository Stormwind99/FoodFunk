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
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistrationHelpers
{
    public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing)
    {
        registry.register(thing);
        return thing;
    }

    public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing,
            String name)
    {
        nameHelper(thing, name);

        return regHelper(registry, thing);
    }

    public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing,
            ResourceLocation loc)
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
            ((Block) thing).setUnlocalizedName(dotname);
        }
        else if (thing instanceof Item)
        {
            ((Item) thing).setUnlocalizedName(dotname);
        }
    }

    public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, String name)
    {
        ResourceLocation loc = GameData.checkPrefix(name);
        nameHelper(thing, loc);
    }

    public static SoundEvent registerSound(IForgeRegistry<SoundEvent> registry, String name)
    {
        ResourceLocation loc = GameData.checkPrefix(name);

        SoundEvent event = new SoundEvent(loc);

        regHelper(registry, event, loc);

        return event;
    }

    @SideOnly(Side.CLIENT)
    public static void registerRender(Item item)
    {
        ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, loc);
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerRender(Block block)
    {
    	Item item = Item.getItemFromBlock(block);
    	registerRender(block, item);
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerRender(Block block, Item item)
    {
        ModelResourceLocation loc = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, loc);
    }

    public static ItemBlock registerItemBlock(IForgeRegistry<Item> registry, Block block)
    {
        ItemBlock item = new ItemBlock(block);
        regHelper(registry, item, block.getRegistryName());
        // regHelper(registry, Item.getItemFromBlock(block), block.getRegistryName());

        return item;
    }

    public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String key)
    {
        ResourceLocation loc = GameData.checkPrefix(key);

        GameRegistry.registerTileEntity(tileEntityClass, loc);
    }
}
