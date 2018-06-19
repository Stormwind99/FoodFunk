package com.wumple.foodfunk;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockEsky extends BlockChestBase implements ITileEntityProvider
{
    public BlockEsky()
	{
		super(Material.IRON);
		this.setHardness(3.0F);
	    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	    this.setCreativeTab(CreativeTabs.DECORATIONS);
	}
    
	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	public TileEntity createNewTileEntity(World worldIn, int meta) {
	    return new TileEntityEsky();
	}
}
