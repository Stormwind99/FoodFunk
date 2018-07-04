package com.wumple.foodfunk.capability.preserving;

import net.minecraft.tileentity.TileEntity;

public interface IPreserving
{
    /**
     * Get the timestamp of the last check of this preserving cap owner's items
     */
    long getLastCheckTime();

    /**
     * Set the timestamp of the last check of this preserving cap owner's items
     */
    void setLastCheckTime(long time);

    /**
     * Set the owner of this capability, and init based on that owner
     */
    void setOwner(TileEntity ownerIn);

    /**
     * Automatically adjust the use-by date on food items stored within to slow or stop rot
     */
    void freshenContents();
    
    /**
     * @return the preserving ratio (100 means no rot, 50 means half speed rot)
     */
    int getRatio();
}
