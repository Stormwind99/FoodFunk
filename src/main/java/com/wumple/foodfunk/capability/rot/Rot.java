package com.wumple.foodfunk.capability.rot;

import java.util.List;

import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.ContainerListenerRot;
import com.wumple.foodfunk.capability.preserving.IPreserving;
import com.wumple.foodfunk.capability.preserving.Preserving;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.adapter.IThing;
import com.wumple.util.capability.eventtimed.EventTimedThingCap;
import com.wumple.util.capability.listener.CapabilityContainerListenerManager;
import com.wumple.util.container.ContainerUseTracker;
import com.wumple.util.crafting.CraftingUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class Rot extends EventTimedThingCap<IThing, RotInfo> implements IRot
{
    // The {@link Capability} instance
    @CapabilityInject(IRot.class)
    public static final Capability<IRot> CAPABILITY = null;
    public static final Direction DEFAULT_FACING = null;

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
        return ConfigHandler.isEnabled();
    }
    
    @Override
    public boolean isDebugging()
    {
        return ConfigHandler.isDebugging();
    }

    /*
     * Build tooltip info based on this rot
     */
    @Override
    public void doTooltip(ItemStack stack, PlayerEntity entity, boolean advanced, List<ITextComponent> tips)
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
                        LazyOptional<IPreserving> lcap = getPreservingContainer(entity, stack);
                        lcap.ifPresent(cap->
                        {
                            cap.doTooltipAddon(stack, entity, advanced, tips);
                        });
                    }
                }

                // Rot state
                boolean beingCrafted = (stack != null) ? CraftingUtil.isItemBeingCraftedBy(stack, entity) : false;
                String key = getStateTooltipKey(info, beingCrafted);

                if (key != null)
                {
                    tips.add(new TranslationTextComponent(key, info.getPercent() + "%", info.getDaysLeft(),
                            info.getDaysTotal()));
                }

                // advanced tooltip debug info
                if (advanced && isDebugging())
                {
                    tips.add(new TranslationTextComponent("misc.foodfunk.tooltip.advanced.datetime", info.getDate(),
                            info.getTime()));
                    tips.add(new TranslationTextComponent("misc.foodfunk.tooltip.advanced.expire", info.getCurTime(),
                            info.getExpirationTimestamp()));

                    int dimension = world.getDimension().getType().getId();
                    int dimensionRatio = info.getDimensionRatio(world);
                    tips.add(new TranslationTextComponent("misc.foodfunk.tooltip.advanced.dimratio", dimensionRatio, dimension));
                }
            }
        }
    }
    
    // ----------------------------------------------------------------------
    // Internal

    @Override
    public LazyOptional<? extends IRot> getCap(ICapabilityProvider thing)
    {
        return IRot.getMyCap(thing);
    }
    
    // only good on client side
    LazyOptional<IPreserving> getPreservingContainer(PlayerEntity entity, ItemStack stack)
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
        ItemStack stack = (owner != null) ? owner.as(ItemStack.class) : null;
        if (stack != null)
        {
        	// Attempted workaround for #56 even tho Rot.CAPABILITY should always be non-null 
        	if (Rot.CAPABILITY != null)
        	{
        		CompoundNBT tag = stack.getOrCreateChildTag("Rot");
        		RotStorage storage = new RotStorage();
        		CompoundNBT nbt = (CompoundNBT)storage.writeNBT(Rot.CAPABILITY, this, null);
        		// Attempted workaround for #56 even tho nbt should never be null or empty
        		if ((nbt != null) && !nbt.isEmpty())
        		{
        			tag.merge(nbt);
        		}
        	}
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
}
