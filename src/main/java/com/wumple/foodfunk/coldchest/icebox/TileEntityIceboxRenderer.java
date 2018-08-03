package com.wumple.foodfunk.coldchest.icebox;

import com.wumple.util.basechest.TileEntityBaseChestRenderer;

import net.minecraft.util.ResourceLocation;

public class TileEntityIceboxRenderer extends TileEntityBaseChestRenderer
{

    private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("foodfunk",
            "textures/model/icebox.png");

    @Override
    protected ResourceLocation getTexture()
    {
        return CHEST_TEXTURE;
    }

}
