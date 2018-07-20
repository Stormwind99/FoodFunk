package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.util.capability.CapabilityUtils;
import com.wumple.util.container.Walker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
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

        IItemHandler capability = CapabilityUtils.fetchCapability(entity, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (capability != null)
        {
            evaluateRotContents(world, capability);
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
            evaluateRotContents(world, invo);
        }
        else if (entity instanceof IInventory)
        {
            IInventory invo = (IInventory) entity;
            evaluateRotContents(world, invo);
        }

        return false;
    }

    public static void evaluateRot(World world, TileEntity tile)
    {
        if ((world.isRemote) || (!ConfigContainer.enabled))
        {
            return;
        }

        IItemHandler capability = CapabilityUtils.fetchCapability(tile, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (capability != null)
        {
            evaluateRotContents(world, capability);
        }
        else if (tile instanceof IInventory)
        {
            IInventory invo = (IInventory) tile; // as(tile, IInventory.class);

            evaluateRotContents(world, invo);
        }
    }

    public static void evaluateRot(World world, Container container)
    {
        if ((world.isRemote) || (!ConfigContainer.enabled))
        {
            return;
        }

        if (container instanceof IInventory)
        {
            IInventory invo = (IInventory) container;

            evaluateRotContents(world, invo);
        }
        else
        {
            evaluateRotContents(world, container);
        }
    }
    
    public static void dimensionShift(EntityPlayer player, int fromDim, int toDim)
    {
        int fromDimensionRatio = RotInfo.getDimensionRatio(fromDim);
        int toDimensionRatio = RotInfo.getDimensionRatio(toDim);
        
        Walker.walkContainer(player, (index, handler, stack) -> {
            IRot cap = RotCapHelper.getRot(stack);

            if (cap != null)
            {
                cap.ratioShift(fromDimensionRatio, toDimensionRatio);
            }
        } );
    }

    // ----------------------------------------------------------------------
    // Internal
    
    protected static void evaluateRotContents(World world, IItemHandler inventory)
    {
        Walker.walkContainer(inventory, (index, itemhandler, stack) -> {
            int count = stack.getCount();
            ItemStack rotItem = RotHelper.evaluateRot(world, stack);

            if (rotItem == null || rotItem.isEmpty() || (rotItem != stack))
            {
                if (rotItem == null)
                {
                    rotItem = ItemStack.EMPTY;
                }
                // Equivalent to inventory.setInventorySlotContents(i, rotItem);
                itemhandler.extractItem(index, count, false);
                itemhandler.insertItem(index, rotItem, false);
            }
        });
    }
     
    protected static void evaluateRotContents(World world, Container inventory)
    {
        Walker.walkContainer(inventory, (index, container, stack) -> {
            ItemStack rotItem = RotHelper.evaluateRot(world, stack);

            if (rotItem == null || rotItem.isEmpty() || (rotItem.getItem() != stack.getItem()))
            {
                if (rotItem == null)
                {
                    rotItem = ItemStack.EMPTY;
                }

                if (ConfigContainer.zdebugging.debug)
                {
                    FoodFunk.logger.info("rotInvo-IInventory 2 sending slot " + index + " " + rotItem);
                }

                inventory.putStackInSlot(index, rotItem);
            }

        });

        inventory.detectAndSendChanges();
    }
    
    protected static void evaluateRotContents(World world, IInventory inventory)
    {
        boolean dirty = false;

        Walker.walkContainer(inventory, (index, container, stack) -> {
            // TODO rotItem == slotItem is true when slotItem is just updated (shouldn't
            // happen unless init missed) and not rotted
            ItemStack rotItem = RotHelper.evaluateRot(world, stack);

            if (rotItem == null || rotItem.isEmpty() || (rotItem.getItem() != stack.getItem()))
            {
                if (rotItem == null)
                {
                    rotItem = ItemStack.EMPTY;
                }

                inventory.setInventorySlotContents(index, rotItem);
            }
        });

        if (dirty && inventory instanceof TileEntity)
        {
            ((TileEntity) inventory).markDirty();
        }
    }

}
