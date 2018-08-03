package com.wumple.foodfunk.coldchest.icebox;

import javax.annotation.Nullable;

import com.wumple.util.basechest.BlockBaseChest;
import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockIcebox extends BlockBaseChest implements ITileEntityProvider
{
    public BlockIcebox()
    {
        super(Material.IRON);
        setHardness(3.0F);
        setCreativeTab(CreativeTabs.MISC);

        RegistrationHelpers.nameHelper(this, "foodfunk:icebox");
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Override
    @Nullable
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityIcebox();
    }
}