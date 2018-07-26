package com.wumple.foodfunk.capability.preserving;

import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.preserving.IPreserving.IPreservingOwner;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.capability.rot.RotInfo;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.capability.eventtimed.IEventTimedItemStackCap;
import com.wumple.util.capability.timerrefreshing.TimerRefreshingCap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Preserving extends TimerRefreshingCap<IPreservingOwner, RotInfo> implements IPreserving
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
    
    // ----------------------------------------------------------------------
    // Internal

    @Override
    protected IEventTimedItemStackCap<RotInfo> getCap(ItemStack stack)
    {
        return IRot.getRot(stack);
    }
}
