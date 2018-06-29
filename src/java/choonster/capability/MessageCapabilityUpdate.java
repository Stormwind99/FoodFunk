package choonster.capability;

import java.io.IOException;

// TODO MAJOR WORK

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

// TODO Incomplete!

/**
 * Base class for messages that update capability data
 *
 * @param <HANDLER>
 *            The capability handler type
 * @param <DATA>
 *            The data type written to and read from the buffer
 * @author Choonster
 */
public abstract class MessageCapabilityUpdate<HANDLER, DATA> implements IMessage
{
	protected final SimpleCapabilityProvider<HANDLER> provider;

	protected MessageCapabilityUpdate(final SimpleCapabilityProvider<HANDLER> provider)
	{
		this.provider = provider;
	}

	@Override
	/**
	 * Convert from the supplied buffer into your specific message type
	 *
	 * @param buf
	 */
	public void fromBytes(ByteBuf buf)
	{
		// requires Access Transformer from foodfunk_at.cfg
		/*
		NBTBase nbt = null;
				
		try
		{
			nbt = CompressedStreamTools.read(new ByteBufInputStream(buf), new NBTSizeTracker(2097152L));		
		}
		catch (IOException e)
		{
			
		}
		finally
		{
			provider.getCapability().readNBT(provider.getInstance(), provider.getFacing(), nbt);
		}
		*/
	}

	@Override
	/**
	 * Deconstruct your message into the supplied byte buffer
	 * 
	 * @param buf
	 */
	public void toBytes(ByteBuf buf)
	{
		// requires Access Transformer from foodfunk_at.cfg
		/*
		NBTBase nbt = provider.getCapability().writeNBT(provider.getInstance(), provider.getFacing());	
		
		try
		{
			CompressedStreamTools.writeTag(nbt, new ByteBufOutputStream(buf));
		}
		catch (IOException e)
		{
		}
		*/
	}

	public abstract static class Handler<HANDLER, DATA, MESSAGE extends MessageCapabilityUpdate<HANDLER, DATA>>
			implements IMessageHandler<MESSAGE, IMessage>
	{

		/**
		 * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
		 * is needed.
		 *
		 * @param message The message
		 * @param ctx     The message context
		 * @return an optional return message
		 */
		@Nullable
		@Override
		public final IMessage onMessage(final MESSAGE message, final MessageContext ctx) {
			
            ItemStack originalStack = null;
            HANDLER originalHandler = CapabilityUtils.getCapability(originalStack, message.provider.getCapability(), message.provider.getFacing());

            CapabilityUtils.getThreadListener(ctx).addScheduledTask(() -> {
            	
            } );
            
            return null;
		}

	}
}
