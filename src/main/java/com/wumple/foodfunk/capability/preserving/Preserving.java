package com.wumple.foodfunk.capability.preserving;

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

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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
    protected IEventTimedThingCap<IThing, RotInfo> getCap(ICapabilityProvider provider)
    {
        return IRot.getRot(provider);
    }
    
    // ----------------------------------------------------------------------
    // Internal

    /*
    @Override
    protected IRot getCap(ICapabilityProvider stack)
    {
        return IRot.getRot(stack);
    }
    */

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
