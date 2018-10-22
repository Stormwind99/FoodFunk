package com.wumple.foodfunk.rottables;

import javax.annotation.Nullable;

import com.wumple.foodfunk.capability.rot.IRot;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.util.adapter.TUtil;
import com.wumple.util.placeholder.TickingTileEntityPlaceholder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RotTickingTileEntity extends TickingTileEntityPlaceholder
{
    public RotTickingTileEntity() { super(); }
    public RotTickingTileEntity(World world) { super(world); }
    
    /**
     * This controls whether the tile entity gets replaced whenever the block state is changed. Normally only want this when block actually is replaced.
     */
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        if (ConfigContainer.rotting.refreshOnGrowth)
        {
            return true;
        }
        
        return (oldState.getBlock() != newState.getBlock());
    }
    
    @Override
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
        MinecraftServer server = world.getMinecraftServer();
        
        if (server != null)
        {
            server.addScheduledTask(new Runnable()
            {
              public void run() {
                  //System.out.println("scheduled task ensureInitialized " + this); 
                  doIt(world);
              }
            });
        }
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