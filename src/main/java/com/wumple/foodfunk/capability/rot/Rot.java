package com.wumple.foodfunk.capability.rot;

import java.util.List;

import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.ContainerListenerRot;
import com.wumple.foodfunk.capability.preserving.IPreserving;
import com.wumple.foodfunk.capability.preserving.Preserving;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.ModConfig;
import com.wumple.util.WumpleUtil;
import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.eventtimed.EventTimedThingCap;
import com.wumple.util.capability.eventtimed.IEventTimedThingCap;
import com.wumple.util.container.capabilitylistener.CapabilityContainerListenerManager;
import com.wumple.util.container.misc.ContainerUseTracker;
import com.wumple.util.misc.CraftingUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class Rot extends EventTimedThingCap<IThing, RotInfo> implements IRot
{
    // The {@link Capability} instance
    @CapabilityInject(IRot.class)
    public static final Capability<IRot> CAPABILITY = null;
    public static final EnumFacing DEFAULT_FACING = null;

    // IDs of the capability
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "rot");

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IRot.class, new RotStorage(), () -> new Rot());

        CapabilityContainerListenerManager.registerListenerFactory(ContainerListenerRot::new);
    }

    public Rot()
    {
        super();
    }

    public Rot(Rot other)
    {
        super(other);
        info = other.info;
    }
    
    @Override
    public RotInfo newT()
    {
        return new RotInfo();
    }

    // ----------------------------------------------------------------------
    // Functionality

    @Override
    public IThing expired(World world, IThing thing)
    {
        RotProperty rotProps = ConfigHandler.rotting.getRotProperty(thing);
        // forget owner to eliminate dependency
        owner = null;
        return (rotProps != null) ? rotProps.forceRot(thing) : null;
    }

    @Override
    public boolean isEnabled()
    {
        return ConfigContainer.enabled;
    }
    
    @Override
    public boolean isDebugging()
    {
        return ConfigContainer.zdebugging.debug;
    }

    /*
     * Build tooltip info based on this rot
     */
    @Override
    public void doTooltip(ItemStack stack, EntityPlayer entity, boolean advanced, List<String> tips)
    {
        boolean usableStack = (stack != null) && (!stack.isEmpty());
        
        if (isEnabled() && (entity != null))
        {
            if (info != null)
            {
                World world = entity.getEntityWorld();
                        
                // if not initialized, set with reasonable guess to be overwritten by server update
                checkInitialized(world);
                
                // preserving container state aka fake temperature - ambient, chilled, cold, frozen
                if (info.isSet())
                {
                    if (usableStack && (entity.openContainer != null))
                    {
                        IPreserving cap = getPreservingContainer(entity, stack);
                        if (cap != null)
                        {
                            cap.doTooltipAddon(stack, entity, advanced, tips);
                        }
                    }
                }

                // Rot state
                boolean beingCrafted = (stack != null) ? CraftingUtil.isItemBeingCraftedBy(stack, entity) : false;
                String key = getStateTooltipKey(info, beingCrafted);

                if (key != null)
                {
                    tips.add(new TextComponentTranslation(key, info.getPercent() + "%", info.getDaysLeft(),
                            info.getDaysTotal()).getUnformattedText());
                }

                // advanced tooltip debug info
                if (advanced && isDebugging())
                {
                    tips.add(new TextComponentTranslation("misc.foodfunk.tooltip.advanced.datetime", info.getDate(),
                            info.getTime()).getUnformattedText());
                    tips.add(new TextComponentTranslation("misc.foodfunk.tooltip.advanced.expire", info.getCurTime(),
                            info.getExpirationTimestamp()).getUnformattedText());

                    int dimension = world.provider.getDimension();
                    int dimensionRatio = info.getDimensionRatio(world);
                    tips.add(new TextComponentTranslation("misc.foodfunk.tooltip.advanced.dimratio", dimensionRatio, dimension).getUnformattedText());
                }
            }
        }
    }
    
    // ----------------------------------------------------------------------
    // Internal

    @Override
    public IRot getCap(ICapabilityProvider thing)
    {
        return IRot.getMyCap(thing);
    }
    
    // only good on client side
    IPreserving getPreservingContainer(EntityPlayer entity, ItemStack stack)
    {
        return ContainerUseTracker.getContainerCapability(entity, stack, Preserving.CAPABILITY, Preserving.DEFAULT_FACING);
    }

    @Override
    public String getStateTooltipKey(RotInfo local, boolean beingCrafted)
    {
        String key = null;

        if (local.isNonExpiring())
        {
            key = "misc.foodfunk.tooltip.preserved";
        }
        else if (local.isSet() && !beingCrafted)
        {
            if (local.getPercent() >= 100)
            {
                key = "misc.foodfunk.tooltip.decaying";
            }
            else
            {
                key = "misc.foodfunk.tooltip.rot";
            }
        }        
        else if (local.time > 0)
        {
            key = "misc.foodfunk.tooltip.fresh";
        }

        return key;
    }
    
    @Override
    public void forceUpdate()
    {
        ItemStack stack = owner.as(ItemStack.class);
        if (stack != null)
        {
            NBTTagCompound tag = stack.getOrCreateSubCompound("Rot");
            RotStorage storage = new RotStorage();
            NBTTagCompound nbt = (NBTTagCompound)storage.writeNBT(Rot.CAPABILITY, this, null);
            tag.merge(nbt);
        }
        
        //int count = stack.getCount();
        //ItemStack newStack = itemhandler.extractItem(index, count, false);
        //itemhandler.insertItem(index, newStack, false);

        super.forceUpdate();
    }

    // ----------------------------------------------------------------------
    // Possible future

    /*
     * public static boolean isInTheCold(ItemStack stack) { // TODO walk up container tree, if any is cold chest then true. If none, get world pos of topmost container. // TODO
     * Check isOnItemFrame // TODO if temperature mod, get temp from it // TODO ToughAsNails:TemperatureHelper.getTargetAtPosUnclamped() ?
     * https://github.com/Glitchfiend/ToughAsNails // TODO if no temperature mod, check biome for temp?
     * 
     * return false; }
     */
    
    // debug
    
    @Override
    public void copyFrom(IEventTimedThingCap<IThing, RotInfo> other)
    {
        // Avoid cheating from crafting or break rotting items
        
        // For example: 
        // Melon 0/14 days -> Slices 0/7 days -> Melon 0/7 days or 7/14 days
        // Melon 8/14 days -> Slices 6/7 days -> Melon 6/7 days or 13/14 days
        // Crafting: Ingredients 1/7, 2/7, 3/7 days -> Results 3/7 days
        // Crafting: Ingredients 1/7, 2/7, 3/7 days -> Results 10/14 days
        // Crafting: Ingredients 5/14, 6/14 -> Results 0/7 days
        // Crafting: Ingredients 6/14, 9/14 -> Results 2/7 days
        
        if (ModConfig.zdebugging.debug) { WumpleUtil.logger.info("copyFrom: other " + other + " this " + this); }
        
        if (!this.isExpirationTimestampSet())
        {
            if (other.isExpirationTimestampSet())
            {
                if (ModConfig.zdebugging.debug) { WumpleUtil.logger.info("copyFrom: uninit this, copying " + other.getDate() + " " + other.getTime()); }
                setExpiration(other.getDate(), other.getTime());
                forceUpdate();               
            }
            else
            {
                // handle uninitialized src or dest
                // should never happen - but for now just skip this operation
                if (ModConfig.zdebugging.debug) { WumpleUtil.logger.info("copyFrom: skipping uninit other " + other.isExpirationTimestampSet() + " this " + this.isExpirationTimestampSet()); }
                return;  
            }
        }
        
        // handle dimension-related state:
        // if other.nonExpiring && info.nonExpiring, do nothing
        // if other.nonExpiring && !info.nonExpiring, do nothing
        // if !other.nonExp && info.nonExpiring, do nothing
        // if !other.nonExp && !info.nonExpiring, do below
        
        if (!this.isNonExpiring())
        {
            long d_o = other.getDate();
            long t_o = other.getTime();
            long e_o = d_o + t_o; // aka other.getExpirationTimestamp();
            long d_i = this.getDate();
            long t_i = this.getTime();
            long e_i = d_i + t_i; // aka info.getExpirationTimestamp();

            long new_d_i = d_i;
            if (e_i > e_o)
            {
                // clamp dest expiration timestamp at src expiration timestamp by moving destination date backwards
                new_d_i = e_o - t_i;
            }
            
            if (ModConfig.zdebugging.debug) { WumpleUtil.logger.info("copyFrom: setting"
                    + " new_d_i " + new_d_i
                    + " d_o " + d_o
                    + " t_o " + t_o
                    + " e_o " + e_o
                    + " d_i " + d_i
                    + " t_i " + t_i
                    + " e_i " + e_i
                    ); }
            
            setExpiration(new_d_i, t_i);
            forceUpdate();
        }
        else
        {
            if (ModConfig.zdebugging.debug) { WumpleUtil.logger.info("copyFrom: skipping this isNotExpiring"); }
        }
    }

}
