package com.wumple.foodfunk.capability.preserving;

import com.wumple.foodfunk.capability.preserving.IPreserving.EntityPreservingOwner;
import com.wumple.foodfunk.capability.preserving.IPreserving.ItemStackPreservingOwner;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.capability.timerrefreshing.TimerRefreshingEventHandler;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PreservingHandler extends TimerRefreshingEventHandler<IPreserving>
{
    public static PreservingHandler INSTANCE = new PreservingHandler();
    
    public static PreservingHandler getInstance()
    {
        return INSTANCE;
    }
    
    public PreservingHandler()
    {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    protected LazyOptional<IPreserving> getCap(ICapabilityProvider stack)
    {
        return IPreserving.getMyCap(stack);
    }

    public boolean isEnabled()
    {
        return ConfigHandler.isEnabled();
    }

    public boolean isDebugging()
    {
        return ConfigHandler.isDebugging();
    }
    
    /**
     * Attach the {@link IPreserving} capability to relevant items.
     *
     * @param event
     *            The event
     */
    @SubscribeEvent
    public static void attachCapabilitiesTileEntity(AttachCapabilitiesEvent<TileEntity> event)
    {
        checkAttachCapability(event, PUtil.to(event.getObject() ));
    }
    
    @SubscribeEvent
    public static void attachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
    	checkAttachCapability(event, PUtil.to(event.getObject() ));
    }
    
    @SubscribeEvent
    public static void attachCapabilitiesItemStack(AttachCapabilitiesEvent<ItemStack> event)
    {
    	checkAttachCapability(event, PUtil.to(event.getObject() ));
    }
    
    static public void checkAttachCapability(AttachCapabilitiesEvent<?> event, IPreserving.IPreservingOwner thing)
    {
        if (ConfigHandler.preserving.doesIt(thing))
        {
        	event.addCapability(Preserving.ID, PreservingProvider.createProvider(thing));
        }
    }

}
