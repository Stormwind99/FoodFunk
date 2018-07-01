package com.wumple.foodfunk.capabilities.rot;

import com.wumple.foodfunk.RotHandler;
import com.wumple.foodfunk.configuration.ConfigHandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandler
{
		/**
		 * Attach the {@link IRot} capability to vanilla items.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
			ItemStack stack = event.getObject();
			ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(stack);
			if (RotHandler.doesRot(rotProps)) {
				event.addCapability(RotProvider.ID, RotHelper.createProvider(stack));
			}
		}
}
