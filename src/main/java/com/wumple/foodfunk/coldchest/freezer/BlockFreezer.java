package com.wumple.foodfunk.coldchest.freezer;

import javax.annotation.Nullable;

import com.wumple.foodfunk.basechest.BlockBaseChest;
import com.wumple.util.RegistrationHelpers;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFreezer extends BlockBaseChest implements ITileEntityProvider
{
    public BlockFreezer()
    {
        super(Material.IRON);
        setHardness(3.0F);
        setCreativeTab(CreativeTabs.MISC);

        RegistrationHelpers.nameHelper(this, "foodfunk:freezer");
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Override
    @Nullable
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityFreezer();
    }
}