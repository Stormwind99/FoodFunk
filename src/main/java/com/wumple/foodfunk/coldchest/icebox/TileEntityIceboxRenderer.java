package com.wumple.foodfunk.coldchest.icebox;

import com.wumple.util.basechest.TileEntityBaseChest;
import com.wumple.util.basechest.TileEntityBaseChestRenderer;

import net.minecraft.client.renderer.GlStateManager;
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
    
    @Override
    protected void rotate(TileEntityBaseChest te)
    {
        super.rotate(te);
        
        // sideways chest
        GlStateManager.rotate(90, 0.0F, 0.0F, 1.0F);
        // open from front to the right
        GlStateManager.rotate(90, 1.0F, 0.0F, 0.0F);
    }
}