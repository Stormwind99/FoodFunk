package com.wumple.foodfunk.capability.preserving;

import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.EntityThing;
import com.wumple.util.adapter.IThing;
import com.wumple.util.adapter.TileEntityThing;
import com.wumple.util.container.ContainerUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

public interface IPreserving
{
    /**
     * Get the timestamp of the last check of this preserving cap owner's items
     */
    long getLastCheckTime();

    /**
     * Set the timestamp of the last check of this preserving cap owner's items
     */
    void setLastCheckTime(long time);

    /**
     * Set the owner of this capability, and init based on that owner
     */
    void setOwner(IPreservingOwner ownerIn);

    /**
     * Automatically adjust the use-by date on food items stored within to slow or stop rot
     */
    void freshenContents();
    
    /**
     * @return the preserving ratio (100 means no rot, 50 means half speed rot)
     */
    int getRatio();
    
    public static interface IPreservingOwner extends IThing
    {
        // Preserving specific
        public NonNullList<EntityPlayer> getPlayersWithContainerOpen(ItemStack itemToSearchFor);
        public Integer getPreservingProperty();
    }
    
    public static class TileEntityPreservingOwner extends TileEntityThing implements IPreservingOwner
    {
    	public TileEntityPreservingOwner(TileEntity ownerIn)
    	{
    		super(ownerIn);
    	}
    	
        public Integer getPreservingProperty()
        {
        	return ConfigHandler.preserving.getProperty(owner);
        }
        
        public NonNullList<EntityPlayer> getPlayersWithContainerOpen(ItemStack itemToSearchFor)
        {
        	return ContainerUtil.getPlayersWithContainerOpen(owner, itemToSearchFor);
        }  
    }
    
    public static class EntityPreservingOwner extends EntityThing implements IPreservingOwner
    {
    	public EntityPreservingOwner(Entity entity)
    	{
    		super(entity);
    	}
    	
        public Integer getPreservingProperty()
        {
        	return ConfigHandler.preserving.getProperty(owner);
        }
        
        public NonNullList<EntityPlayer> getPlayersWithContainerOpen(ItemStack itemToSearchFor)
        {
        	return ContainerUtil.getPlayersWithContainerOpen(owner, itemToSearchFor);
        }
    }

}
