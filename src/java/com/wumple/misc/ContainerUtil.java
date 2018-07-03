package com.wumple.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;

public class ContainerUtil
{
    // horrible hack - find players with container open, by searching nearby and for
    // a known item in the container
    public static NonNullList<EntityPlayer> getPlayersWithContainerOpen(TileEntity container,
            ItemStack itemToSearchFor)
    {
        int i = container.getPos().getX();
        int j = container.getPos().getY();
        int k = container.getPos().getZ();

        NonNullList<EntityPlayer> users = NonNullList.create();

        IInventory icontainer = null;
        if (container instanceof IInventory)
        {
            icontainer = (IInventory) container;
        }

        for (EntityPlayer player : container.getWorld().getEntitiesWithinAABB(EntityPlayer.class,
                new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F), (double) ((float) k - 5.0F),
                        (double) ((float) (i + 1) + 5.0F), (double) ((float) (j + 1) + 5.0F),
                        (double) ((float) (k + 1) + 5.0F))))
        {
            boolean add = false;

            if (player.openContainer instanceof ContainerChest)
            {
                IInventory iinventory = ((ContainerChest) player.openContainer).getLowerChestInventory();

                if (iinventory == container) // || (iinventory instanceof InventoryLargeChest &&
                                             // ((InventoryLargeChest)iinventory).isPartOfLargeChest(container))
                {
                    add = true;
                }

                if (!add && (icontainer != null) && (iinventory instanceof InventoryLargeChest)
                        && ((InventoryLargeChest) iinventory).isPartOfLargeChest(icontainer))
                {
                    add = true;
                }
            }

            // horrid hack - if container contains item we know about, it is the container
            // we are looking for
            if (!add && (player.openContainer != null) && (itemToSearchFor != null) && (!itemToSearchFor.isEmpty()))
            {
                NonNullList<ItemStack> stack = player.openContainer.getInventory();

                if (stack.contains(itemToSearchFor))
                {
                    add = true;
                }
            }

            // if player.openContainer.listeners

            if (add)
            {
                users.add(player);
            }
        }

        return users;
    }

}
