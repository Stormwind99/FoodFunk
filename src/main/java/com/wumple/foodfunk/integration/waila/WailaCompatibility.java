package com.wumple.foodfunk.integration.waila;

import java.util.List;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.rottables.TileEntityMelonRottable;
import com.wumple.foodfunk.rottables.TileEntityPumpkinRottable;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class WailaCompatibility implements IWailaDataProvider
{

    public static final WailaCompatibility INSTANCE = new WailaCompatibility();

    private WailaCompatibility()
    {
    }

    private static boolean registered;
    private static boolean loaded;

    public static void load(IWailaRegistrar registrar)
    {
        //System.out.println("WailaCompatibility.load");
        if (!registered)
        {
            throw new RuntimeException("Please register this handler using the provided method.");
        }
        if (!loaded)
        {
            /*
             * registrar.registerHeadProvider(INSTANCE, DataBlock.class);
             * registrar.registerBodyProvider(INSTANCE, DataBlock.class);
             * registrar.registerTailProvider(INSTANCE, DataBlock.class);
             */
            registrar.registerBodyProvider(INSTANCE, TileEntityMelonRottable.class);
            registrar.registerBodyProvider(INSTANCE, TileEntityPumpkinRottable.class);
            loaded = true;
        }
    }

    public static void register()
    {
        if (registered) { return; }
        registered = true;
        FMLInterModComms.sendMessage("waila", "register", "com.wumple.foodfunk.integration.waila.WailaCompatibility.load");
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos)
    {
        return tag;
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity te = accessor.getWorld().getTileEntity(accessor.getPosition());
        IRot rot = IRot.getRot(te);
        if (rot != null)
        {
            rot.doTooltip(null, accessor.getPlayer(), ConfigContainer.zdebugging.debug, currenttip);
        }
        /*
        Block block = accessor.getBlock();
        if (block instanceof WailaInfoProvider)
        {
            return ((WailaInfoProvider) block).getWailaBody(itemStack, currenttip, accessor, config);
        }
        */
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

}