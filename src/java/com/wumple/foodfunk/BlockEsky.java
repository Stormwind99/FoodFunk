package com.wumple.foodfunk;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.registries.GameData;

public class BlockEsky extends BlockChestBase implements ITileEntityProvider
{
    public BlockEsky()
	{
		super(Material.IRON);
		setHardness(3.0F);
	    setCreativeTab(CreativeTabs.DECORATIONS);
	    
	    String name = "foodfunk:esky";
	    setRegistryName(GameData.checkPrefix(name));
	    setUnlocalizedName(name);
	    
	    //setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}
    
	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	public TileEntity createNewTileEntity(World worldIn, int meta) {
	    return new TileEntityEsky();
	}
}
