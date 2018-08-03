package com.wumple.foodfunk.coldchest.larder;

import com.wumple.util.basechest.TileEntityBaseChestRenderer;

import net.minecraft.util.ResourceLocation;

public class TileEntityLarderRenderer extends TileEntityBaseChestRenderer
{

    private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("foodfunk", "textures/model/larder.png");

    @Override
    protected ResourceLocation getTexture()
    {
        return CHEST_TEXTURE;
    }

}
