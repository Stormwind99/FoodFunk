package com.wumple.foodfunk.capability.preserving;

import net.minecraft.tileentity.TileEntity;

interface IPreserving
{
    long getLastCheckTime();

    void setLastCheckTime(long time);

    /*
     * Set the owner of this capability, and init based on that owner
     */
    void setOwner(TileEntity ownerIn);

    /**
     * Automatically adjust the use-by date on food items stored within to slow or stop rot
     */
    void freshenContents();
}
