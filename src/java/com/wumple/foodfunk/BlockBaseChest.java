package com.wumple.foodfunk;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockBaseChest extends BlockContainer implements ITileEntityProvider {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockBaseChest(Material materialIn) {
		super(materialIn);
	}

	public BlockBaseChest(Material materialIn, MapColor color) {
		super(materialIn, color);
	}
	
	// from http://www.minecraftforge.net/forum/topic/62067-solved-itickable-and-tes-not-ticking/
	@Override
	public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }
	
	// from http://www.minecraftforge.net/forum/topic/42458-solved1102-blockstates-crashing/?do=findComment&comment=228689
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}

	// from https://stackoverflow.com/questions/34677155/minecraft-doesnt-find-blockstates-state
	/* This method returns the IBlockState from the metadata
	 * so we can have the proper rotation and textures of the block. 
	 * This method is usually only called on map/chunk load.
	 */
	public IBlockState getStateFromMeta( int meta )
	{
	    EnumFacing enumfacing = EnumFacing.getFront(meta);

	    if (enumfacing.getAxis() == EnumFacing.Axis.Y)
	    {
	        enumfacing = EnumFacing.NORTH;
	    }

	    return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	// from https://stackoverflow.com/questions/34677155/minecraft-doesnt-find-blockstates-state
	/* Here the EnumFacing is translated into a metadata value(0-15)
	 * so it can be stored. You can store up to 16 different states alone
	 * in metadata, but no more. If you need more consider using a tile
	 * entity alongside the metadata for more flexiblity
	 */
	public int getMetaFromState( IBlockState state )
	{
	    return (( EnumFacing )state.getValue( FACING )).getIndex();
	}
	
	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
			float hitX, float hitY, float hitZ) {
				if (worldIn.isRemote)
				{
					return true;
				}
				else
				{
					IInventory iinventory = (TileEntityColdChest)worldIn.getTileEntity(pos);
					
					if (iinventory != null)
					{
						playerIn.displayGUIChest(iinventory);
					}
					
					return true;
				}
			}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 * @deprecated call via {@link IBlockState#isOpaqueCube()} whenever possible. Implementing/overriding is fine.
	 */
	public boolean isOpaqueCube(IBlockState state) {
	    return false;
	}

	/**
	 * @deprecated call via {@link IBlockState#isFullCube()} whenever possible. Implementing/overriding is fine.
	 */
	public boolean isFullCube(IBlockState state) {
	    return false;
	}

	/**
	 * @deprecated call via {@link IBlockState#hasCustomBreakingProgress()} whenever possible. Implementing/overriding
	 * is fine.
	 */
	@SideOnly(Side.CLIENT)
	public boolean hasCustomBreakingProgress(IBlockState state) {
	    return true;
	}

	/**
	 * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
	 * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
	 * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
	 */
	public EnumBlockRenderType getRenderType(IBlockState state) {
	    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	/**
	 * Called by ItemBlocks after a block is set in the world, to allow post-place logic
	 */
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
	    EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
	    state = state.withProperty(FACING, enumfacing);
	    BlockPos blockpos = pos.north();
	    BlockPos blockpos1 = pos.south();
	    BlockPos blockpos2 = pos.west();
	    BlockPos blockpos3 = pos.east();
	    boolean flag = this == worldIn.getBlockState(blockpos).getBlock();
	    boolean flag1 = this == worldIn.getBlockState(blockpos1).getBlock();
	    boolean flag2 = this == worldIn.getBlockState(blockpos2).getBlock();
	    boolean flag3 = this == worldIn.getBlockState(blockpos3).getBlock();
	
	    if (!flag && !flag1 && !flag2 && !flag3)
	    {
	        worldIn.setBlockState(pos, state, 3);
	    }
	    else if (enumfacing.getAxis() != EnumFacing.Axis.X || !flag && !flag1)
	    {
	        if (enumfacing.getAxis() == EnumFacing.Axis.Z && (flag2 || flag3))
	        {
	            if (flag2)
	            {
	                worldIn.setBlockState(blockpos2, state, 3);
	            }
	            else
	            {
	                worldIn.setBlockState(blockpos3, state, 3);
	            }
	
	            worldIn.setBlockState(pos, state, 3);
	        }
	    }
	    else
	    {
	        if (flag)
	        {
	            worldIn.setBlockState(blockpos, state, 3);
	        }
	        else
	        {
	            worldIn.setBlockState(blockpos1, state, 3);
	        }
	
	        worldIn.setBlockState(pos, state, 3);
	    }
	
	    if (stack.hasDisplayName())
	    {
	        TileEntity tileentity = worldIn.getTileEntity(pos);
	
	        if (tileentity instanceof TileEntityChest)
	        {
	            ((TileEntityChest)tileentity).setCustomName(stack.getDisplayName());
	        }
	    }
	}

	/**
	 * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
	 */
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
	    TileEntity tileentity = worldIn.getTileEntity(pos);
	
	    if (tileentity instanceof IInventory)
	    {
	        InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
	        worldIn.updateComparatorOutputLevel(pos, this);
	    }
	
	    super.breakBlock(worldIn, pos, state);
	}

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Nullable
    abstract public TileEntity createNewTileEntity(World worldIn, int meta);
}