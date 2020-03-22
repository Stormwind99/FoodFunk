package com.wumple.foodfunk.chest.icebox;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IceboxItemStackTileEntityRenderer extends ItemStackTileEntityRenderer
{
	private static final IceboxTileEntity TE = new IceboxTileEntity();
	public static IceboxItemStackTileEntityRenderer instance = new IceboxItemStackTileEntityRenderer();
	
	@Override
    public void renderByItem(ItemStack itemStackIn)
    {
        Item item = itemStackIn.getItem();

        if (Block.getBlockFromItem(item) instanceof IceboxBlock)
        {
        	TileEntityRendererDispatcher.instance.renderAsItem(TE);
        }
        else
        {
            super.renderByItem(itemStackIn);
        }
    }
}