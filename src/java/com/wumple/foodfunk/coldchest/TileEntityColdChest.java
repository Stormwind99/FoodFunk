package com.wumple.foodfunk.coldchest;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ITickable;

public abstract class TileEntityColdChest extends TileEntityBaseChest implements IInventory, ITickable
{
	public TileEntityColdChest() {
		super();
	}
}