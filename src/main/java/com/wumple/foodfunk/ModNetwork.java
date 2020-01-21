package com.wumple.foodfunk;

import com.wumple.foodfunk.capability.Messages;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetwork
{
	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Reference.MOD_ID, "network");

	public static final String NETWORK_VERSION = new ResourceLocation(Reference.MOD_ID, "1").toString();

    // Start the IDs at 1 so any unregistered messages (ID 0) throw a more obvious
    // exception when received
    private static int messageID = 1;
    
	public static SimpleChannel getNetworkChannel()
	{
		final SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
				.clientAcceptedVersions(version -> true).serverAcceptedVersions(version -> true)
				.networkProtocolVersion(() -> NETWORK_VERSION).simpleChannel();
		
		int id = messageID;
		
		id = Messages.register(channel, id);
		
		return channel;
	}
}