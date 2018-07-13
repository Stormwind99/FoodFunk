package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.util.capability.CapabilityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RotHandler
{
    public static boolean evaluateRot(World world, Entity entity)
    {
        if (world.isRemote || !ConfigContainer.enabled)
        {
            return false;
        }
        
        if (entity instanceof EntityPlayer)
        {
        	EntityPlayer player = (EntityPlayer) entity;
        	// check open container so user sees updates in open container
            // container listener would not handle this
            if ((player.openContainer != null) && !(player.openContainer instanceof ContainerPlayer)) 
            {
            	evaluateRotContents(world, player.openContainer);
            }
        }

        IItemHandler capability = CapabilityUtils.getCapability(entity, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                null);

        if (capability != null)
        {
            return evaluateRotContents(world, capability);
        }
        else if (entity instanceof EntityItem)
        {
            EntityItem item = (EntityItem) entity;

            ItemStack rotStack = RotHelper.evaluateRot(world, item.getItem());

            if (item.getItem() != rotStack)
            {
                item.setItem(rotStack);
                return true;
            }
        }
        else if (entity instanceof EntityPlayer)
        {
        	EntityPlayer player = (EntityPlayer) entity;
            IInventory invo = player.inventory;
            return evaluateRotContents(world, invo);
        }
        else if (entity instanceof IInventory)
        {
            IInventory invo = (IInventory) entity;
            return evaluateRotContents(world, invo);
        }

        return false;
    }

    public static boolean evaluateRot(World world, TileEntity tile)
    {
        if ((world.isRemote) || (!ConfigContainer.enabled))
        {
            return false;
        }

        IItemHandler capability = CapabilityUtils.getCapability(tile, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (capability != null)
        {
            return evaluateRotContents(world, capability);
        }
        else if (tile instanceof IInventory)
        {
            IInventory invo = (IInventory) tile; // as(tile, IInventory.class);

            return evaluateRotContents(world, invo);
        }

        return false;
    }

    public static boolean evaluateRot(World world, Container container)
    {
        if ((world.isRemote) || (!ConfigContainer.enabled))
        {
            return false;
        }

        if (container instanceof IInventory)
        {
            IInventory invo = (IInventory) container;

            return evaluateRotContents(world, invo);
        }

        return evaluateRotContents(world, container);
    }

    // ----------------------------------------------------------------------
    // Internal

    protected static boolean evaluateRotContents(World world, IInventory inventory)
    {
        int slots = (inventory == null) ? 0 : inventory.getSizeInventory();

        boolean dirty = false;

        try
        {
            for (int i = 0; i < slots; i++)
            {
                ItemStack slotItem = inventory.getStackInSlot(i);

                if ((slotItem != null) && (!slotItem.isEmpty()))
                {
                    // TODO rotItem == slotItem is true when slotItem is just updated (shouldn't
                    // happen unless init missed) and not rotted
                    ItemStack rotItem = RotHelper.evaluateRot(world, slotItem);

                    if (rotItem == null || rotItem.isEmpty() || (rotItem.getItem() != slotItem.getItem()))
                    {
                        if (rotItem == null)
                        {
                            rotItem = ItemStack.EMPTY;
                        }

                        inventory.setInventorySlotContents(i, rotItem);
                        dirty = true;
                    }
                }
            }

            if (dirty && inventory instanceof TileEntity)
            {
                ((TileEntity) inventory).markDirty();
            }

            return dirty;
        }
        catch (Exception e)
        {
            FoodFunk.logger.error("An error occured while attempting to rot inventory:", e);
            return false;
        }
    }

    protected static boolean evaluateRotContents(World world, Container inventory)
    {
        int count = (inventory == null) || (inventory.inventorySlots == null) ? 0 : inventory.inventorySlots.size();

        boolean dirty = false;

        try
        {
            for (int i = 0; i < count; i++)
            {
                Slot slot = inventory.getSlot(i);
                ItemStack slotItem = slot.getStack();

                if ((slotItem != null) && (!slotItem.isEmpty()))
                {
                    ItemStack rotItem = RotHelper.evaluateRot(world, slotItem);

                    if (rotItem == null || rotItem.isEmpty() || (rotItem.getItem() != slotItem.getItem()))
                    {
                        if (rotItem == null)
                        {
                            rotItem = ItemStack.EMPTY;
                        }

                        if (ConfigContainer.zdebugging.debug)
                        {
                            FoodFunk.logger.info("rotInvo-IInventory 2 sending slot " + i + " " + rotItem);
                        }

                        inventory.putStackInSlot(i, rotItem);
                        dirty = true;
                    }
                }
            }

            if (dirty)
            {
                inventory.detectAndSendChanges();
            }

            return dirty;
        }
        catch (Exception e)
        {
            FoodFunk.logger.error("An error occured while attempting to rot inventory:", e);
            return false;
        }
    }

    protected static boolean evaluateRotContents(World world, IItemHandler inventory)
    {
        int slots = (inventory == null) ? 0 : inventory.getSlots();
        if (slots <= 0)
        {
            return false;
        }

        boolean flag = false;

        try
        {
            for (int i = 0; i < slots; i++)
            {
                ItemStack slotItem = inventory.getStackInSlot(i);
                int count = slotItem.getCount();

                if ((slotItem != null) && (!slotItem.isEmpty()))
                {
                    ItemStack rotItem = RotHelper.evaluateRot(world, slotItem);

                    if (rotItem == null || rotItem.isEmpty() || (rotItem != slotItem))
                    {
                        if (rotItem == null)
                        {
                            rotItem = ItemStack.EMPTY;
                        }
                        // Equivalent to inventory.setInventorySlotContents(i, rotItem);
                        inventory.extractItem(i, count, false);
                        inventory.insertItem(i, rotItem, false);
                        flag = true;
                    }
                }
            }

            return flag;
        }
        catch (Exception e)
        {
            FoodFunk.logger.error("An error occured while attempting to rot inventory:", e);
            return false;
        }
    }
}
