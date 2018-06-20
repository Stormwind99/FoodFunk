package com.wumple.foodfunk;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.registries.GameData;

public class BlockFreezer extends BlockChestBase implements ITileEntityProvider
{   
	public BlockFreezer()
	{
		super(Material.IRON);
		setHardness(3.0F);
	    setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	    setCreativeTab(CreativeTabs.DECORATIONS);
	    String name = "foodfunk:freezer";
	    setRegistryName(GameData.checkPrefix(name));
	    setUnlocalizedName(name);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityFreezer();
	}
}