package com.wumple.foodfunk.capability.rot;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Rot capability 
 */
public interface IRot
{
    
    /**
     * The timestamp at which the stack is considered at 0% rot - creation time at first, but advanced by preserving containers
     */
    public long getDate();

    /**
     * Set the timestamp at which the stack is considered at 0% rot - creation time at first, but advanced by preserving containers
     */
    public void setDate(long dateIn);

    /**
     * The amount of time the item takes to rot. The time at which becomes rotten is date + time
     */
    public long getTime();

    /**
     * Set the amount of time the item takes to rot. The time at which becomes rotten is date + time
     */
    public void setTime(long timeIn);

    /**
     * Helper to set both date and time at once
     */
    public void setRot(long dateIn, long timeIn);

    /**
     * Set the owner of this capability, and init based on that owner
     */
    public void setOwner(ItemStack ownerIn);

    /**
     * Move this cap timeIn ticks further into the future
     */
    public void reschedule(long timeIn);

    /**
     * Evaluate this rot, which belongs to stack
     */
    public ItemStack evaluateRot(World world, ItemStack stack);

    /**
     * Build tooltip info based on this rot
     */
    public void doTooltip(ItemStack stack, EntityPlayer entity, boolean advanced, List<String> tips);

    /**
     * Set rot on crafted items dependent on the ingredients
     */
    public void handleCraftedRot(World world, IInventory craftMatrix, ItemStack crafting);
    
    /**
     * Handle shifting dimensions with possibly different rot rates
     */
    public void ratioShift(int fromRatio, int toRatio);
}