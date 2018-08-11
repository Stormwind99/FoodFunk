package com.wumple.foodfunk.rottables;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.util.adapter.TUtil;
import com.wumple.util.placeholder.TickingTileEntityPlaceholder;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.world.World;

public class RotTickingTileEntity extends TickingTileEntityPlaceholder
{
    public void doIt(World world)
    {
        IRot cap = IRot.getMyCap(this);
        if (cap != null)
        {
            cap.evaluate(world, TUtil.to(this));
        }
    }

    @Override
    public void ensureInitialized(World world)
    {
        doIt(world);
    }


    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }
}