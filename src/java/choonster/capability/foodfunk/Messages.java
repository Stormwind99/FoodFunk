package choonster.capability.foodfunk;

import com.wumple.foodfunk.FoodFunk;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.Side;

public class Messages {
	// Start the IDs at 1 so any unregistered messages (ID 0) throw a more obvious exception when received
	private static int messageID = 1;

	public static void register() {
		registerMessage(MessageBulkUpdateContainerRots.Handler.class, MessageBulkUpdateContainerRots.class, Side.CLIENT);
		registerMessage(MessageUpdateContainerRot.Handler.class, MessageUpdateContainerRot.class, Side.CLIENT);
	}

	private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(final Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, final Class<REQ> requestMessageType, final Side receivingSide) {
		FoodFunk.network.registerMessage(messageHandler, requestMessageType, messageID++, receivingSide);
	}
}
