package com.wumple.foodfunk.capability.preserving;

import com.wumple.foodfunk.capability.preserving.IPreserving.EntityPreservingOwner;
import com.wumple.foodfunk.capability.preserving.IPreserving.ItemStackPreservingOwner;
import com.wumple.foodfunk.capability.preserving.IPreserving.TileEntityPreservingOwner;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.capability.timerrefreshing.TimerRefreshingEventHandler;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    protected IPreserving getCap(ICapabilityProvider stack)
    {
        return IPreserving.getMyCap(stack);
    }

    public boolean isEnabled()
    {
        return ConfigContainer.enabled;
    }

    public boolean isDebugging()
    {
        return ConfigContainer.zdebugging.debug;
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
        TileEntity entity = event.getObject();

        if (ConfigHandler.preserving.doesIt(entity))
        {
            PreservingProvider provider = PreservingProvider.createProvider(new TileEntityPreservingOwner(entity));
            event.addCapability(Preserving.ID, provider);
        }
    }
    
    @SubscribeEvent
    public static void attachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
        Entity entity = event.getObject();

        if (ConfigHandler.preserving.doesIt(entity))
        {
            PreservingProvider provider = PreservingProvider.createProvider(new EntityPreservingOwner(entity));
            event.addCapability(Preserving.ID, provider);
        }
    }
    
    @SubscribeEvent
    public static void attachCapabilitiesItemStack(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack it = event.getObject();

        if (ConfigHandler.preserving.doesIt(it))
        {
            PreservingProvider provider = PreservingProvider.createProvider(new ItemStackPreservingOwner(it));
            event.addCapability(Preserving.ID, provider);
        }
    }
}
