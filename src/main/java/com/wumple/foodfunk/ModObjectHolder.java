package com.wumple.foodfunk;

import com.wumple.foodfunk.chest.icebox.IceboxBlock;
import com.wumple.foodfunk.chest.icebox.IceboxTileEntity;
import com.wumple.foodfunk.chest.larder.LarderBlock;
import com.wumple.foodfunk.chest.larder.LarderTileEntity;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
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
    
    @ObjectHolder("foodfunk:larder")
    public static TileEntityType<LarderTileEntity> LarderBlock_Tile;
    
    @ObjectHolder("foodfunk:icebox")
    public static TileEntityType<IceboxTileEntity> IceboxBlock_Tile;
}
