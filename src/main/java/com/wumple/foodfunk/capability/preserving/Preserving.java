package com.wumple.foodfunk.capability.preserving;

import java.util.List;

import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.preserving.IPreserving.IPreservingOwner;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.RotInfo;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.IThing;
import com.wumple.util.adapter.TUtil;
import com.wumple.util.capability.eventtimed.IEventTimedThingCap;
import com.wumple.util.capability.timerrefreshing.TimerRefreshingCap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;

public class Preserving extends TimerRefreshingCap<IPreservingOwner, RotInfo, IThing> implements IPreserving
{
    // The {@link Capability} instance
    @CapabilityInject(IPreserving.class)
    public static final Capability<IPreserving> CAPABILITY = null;
    public static final EnumFacing DEFAULT_FACING = null;

    // IDs of the capability
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "preserving");

    // ----------------------------------------------------------------------
    // Init

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IPreserving.class, new PreservingStorage(), () -> new Preserving());
    }

    Preserving()
    {
        super();
    }

    Preserving(IPreservingOwner ownerIn)
    {
        this();
        owner = ownerIn;
    }

    // ----------------------------------------------------------------------
    // IPreserving

    @Override
    protected void cache()
    {
        Integer ratio = owner.getPreservingProperty();
        // at this point ratio should not be null - probably a bug, maybe throw exception
        refreshingRatio = (ratio != null) ? ratio.intValue() : ConfigHandler.NO_PRESERVING;
    }
    
    @Override
    protected long getEvaluationInterval()
    {
        return ConfigContainer.evaluationInterval;
    }

    @Override
    protected IEventTimedThingCap<IThing, RotInfo> getTimedCap(ICapabilityProvider provider)
    {
        return IRot.getMyCap(provider);
    }
    
    @Override
    public void doTooltip(ItemStack stack, EntityPlayer entity, boolean advanced, List<String> tips)
    {
        int ratio = getRatio();
        String key = getTemperatureTooltipKey();
        tips.add(new TextComponentTranslation(key, ratio).getUnformattedText());
        super.doTooltip(stack, entity, advanced, tips);
    }
    
    public String getTemperatureTooltipKey()
    {
        return getTemperatureTooltipKey(getRatio());
    }
    
    protected static String getTemperatureTooltipKey(final int ratio)
    {
        String key = null;

        if (ratio == 0)
        {
            key = "misc.foodfunk.tooltip.state.cold0";
        }
        else if ((ratio > 0) && (ratio <= 50))
        {
            key = "misc.foodfunk.tooltip.state.cold1";
        }
        else if ((ratio > 50) && (ratio < 100))
        {
            key = "misc.foodfunk.tooltip.state.cold2";
        }
        else if (ratio == 100)
        {
            key = "misc.foodfunk.tooltip.state.cold3";
        }
        else if (ratio > 100)
        {
            key = "misc.foodfunk.tooltip.state.cold4";
        }
        else if ((ratio < 0) && (ratio >= -50))
        {
            key = "misc.foodfunk.tooltip.state.warm1";
        }
        else if ((ratio < -50) && (ratio > -100))
        {
            key = "misc.foodfunk.tooltip.state.warm2";
        }
        else if (ratio <= -100)
        {
            key = "misc.foodfunk.tooltip.state.warm3";
        }

        return key;
    }
    
    // ----------------------------------------------------------------------
    // Internal

    @Override
    protected boolean rescheduleAndCheck(IEventTimedThingCap<IThing,RotInfo> cap, int index, IItemHandler itemhandler, ItemStack stack, long time)
    {
        assert (cap != null);
        
        cap.reschedule(time);
        
        // we're here, might as well see if reschedule caused expiration
        cap.evaluate(owner.getWorld(), index, itemhandler, (IThing)TUtil.to(stack));
        
        return true;
    }
}
