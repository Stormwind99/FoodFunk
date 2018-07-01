package com.wumple.foodfunk.capabilities.preserving;

import com.wumple.foodfunk.FoodFunk;
import com.wumple.foodfunk.RotHandler;
import com.wumple.foodfunk.configuration.ConfigContainer;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler
{
		/**
		 * Attach the {@link IPreserving} capability to vanilla items.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
			TileEntity entity = event.getObject();
			
			if (RotHandler.doesPreserve(entity)) {
				PreservingProvider provider = new PreservingProvider(PreservingProvider.CAPABILITY, PreservingProvider.DEFAULT_FACING, entity);
				event.addCapability(PreservingProvider.ID, provider);
			}
		}
}
