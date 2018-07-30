package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.eventtimed.IEventTimedThingCap;
import com.wumple.util.capability.eventtimed.ThingTimerEventHandler;
import com.wumple.util.tileentity.placeholder.CapCopier;
import com.wumple.util.tileentity.placeholder.TileEntityPlaceholder;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
// ICopyableCap< IEventTimedThingCap<W,T> >
// IEventTimedThingCap<W extends IThing, T extends Expiration>
public class RotHandler extends ThingTimerEventHandler<IThing, IRot> implements CapCopier<IEventTimedThingCap<IThing,RotInfo>>
{
    public static RotHandler INSTANCE = new RotHandler();
    
    public static RotHandler getInstance()
    {
        return INSTANCE;
    }
    
    public RotHandler()
    {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Override
    protected IRot getCap(ItemStack stack)
    {
        return IRot.getRot(stack);
    }

    @Override
    public boolean isEnabled()
    {
        return ConfigContainer.enabled;
    }
    
    @Override
    public int getADimensionRatio(int dim)
    {
        return RotInfo.getADimensionRatio(dim);
    }
    
    @Override
    public boolean isDebugging()
    {
        return ConfigContainer.zdebugging.debug;
    }
    
    @Override
    protected long getEvaluationInterval()
    {
        return ConfigContainer.evaluationInterval;
    }
    
    /**
     * Attach the {@link IRot} capability to vanilla items.
     *
     * @param event
     *            The event
     */
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        if (ConfigHandler.rotting.doesRot(stack))
        {
            event.addCapability(Rot.ID, RotProvider.createProvider(stack));
        }
    }
    
    /// Experiments

    
    @SubscribeEvent
    public static void attachCapabilities2(AttachCapabilitiesEvent<TileEntity> event)
    {
        TileEntity tileentity = event.getObject();
        if (tileentity instanceof TileEntityPlaceholder) // ConfigHandler.rotting.doesRot(tileentity))
        {
            event.addCapability(Rot.ID, RotProvider.createProvider(null));
        }
    }

    TileEntity lastTileEntity = null;

    public TileEntity setLastTileEntity(TileEntity other)
    {
        TileEntity old = lastTileEntity;
        lastTileEntity = other;
        return old;
    }
    
    @Override
    public IRot getCap(ICapabilityProvider provider)
    {
        return IRot.getRot(provider);
    }
    
    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event)
    { CapCopier.super.onHarvest(event); }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event)
    { CapCopier.super.onBreak(event); }
    
    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event)
    { CapCopier.super.onPlace(event); }
}
