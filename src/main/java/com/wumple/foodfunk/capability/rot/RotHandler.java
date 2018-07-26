package com.wumple.foodfunk.capability.rot;

import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.capability.eventtimed.TimerEventHandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RotHandler extends TimerEventHandler<IRot>
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
}
