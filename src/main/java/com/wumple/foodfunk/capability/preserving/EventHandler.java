package com.wumple.foodfunk.capability.preserving;

import com.wumple.foodfunk.configuration.ConfigHandler;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler
{
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
            PreservingProvider provider = new PreservingProvider(Preserving.CAPABILITY, Preserving.DEFAULT_FACING, entity);
            event.addCapability(Preserving.ID, provider);
        }
    }
    
    /*
    @SubscribeEvent
    public static void attachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
        Entity entity = event.getObject();

        if (ConfigHandler.preserving.doesIt(entity))
        {
            PreservingProvider provider = new PreservingProvider(Preserving.CAPABILITY, Preserving.DEFAULT_FACING, entity);
            event.addCapability(Preserving.ID, provider);
        }
    }
    */
}
