package com.wumple.foodfunk;

import com.wumple.foodfunk.chest.esky.EskyBlock;
import com.wumple.foodfunk.chest.esky.EskyTileEntity;
import com.wumple.foodfunk.chest.freezer.FreezerBlock;
import com.wumple.foodfunk.chest.freezer.FreezerTileEntity;
import com.wumple.foodfunk.chest.icebox.IceboxBlock;
import com.wumple.foodfunk.chest.icebox.IceboxTileEntity;
import com.wumple.foodfunk.chest.larder.LarderBlock;
import com.wumple.foodfunk.chest.larder.LarderTileEntity;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ObjectHolder;

public class ModObjectHolder
{
    @ObjectHolder("foodfunk:rotten_food")
    public static Item rotten_food;

    @ObjectHolder("foodfunk:spoiled_milk")
    public static Item spoiled_milk;
    
    @ObjectHolder("foodfunk:rotted_item")
    public static Item rotted_item;

    @ObjectHolder("foodfunk:biodegradable_item")
    public static Item biodegradable_item;
    
    // ------------------------------------------------------------------------
    // containers
    
    @ObjectHolder("foodfunk:larder")
    public static LarderBlock LarderBlock;

    @ObjectHolder("foodfunk:icebox")
    public static IceboxBlock IceboxBlock;

    @ObjectHolder("foodfunk:esky")
    public static EskyBlock EskyBlock;

    @ObjectHolder("foodfunk:freezer")
    public static FreezerBlock FreezerBlock;

    @ObjectHolder("foodfunk:larder")
    public static TileEntityType<LarderTileEntity> LarderBlock_Tile;
    
    @ObjectHolder("foodfunk:icebox")
    public static TileEntityType<IceboxTileEntity> IceboxBlock_Tile;
    
    @ObjectHolder("foodfunk:esky")
    public static TileEntityType<EskyTileEntity> EskyBlock_Tile;
    
    @ObjectHolder("foodfunk:freezer")
    public static TileEntityType<FreezerTileEntity> FreezerBlock_Tile;
    
    // ----------------------------------------------------------------------
    // SoundEvents

    // @ObjectHolder("foodfunk:larder_open")
    public static SoundEvent larder_open = null;

    // @ObjectHolder("foodfunk:larder_close")
    public static SoundEvent larder_close = null;
    
    // @ObjectHolder("foodfunk:icebox_open")
    public static SoundEvent icebox_open = null;

    // @ObjectHolder("foodfunk:icebox_close")
    public static SoundEvent icebox_close = null;
    
    // @ObjectHolder("foodfunk:esky_open")
    public static SoundEvent esky_open = null;

    // @ObjectHolder("foodfunk:esky_close")
    public static SoundEvent esky_close = null;

    // @ObjectHolder("foodfunk:freezer_open")
    public static SoundEvent freezer_open = null;

    // @ObjectHolder("foodfunk:freezer_close")
    public static SoundEvent freezer_close = null;
}
