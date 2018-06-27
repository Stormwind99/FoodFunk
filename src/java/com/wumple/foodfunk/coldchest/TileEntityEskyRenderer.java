package com.wumple.foodfunk.coldchest;

import net.minecraft.util.ResourceLocation;

public class TileEntityEskyRenderer extends TileEntityBaseChestRenderer {

	private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("foodfunk", "textures/model/esky.png");

	@Override
	protected ResourceLocation getTexture() {
		return CHEST_TEXTURE;
	}

}
