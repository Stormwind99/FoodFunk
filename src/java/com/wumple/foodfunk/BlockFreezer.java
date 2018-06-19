package com.wumple.foodfunk;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockFreezer extends BlockChestBase implements ITileEntityProvider
{   
	public BlockFreezer()
	{
		super(Material.IRON);
		this.setHardness(3.0F);
	    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	    this.setCreativeTab(CreativeTabs.DECORATIONS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityFreezer();
	}
}