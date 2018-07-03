package com.wumple.foodfunk.capability.rot;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/*
 * Rot capability 
 */
public interface IRot
{
    public long getDate();

    public void setDate(long dateIn);

    public long getTime();

    public void setTime(long timeIn);

    public void setRot(long dateIn, long timeIn);

    /*
     * Set the owner of this capability, and init based on that owner
     */
    public void setOwner(ItemStack ownerIn);

    /*
     * Move this cap timeIn ticks further into the future
     */
    public void reschedule(long timeIn);

    /*
     * Evaluate this rot, which belongs to stack
     */
    public ItemStack evaluateRot(World world, ItemStack stack);

    /*
     * Build tooltip info based on this rot
     */
    public void doTooltip(ItemStack stack, Entity entity, boolean advanced, List<String> tips);

    /*
     * Set rot on crafted items dependent on the ingredients
     */
    public void handleCraftedRot(World world, IInventory craftMatrix, ItemStack crafting);
}