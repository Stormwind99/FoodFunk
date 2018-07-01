package com.wumple.foodfunk;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capabilities.rot.IRot;
import com.wumple.foodfunk.capabilities.rot.RotHelper;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;

import choonster.capability.CapabilityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class RotHandler
{
    public static boolean doesPreserve(TileEntity it)
    {
        ResourceLocation loc = (it == null) ? null : TileEntity.getKey(it.getClass());
        String key = (loc == null) ? null : loc.toString();
        boolean preserving = false;

        if ( (key != null) && ConfigContainer.preserving.ratios.containsKey(key) )
        {
            int ratio = ConfigContainer.preserving.ratios.get(key);
            if (ratio != 0)
            {
                preserving = true;
            }
        }

        return preserving;
    }

    public static int getPreservingRatio(TileEntity it)
    {
        ResourceLocation loc = (it == null) ? null : TileEntity.getKey(it.getClass());
        String key = (loc == null) ? null : loc.toString();

        int ratio = 0;
        if ( (key != null) && ConfigContainer.preserving.ratios.containsKey(key) )
        {
            ratio = ConfigContainer.preserving.ratios.get(key);

        }

        return ratio;
    }


    public static boolean doesRot(ConfigHandler.RotProperty rotProp)
    {
        return ((rotProp != null) && rotProp.doesRot());
    }

    public static ItemStack doRot(World world, ItemStack item)
    {
        ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(item);

        if ( !doesRot(rotProps) )
        {
            return clearRotData(item);
        }
        else
        {
            return updateRot(world, item, rotProps);
        }
    }

    private static ItemStack forceRot(ItemStack stack, String rotID)
    {
        // WAS int meta = rotProps.rotMeta < 0? item.getItemDamage() : rotProps.rotMeta;
        // int meta = stack.getMetadata();

        Item item = Item.REGISTRY.getObject(new ResourceLocation(rotID));

        return item == null ? ItemStack.EMPTY : new ItemStack(item, stack.getCount()); // , meta);
    }

    public static ItemStack updateRot(World world, ItemStack stack, ConfigHandler.RotProperty rotProps)
    {
        // TODO integrate with Serene Seasons temperature system
        // If stack in a cold/frozen location, change rot appropriately (as if esky or freezer)
        // Might allow building a walk-in freezer like in RimWorld

        if (!doesRot(rotProps))
        {
            clearRotData(stack);
            return stack;
        }

        long rotTime = rotProps.getRotTime();

        IRot cap = RotHelper.getRot(stack);

        long UBD = cap.getDate();
        long worldTime = world.getTotalWorldTime();

        // inititalization was missed somehow - so fix it
        if(UBD == 0)
        {
            // previous calculation:
            //UBD = (worldTime/ConfigHandler.TICKS_PER_DAY) * ConfigHandler.TICKS_PER_DAY;
            //UBD = UBD <= 0L? 1L : UBD
            UBD=worldTime;
            cap.setRot(UBD, rotTime); 	
        }

        long rotTimeStamp = UBD + rotTime;

        if(worldTime >= rotTimeStamp)
        {
            return forceRot(stack, rotProps.rotID);
        }

        // this shouldn't be needed - but if rotProps.rotTime changes from config change, this will update it
        cap.setRot(UBD, rotTime);

        return stack;
    }

    public static boolean rotInvo(World world, IInventory inventory)
    {
        int slots = (inventory == null) ? 0 : inventory.getSizeInventory();

        boolean dirty = false;

        try
        {
            for(int i = 0; i < slots; i++)
            {
                ItemStack slotItem = inventory.getStackInSlot(i);

                if((slotItem != null) && (!slotItem.isEmpty()))
                {
                    // TODO rotItem == slotItem is true when slotItem is just updated (shouldn't happen unless init missed) and not rotted
                    ItemStack rotItem = doRot(world, slotItem);

                    if(rotItem == null || rotItem.isEmpty() || (rotItem.getItem() != slotItem.getItem()))
                    {
                        if (rotItem == null)
                        {
                            rotItem = ItemStack.EMPTY;
                        }

                        inventory.setInventorySlotContents(i, rotItem);
                        dirty = true;
                    }
                }
            }

            if(dirty && inventory instanceof TileEntity)
            {
                ((TileEntity)inventory).markDirty();
            }

            return dirty;
        }
        catch(Exception e)
        {
            FoodFunk.logger.error("An error occured while attempting to rot inventory:", e);
            return false;
        }
    }

    public static boolean rotInvo(World world, Container inventory)
    {
        int count = (inventory == null) || (inventory.inventorySlots == null) ? 0 : inventory.inventorySlots.size();

        boolean dirty = false;

        try
        {
            for(int i = 0; i < count; i++)
            {
                Slot slot = inventory.getSlot(i);
                ItemStack slotItem = slot.getStack();

                if((slotItem != null) && (!slotItem.isEmpty()))
                {
                    ItemStack rotItem = doRot(world, slotItem);

                    if(rotItem == null || rotItem.isEmpty() || (rotItem.getItem() != slotItem.getItem()))
                    {
                        if (rotItem == null)
                        {
                            rotItem = ItemStack.EMPTY;
                        }

                        if (ConfigContainer.zdebugging.debug)
                        {
                            FoodFunk.logger.info("rotInvo-IInventory 2 sending slot " + i + " " + rotItem);
                        }

                        inventory.putStackInSlot(i, rotItem);
                        dirty = true;
                    }
                }
            }

            if(dirty)
            {
                inventory.detectAndSendChanges();
            }

            return dirty;
        }
        catch(Exception e)
        {
            FoodFunk.logger.error("An error occured while attempting to rot inventory:", e);
            return false;
        }
    }


    public static boolean rot(World world, Entity entity)
    {
        if (world.isRemote || !ConfigContainer.enabled)
        {
            return false;
        }

        IItemHandler capability = CapabilityUtils.getCapability(entity, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (capability != null)
        {
            return rotInvo(world, capability);
        }
        else if (entity instanceof EntityItem)
        {
            EntityItem item = (EntityItem)entity;

            ItemStack rotStack = RotHandler.doRot(world, item.getItem());

            if(item.getItem() != rotStack)
            {
                item.setItem(rotStack);
            }
        }
        else if (entity instanceof EntityPlayer)
        {
            IInventory invo = ((EntityPlayer)entity).inventory;
            return rotInvo(world, invo);
        } 
        else if (entity instanceof IInventory)
        {
            IInventory invo = (IInventory)entity;
            return rotInvo(world, invo);
        }

        return false;
    }

    public static <T> T as(Object o, Class<T> t)
    {
        return t.isInstance(o) ? t.cast(o) : null;
    }

    public static boolean rot(World world, TileEntity tile)
    {
        if ((world.isRemote) || (!ConfigContainer.enabled))
        {
            return false;
        }

        IItemHandler capability = CapabilityUtils.getCapability(tile, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (capability != null)
        {
            return rotInvo(world, capability);
        }
        else if (tile instanceof IInventory)
        {
            IInventory invo = (IInventory)tile; // as(tile, IInventory.class);

            return rotInvo(world, invo);
        }

        return false;
    }

    public static boolean rot(World world, Container container)
    {
        if ((world.isRemote) || (!ConfigContainer.enabled))
        {
            return false;
        }

        if (container instanceof IInventory)
        {
            IInventory invo = (IInventory)container;

            return rotInvo(world, invo);
        }

        return false;
    }

    public static boolean rotInvo(World world, IItemHandler inventory)
    {
        int slots = (inventory == null) ? 0 : inventory.getSlots();
        if ( slots <= 0 )
        {
            return false;
        }

        boolean flag = false;

        try
        {
            for(int i = 0; i < slots; i++)
            {
                ItemStack slotItem = inventory.getStackInSlot(i);
                int count = slotItem.getCount();

                if((slotItem != null) && (!slotItem.isEmpty()))
                {
                    ItemStack rotItem = doRot(world, slotItem);

                    if(rotItem == null || rotItem.isEmpty() || (rotItem.getItem() != slotItem.getItem()))
                    {
                        if (rotItem == null)
                        {
                            rotItem = ItemStack.EMPTY;
                        }
                        // Equivalent to inventory.setInventorySlotContents(i, rotItem);
                        inventory.extractItem(i, count, false);
                        inventory.insertItem(i, rotItem, false);
                        flag = true;
                    }
                }
            }

            return flag;
        }
        catch(Exception e)
        {
            FoodFunk.logger.error("An error occured while attempting to rot inventory:", e);
            return false;
        }
    }


    public static ItemStack removeDeprecatedRotData(ItemStack stack)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if(tags != null)
        {
            if(tags.hasKey("EM_ROT_DATE"))
            {
                tags.removeTag("EM_ROT_DATE");
            }
            if(tags.hasKey("EM_ROT_TIME"))
            {
                tags.removeTag("EM_ROT_TIME");
            }

            // remove empty NBT tag compound from rotten items so they can be merged - saw this bug onces
            if (tags.hasNoTags())
            {
                stack.setTagCompound(null);
            }
        }

        return stack;
    }

    public static ItemStack clearRotData(ItemStack stack)
    {
        // IRot rot = RotHelper.getRot(stack);
        // TODO: no way to clean Rot capability easily - have to have provider start ignoring it

        // Remove old
        return removeDeprecatedRotData(stack);
    }

    public static ItemStack rescheduleRot(ItemStack stack, long time, long worldTime)
    {
        IRot cap = RotHelper.getRot(stack);

        if (cap != null)
        {
            cap.reschedule(time);
        }

        return stack;
    }

    public static void handleCraftedRot(World world, IInventory craftMatrix, ItemStack crafting)
    {
        ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(crafting);

        IRot ccap = RotHelper.getRot(crafting);

        if(!doesRot(rotProps) || (ccap == null))
        {
            return; // Crafted item doesn't rot
        }

        long worldTime = world.getTotalWorldTime();
        long rotTime = rotProps.getRotTime();

        long lowestDate = worldTime;

        int slots = craftMatrix.getSizeInventory();
        for(int i = 0; i < slots; i++)
        {
            ItemStack stack = craftMatrix.getStackInSlot(i);

            if(stack == null || stack.isEmpty() || stack.getItem() == null)
            {
                continue;
            }

            IRot cap = RotHelper.getRot(stack);

            if( (cap != null) && (cap.getDate() < lowestDate))
            {
                lowestDate = cap.getDate();
            }
        }

        ccap.setRot(lowestDate, rotTime);
    }

    public static class RotTimes
    {
        /*
         *  The timestamp at which the stack is considered at 0% rot - creation time at first, but advanced by preserving containers
         */
        public long date;
        /*
         * The amount of time the item takes to rot.   The time at which becomes rotten is date + time
         */
        public long time;
        /*
         * The current time, being kept for consistent comparisons and convenient calculations
         */
        public long curTime;

        RotTimes(long _date, long _time, long _curTime)
        {
            date = _date;
            time = _time;
            curTime = _curTime;
        }

        public long getDate()
        {
            return date;
        }

        public long getTime()
        {
            return time;
        }

        public long getCurTime()
        {
            return curTime;
        }

        public long getExpirationTimestamp()
        {
            return date + time;
        }

        public int getPercent()
        {
            // make sure percent >= 0
            return Math.max(0, MathHelper.floor((double)(curTime - date)/time * 100D));
        }

        public int getDaysLeft()
        {
            return Math.max(0, MathHelper.floor((double)(curTime - date)/ConfigHandler.TICKS_PER_DAY));
        }

        public int getDaysTotal()
        {
            return MathHelper.floor((double)time/ConfigHandler.TICKS_PER_DAY);
        }

        public int getUseBy()
        {
            return MathHelper.floor((double)(date + time)/ConfigHandler.TICKS_PER_DAY);
        }

        public boolean isSet()
        {
            return (date > 0);
        }

        public boolean isNoRot()
        {
            return (time == ConfigHandler.DAYS_NO_ROT);
        }
    }

    @Nullable
    public static RotTimes getRotTimes(IRot cap, long curTime)
    {
        RotTimes rotTimes = null;

        if (cap != null)
        {
            long rotDate = cap.getDate();
            long rotTime = cap.getTime();

            rotTimes = new RotTimes(rotDate, rotTime, curTime);
        }

        return rotTimes;
    }

    public static void setDefaults(ItemStack stack, IRot cap)
    {
        ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(stack);

        if ( doesRot(rotProps) )
        {
            cap.setTime(rotProps.getRotTime());
        }
    }

    public static boolean isInTheCold(ItemStack stack)
    {
        // TODO walk up container tree, if any is cold chest then true.  If none, get world pos of topmost container.
        //     TODO Check isOnItemFrame
        // TODO if temperature mod, get temp from it
        //     TODO ToughAsNails:TemperatureHelper.getTargetAtPosUnclamped() ?  https://github.com/Glitchfiend/ToughAsNails
        // TODO if no temperature mod, check biome for temp?

        return false;
    }
}
