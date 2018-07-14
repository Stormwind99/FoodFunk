package com.wumple.util.container;

import java.util.ArrayList;
import java.util.List;

import com.wumple.util.Util;
import com.wumple.util.adapter.EntityThing;
import com.wumple.util.adapter.IThing;
import com.wumple.util.adapter.TileEntityThing;
import com.wumple.util.capability.CapabilityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerUtil
{
    // horrid hack - if container contains item we know about, it is the container we are looking for
    public static boolean isPlayerWithContainerOpenBase(EntityPlayer player, TileEntity container, IInventory icontainer, ItemStack itemToSearchFor)
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

        // horrid hack - if container contains item we know about, it is the container we are looking for
        if (!add && (player.openContainer != null) && (itemToSearchFor != null) && (!itemToSearchFor.isEmpty()))
        {
            NonNullList<ItemStack> stack = player.openContainer.getInventory();

            if (stack.contains(itemToSearchFor))
            {
                add = true;
            }
        }
        
        // MAYBE if player.openContainer.listeners if it wasn't private (use AT)?
        
        return add;
    }
    
    // horrid hack - if container contains item we know about, it is the container we are looking for
    public static boolean isPlayerWithContainerOpen(EntityPlayer player, TileEntity container, ItemStack itemToSearchFor)
    {
        IInventory icontainer = null;
        if (container instanceof IInventory)
        {
            icontainer = (IInventory) container;
        }

        return isPlayerWithContainerOpenBase(player, container, icontainer, itemToSearchFor);
    }
    
    protected static <T extends Entity> List<T> getEntitiesNearby(Class <? extends T > classEntity, BlockPos pos, World world)
    {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        
        AxisAlignedBB box = new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F), (double) ((float) k - 5.0F),
                (double) ((float) (i + 1) + 5.0F), (double) ((float) (j + 1) + 5.0F),
                (double) ((float) (k + 1) + 5.0F));

        return world.getEntitiesWithinAABB(classEntity, box);
    }
    
    // horrible hack - find players with container open, by searching nearby and for
    // a known item in the container
    public static NonNullList<EntityPlayer> getPlayersWithContainerOpen(TileEntity container, ItemStack itemToSearchFor)
    {
        NonNullList<EntityPlayer> users = NonNullList.create();

        IInventory icontainer = null;
        if (container instanceof IInventory)
        {
            icontainer = (IInventory) container;
        }

        for (EntityPlayer player : getEntitiesNearby(EntityPlayer.class, container.getPos(), container.getWorld()))
        {
            if (isPlayerWithContainerOpenBase(player, container, icontainer, itemToSearchFor))
            {
                users.add(player);
            }
        }

        return users;
    }

    // horrible hack - find players with container open, by searching nearby and for
    // a known item in the container
    public static NonNullList<EntityPlayer> getPlayersWithContainerOpen(Entity container, ItemStack itemToSearchFor)
    {
        NonNullList<EntityPlayer> users = NonNullList.create();

        IInventory icontainer = null;
        if (container instanceof IInventory)
        {
            icontainer = (IInventory) container;
        }

        for (EntityPlayer player : getEntitiesNearby(EntityPlayer.class, container.getPosition(), container.world))
        {
            if (isPlayerWithContainerOpenBase(player, null, icontainer, itemToSearchFor))
            {
                users.add(player);
            }
        }

        return users;
    }

    // horrible hack - get the TileEntity corresponding to container
    public static List<TileEntity> getTileEntitiesNearby(BlockPos position, World world)
    {        
        // idea #1
        // for each tileentity in range of player
        //   if supports IItemHandler, ask if has item.  If so, done.
        //   if supports IInventory, ask if has item.  If so, done.
        
        int i = position.getX();
        int j = position.getY();
        int k = position.getZ();
        
        final int x1 = (int) ((float) i - 5.0F);
        final int y1 = (int) ((float) j - 5.0F);
        final int z1 = (int) ((float) k - 5.0F);
        final int x2 = (int) ((float) (i + 1) + 5.0F);
        final int y2 = (int) ((float) (j + 1) + 5.0F);
        final int z2 = (int) ((float) (k + 1) + 5.0F);
        
        ArrayList<TileEntity> list = new ArrayList<TileEntity>();
        
        // iterate over all tileentities nearby
        for (BlockPos pos : BlockPos.getAllInBoxMutable(x1, y1, z1, x2, y2, z2))
        {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity != null)
            {
            	list.add(tileentity);
            }
        }
        
        return list;
    }
    
    // horrible hack - get the TileEntity corresponding to container
    public static TileEntity getTileEntityForContainer(Container container, ItemStack itemToSearchFor, BlockPos position, World world)
    {        
        // idea #1
        // for each tileentity in range of player
        //   if supports IItemHandler, ask if has item.  If so, done.
        //   if supports IInventory, ask if has item.  If so, done.
        
        // iterate over all tileentities nearby
        for (TileEntity tileentity : getTileEntitiesNearby(position, world))
        {
            if (doesContain(tileentity, itemToSearchFor))
            {
                return tileentity;
            }
        }
        
        return null;
    }

    static public boolean doesContain(IItemHandler capability, ItemStack itemToSearchFor)
    {
        // check TileEntity's IItemHandler capability, if provided
        if (capability != null)
        {
            for (int slot = 0; slot < capability.getSlots(); ++slot)
            {
                if (capability.getStackInSlot(slot) == itemToSearchFor)
                {
                    return true;
                }
            }
        }

        return false;
    }
    
    static public boolean doesContain(IInventory iinventory, ItemStack itemToSearchFor)
    {
    	if (iinventory != null)
    	{
    		for (int slot = 0; slot < iinventory.getSizeInventory(); ++slot)
    		{
    			if (iinventory.getStackInSlot(slot) == itemToSearchFor)
    			{
    				return true;
    			}
    		}
    	}
    
    	return false;
    }
    
    /*
     * Does the tileentity (via its IItemHandler cap or IInventory interface) contain itemToSearchFor?
     */
    static public boolean doesContain(TileEntity tileentity, ItemStack itemToSearchFor)
    {
        // check TileEntity's IItemHandler capability, if provided
        IItemHandler capability = CapabilityUtils.getCapability(tileentity, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        // check TileEntity's IInventory interface, if provided
        IInventory iinventory = Util.as(tileentity, IInventory.class);
        
        return doesContain(capability, itemToSearchFor) ? true : doesContain(iinventory, itemToSearchFor);
    }
    
    /*
     * Does the tileentity (via its IItemHandler cap or IInventory interface) contain itemToSearchFor?
     */
    static public boolean doesContain(Entity entity, ItemStack itemToSearchFor)
    {
        // check TileEntity's IItemHandler capability, if provided
        IItemHandler capability = CapabilityUtils.getCapability(entity, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        // check TileEntity's IInventory interface, if provided
        IInventory iinventory = Util.as(entity, IInventory.class);
        
        return doesContain(capability, itemToSearchFor) ? true : doesContain(iinventory, itemToSearchFor);
    }
    
    // horrible hack - find the thing containing stack
    static public IThing getContainedBy(ItemStack stack, Entity entityHint, TileEntity tileEntityHint)
    {
    	if (doesContain(entityHint, stack))
    	{
    		return new EntityThing(entityHint);
    	}
    	
    	if (doesContain(tileEntityHint, stack))
    	{
    		return new TileEntityThing(tileEntityHint);
    	}
    	
    	BlockPos posGuess = null;
    	World worldGuess = null;
    	if (entityHint != null)
    	{
    		posGuess = entityHint.getPosition();
    		worldGuess = entityHint.getEntityWorld();
    	}
    	else if (tileEntityHint != null)
    	{
    		posGuess = tileEntityHint.getPos();
    		worldGuess = tileEntityHint.getWorld();
    	}
    	/*
    	else
    	{
    		posGuess = FoodFunk.proxy.getClientPlayer().getPos();
    	}
    	*/
    	
    	// TODO open TileEntity container
    	// TODO open Entity container
    	
    	if ((posGuess == null) || (worldGuess == null))
    	{
    		return null;
    	}
    	
    	// other Entities nearby
        for (Entity entity : getEntitiesNearby(Entity.class, posGuess, worldGuess))
        {
        	if (doesContain(entity, stack))
        	{
        		return new EntityThing(entity);
        	}
        }

    	// other TileEntities nearby
        for (TileEntity entity : getTileEntitiesNearby(posGuess, worldGuess))
        {
        	if (doesContain(entity, stack))
        	{
        		return new TileEntityThing(entity);
        	}
        }
        
    	return null;
    }
}