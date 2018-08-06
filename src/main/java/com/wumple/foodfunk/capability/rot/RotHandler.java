package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.IThing;
import com.wumple.util.adapter.TUtil;
import com.wumple.util.capability.eventtimed.ThingTimerEventHandler;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

@Mod.EventBusSubscriber
public class RotHandler extends ThingTimerEventHandler<IThing, IRot> implements RotCapCopier
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
        checkAttachCapability(event, TUtil.to(event.getObject()) );
    }
        
    @SubscribeEvent
    public static void attachCapabilities2(AttachCapabilitiesEvent<TileEntity> event)
    {
        checkAttachCapability(event, TUtil.to(event.getObject()) );
    }
    
    @SubscribeEvent
    public static void attachCapabilities3(AttachCapabilitiesEvent<Entity> event)
    {
        checkAttachCapability(event, TUtil.to(event.getObject()) );
    }
    
    static public void checkAttachCapability(AttachCapabilitiesEvent<?> event, IThing thing)
    {
        if (ConfigHandler.rotting.doesRot(thing))
        {
            event.addCapability(Rot.ID, RotProvider.createProvider(thing));
        }
    }

    // horrible hack for lack of tileentity in some block related events
    protected TileEntity lastTileEntity = null;

    public TileEntity setLastTileEntity(TileEntity other)
    {
        TileEntity old = lastTileEntity;
        lastTileEntity = other;
        return old;
    }
    
    @Override
    public ItemStack check(World world, ItemStack stack)
    {
        return evaluateTimer(world, stack);
    }
    
    @Override
    public IRot getCap(ICapabilityProvider provider)
    {
        return IRot.getRot(provider);
    }
    
    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event)
    { RotCapCopier.super.onHarvest(event); }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event)
    { RotCapCopier.super.onBreak(event); }
    
    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.PlaceEvent event)
    {
        super.onPlaceBlock(event);
        RotCapCopier.super.onPlaceBlock(event);
    }
    
    public void log(String msg)
    {
        if (ConfigContainer.zdebugging.debug)
        {
            FoodFunk.logger.info(msg);
        }
    }
    
    /**
     *  Prevents exploit of making foods with almost rotten food to prolong total life of food supplies
     */
    @SubscribeEvent
    public void onCrafted(ItemCraftedEvent event) 
    {
        log("RotHandler.onCrafted begin on " + event.crafting);
        if (!isEnabled() || event.player.world.isRemote || event.crafting == null
                || event.crafting.isEmpty() || event.crafting.getItem() == null)
        {
            log("RotHandler.onCrafted skipped, remote " + event.player.world.isRemote);
            return;
        }
        
        ItemStack crafting = event.crafting;
        IInventory craftMatrix = event.craftMatrix;
        World world = event.player.world;

        IRot ccap = getCap(crafting);

        if (ccap != null)
        {
            log("RotHandler.onCrafted handleCraftedTimers");
            ccap.handleCraftedTimers(world, craftMatrix, crafting);
        } // else crafted item doesn't rot
        
        log("RotHandler.onCrafted end on " + crafting);
    }
}
