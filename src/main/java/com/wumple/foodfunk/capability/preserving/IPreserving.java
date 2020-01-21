package com.wumple.foodfunk.capability.preserving;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capability.preserving.IPreserving.IPreservingOwner;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.RotInfo;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.EntityThing;
import com.wumple.util.adapter.IThing;
import com.wumple.util.adapter.ItemStackThing;
import com.wumple.util.adapter.TileEntityThing;
import com.wumple.util.capability.CapabilityUtils;
import com.wumple.util.capability.timerrefreshing.ITimerRefreshingCap;
import com.wumple.util.container.ContainedByUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public interface IPreserving extends ITimerRefreshingCap<IPreservingOwner, RotInfo>
{
    /**
     * Get the {@link IRot} from the specified provider's capabilities, if any.
     *
     * @param provider
     * @return The IRot, or null if there isn't one
     */
    @Nullable
    static LazyOptional<IPreserving> getMyCap(@Nullable ICapabilityProvider provider)
    {
        return CapabilityUtils.fetchCapability(provider, Preserving.CAPABILITY, Preserving.DEFAULT_FACING);
    }
    
    public static interface IPreservingOwner extends IThing
    {
        // Preserving specific
        public NonNullList<PlayerEntity> getPlayersWithContainerOpen(ItemStack itemToSearchFor);
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
        
        public NonNullList<PlayerEntity> getPlayersWithContainerOpen(ItemStack itemToSearchFor)
        {
        	return null; // PORT ContainerUtil.getPlayersWithContainerOpen(owner, itemToSearchFor);
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
        
        public NonNullList<PlayerEntity> getPlayersWithContainerOpen(ItemStack itemToSearchFor)
        {
        	return ContainedByUtil.getPlayersWithContainerOpen(owner, itemToSearchFor);
        }
    }
    
    public static class ItemStackPreservingOwner extends ItemStackThing implements IPreservingOwner
    {
        public ItemStackPreservingOwner(ItemStack it)
        {
            super(it);
        }
        
        public Integer getPreservingProperty()
        {
            return ConfigHandler.preserving.getProperty(owner);
        }
        
        public NonNullList<PlayerEntity> getPlayersWithContainerOpen(ItemStack itemToSearchFor)
        {
            return null; 
        }
    }
}
