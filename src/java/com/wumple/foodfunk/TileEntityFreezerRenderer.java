package com.wumple.foodfunk;

import net.minecraft.util.ResourceLocation;

public class TileEntityFreezerRenderer extends TileEntityBaseChestRenderer {

	private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("foodfunk", "textures/model/freezer.png");

	@Override
	protected ResourceLocation getTexture() {
		return CHEST_TEXTURE;
	}

}
