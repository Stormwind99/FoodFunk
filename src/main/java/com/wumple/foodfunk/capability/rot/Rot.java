package com.wumple.foodfunk.capability.rot;

import java.util.List;
import java.util.Random;

import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.ContainerListenerRot;
import com.wumple.foodfunk.capability.preserving.IPreserving;
import com.wumple.foodfunk.capability.preserving.Preserving;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.capability.CapabilityContainerListenerManager;
import com.wumple.util.container.ContainerUseTracker;
import com.wumple.util.misc.CraftingUtil;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Rot implements IRot
{
    // The {@link Capability} instance
    @CapabilityInject(IRot.class)
    public static final Capability<IRot> CAPABILITY = null;
    public static final EnumFacing DEFAULT_FACING = null;

    // IDs of the capability
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "rot");

    protected static Random random = new Random();

    // RotInfo holds the rot data (composition due to cap network serialization classes)
    protected RotInfo info = new RotInfo();
    // what itemstack is this cap attached to?
    ItemStack owner = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(IRot.class, new RotStorage(), () -> new Rot());

        CapabilityContainerListenerManager.registerListenerFactory(ContainerListenerRot::new);
    }

    public Rot()
    {

    }

    public Rot(Rot other)
    {
        info = other.info;
    }

    @Override
    public long getDate()
    {
        return info.getDate();
    }

    @Override
    public long getTime()
    {
        return info.getTime();
    }

    @Override
    public void setDate(long dateIn)
    {
        info.setDate(dateIn);
    }

    @Override
    public void setTime(long timeIn)
    {
        info.setTime(timeIn);
    }

    @Override
    public void setRot(long dateIn, long timeIn)
    {
        info.set(dateIn, timeIn);
    }

    @Override
    public void reschedule(long timeIn)
    {
        info.reschedule(timeIn);
        forceUpdate();
    }

    public void forceUpdate()
    {
        // HACK to force Container.detectAndSendChanges to detect change and notify ContainerListener
        NBTTagCompound tag = owner.getOrCreateSubCompound("Rot");
        info.writeToNBT(tag);
    }

    public RotInfo setInfo(RotInfo infoIn)
    {
        info = infoIn;
        return info;
    }

    public RotInfo getInfo()
    {
        return info;
    }

    /*
     * Set the owner of this capability, and init based on that owner
     */
    public void setOwner(ItemStack ownerIn)
    {
        if (ownerIn != owner)
        {
            owner = ownerIn;

            // on server, setting default waits until later so a World will be present
            // on client, tooltip will init with reasonable guess until update is received from server
        }
    }

    public ItemStack getOwner()
    {
        return owner;
    }

    // ----------------------------------------------------------------------
    // Functionality

    /*
     * Evaluate this rot, which belongs to stack
     */
    public ItemStack evaluateRot(World world, ItemStack stack)
    {
        // TODO integrate with Serene Seasons temperature system
        // If stack in a cold/frozen location, change rot appropriately (as if esky or
        // freezer)
        // Might allow building a walk-in dofreezer like in RimWorld

        if (!info.checkInitialized(world, stack))
        {
            forceUpdate();
        }

        if (!info.isNoRot())
        {
            if (info.hasExpired())
            {
                RotProperty rotProps = ConfigHandler.rotting.getRotProperty(stack);
                // forget owner to eliminate dependency
                owner = null;
                return (rotProps != null) ? rotProps.forceRot(stack) : null;
            }
        }

        return stack;
    }

    /*
     * Build tooltip info based on this rot
     */
    public void doTooltip(ItemStack stack, EntityPlayer entity, boolean advanced, List<String> tips)
    {
        if (ConfigContainer.enabled && (stack != null) && !stack.isEmpty() && (entity != null))
        {
            if (info != null)
            {
                World world = entity.getEntityWorld();
                        
                // if not initialized, set with reasonable guess to be overwritten by server update
                info.checkInitialized(world, owner);
                
                // preserving container state aka fake temperature - ambient, chilled, cold, frozen
                if (info.isSet())
                {
                    if (entity.openContainer != null)
                    {
                        IPreserving cap = getPreservingContainer(entity, stack);
                        if (cap != null)
                        {
                            int ratio = cap.getRatio();
                            String key = getTemperatureTooltipKey(ratio);
                            tips.add(new TextComponentTranslation(key, ratio).getUnformattedText());
                        }
                    }
                }

                // Rot state
                boolean beingCrafted = CraftingUtil.isItemBeingCraftedBy(stack, entity);
                String key = getRotStateTooltipKey(info, beingCrafted);

                if (key != null)
                {
                    tips.add(new TextComponentTranslation(key, info.getPercent() + "%", info.getDaysLeft(),
                            info.getDaysTotal()).getUnformattedText());
                }

                // advanced tooltip debug info
                if (advanced && ConfigContainer.zdebugging.debug)
                {
                    tips.add(new TextComponentTranslation("misc.foodfunk.tooltip.advanced.datetime", info.getDate(),
                            info.getTime()).getUnformattedText());
                    tips.add(new TextComponentTranslation("misc.foodfunk.tooltip.advanced.expire", info.getCurTime(),
                            info.getExpirationTimestamp()).getUnformattedText());

                    int dimension = world.provider.getDimension();
                    int dimensionRatio = RotInfo.getDimensionRatio(world);
                    tips.add(new TextComponentTranslation("misc.foodfunk.tooltip.advanced.dimratio", dimensionRatio, dimension).getUnformattedText());
                }
            }
        }
    }

    /*
     * Set rot on crafted items dependent on the ingredients
     */
    public void handleCraftedRot(World world, IInventory craftMatrix, ItemStack crafting)
    {
        long lowestDate = world.getTotalWorldTime();

        int slots = craftMatrix.getSizeInventory();
        for (int i = 0; i < slots; i++)
        {
            ItemStack stack = craftMatrix.getStackInSlot(i);

            if (stack == null || stack.isEmpty() || stack.getItem() == null)
            {
                continue;
            }

            IRot cap = RotCapHelper.getRot(stack);

            if ((cap != null) && (cap.getDate() < lowestDate))
            {
                lowestDate = cap.getDate();
            }
        }

        info.setDateSafe(lowestDate);
    }
    
    public void ratioShift(int fromRatio, int toRatio)
    {
        info.ratioShift(fromRatio, toRatio, owner);
        forceUpdate();
    }

    // ----------------------------------------------------------------------
    // Internal

    // only good on client side
    IPreserving getPreservingContainer(EntityPlayer entity, ItemStack stack)
    {
        return ContainerUseTracker.getContainerCapability(entity, stack, Preserving.CAPABILITY, Preserving.DEFAULT_FACING);
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
        else if (ratio >= 100)
        {
            key = "misc.foodfunk.tooltip.state.cold3";
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

    protected String getRotStateTooltipKey(RotInfo local, boolean beingCrafted)
    {
        String key = null;

        if (local.isNoRot())
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
