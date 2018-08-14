package com.wumple.foodfunk.integration.theoneprobe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.capability.preserving.IPreserving;
import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.util.tooltip.ITooltipProvider;
import com.wumple.util.tooltip.TooltipUtils;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TOPProvider implements Function<ITheOneProbe, Void>
{
    public static ITheOneProbe probe;

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe)
    {
        probe = theOneProbe;
        FoodFunk.logger.info("Enabled support for The One Probe");
        probe.registerProvider(new IProbeInfoProvider()
        {
            @Override
            public String getID()
            {
                return "foodfunk:default";
            }
            
            protected void tipsToProbe(IProbeInfo probeInfo, List<String> tips)
            {
                for (String tip : tips)
                {
                    probeInfo.horizontal().text(tip);
                }
            }

            @Override
            public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
            {
                TileEntity te = world.getTileEntity(data.getPos());
                List<String> tips = new ArrayList<String>();
                ITooltipProvider[] providers = { IRot.getMyCap(te), IPreserving.getMyCap(te) };
                TooltipUtils.doTooltip(providers, null, player, ConfigContainer.zdebugging.debug, tips);
                tipsToProbe(probeInfo, tips);
            }
        });
        return null;
    }
}