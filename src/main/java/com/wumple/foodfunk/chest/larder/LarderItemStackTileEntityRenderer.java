package com.wumple.foodfunk.chest.larder;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LarderItemStackTileEntityRenderer extends ItemStackTileEntityRenderer
{
	private static final LarderTileEntity TE = new LarderTileEntity();
	public static LarderItemStackTileEntityRenderer instance = new LarderItemStackTileEntityRenderer();
	
	@Override
    public void renderByItem(ItemStack itemStackIn)
    {
        Item item = itemStackIn.getItem();

        if (Block.getBlockFromItem(item) instanceof LarderBlock)
        {
        	TileEntityRendererDispatcher.instance.renderAsItem(TE);
        }
        else
        {
            super.renderByItem(itemStackIn);
        }
    }
}