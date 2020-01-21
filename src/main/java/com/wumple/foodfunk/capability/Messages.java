package com.wumple.foodfunk.capability;

import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Messages
{
    public static int register(SimpleChannel channel, int id)
    {
		channel.messageBuilder(MessageBulkUpdateContainerRots.class, id++)
		.decoder(MessageBulkUpdateContainerRots::decode)
		.encoder(MessageBulkUpdateContainerRots::encode)
		.consumer(MessageBulkUpdateContainerRots::handle)
		.add();

		channel.messageBuilder(MessageUpdateContainerRot.class, id++)
		.decoder(MessageUpdateContainerRot::decode)
		.encoder(MessageUpdateContainerRot::encode)
		.consumer(MessageUpdateContainerRot::handle)
		.add();
		
		return id;
    }
}
