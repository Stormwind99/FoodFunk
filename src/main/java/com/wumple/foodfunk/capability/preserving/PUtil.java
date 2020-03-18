package com.wumple.foodfunk.capability.preserving;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class PUtil
{
    static public IPreserving.IPreservingOwner to(ItemStack other)
    {
        return new IPreserving.ItemStackPreservingOwner(other);
    }
    
    static public IPreserving.IPreservingOwner to(TileEntity other)
    {
        return new IPreserving.TileEntityPreservingOwner(other);
    }

    static public IPreserving.IPreservingOwner to(Entity other)
    {
        return new IPreserving.EntityPreservingOwner(other);
    }
}