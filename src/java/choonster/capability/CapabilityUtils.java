// by Choonster
// from https://github.com/Choonster-Minecraft-Mods/TestMod3

package choonster.capability;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Utility methods for Capabilities.
 *
 * @author Choonster
 */
public class CapabilityUtils
{
	/**
	 * Get a capability handler from an {@link ICapabilityProvider} if it exists.
	 *
	 * @param provider
	 *            The provider
	 * @param capability
	 *            The capability
	 * @param facing
	 *            The facing
	 * @param <T>
	 *            The handler type
	 * @return The handler, if any.
	 */
	@Nullable
	public static <T> T getCapability(@Nullable ICapabilityProvider provider, Capability<T> capability,
			@Nullable EnumFacing facing)
	{
		return provider != null && provider.hasCapability(capability, facing)
				? provider.getCapability(capability, facing)
				: null;
	}

	public static IThreadListener getThreadListener(final MessageContext context)
	{
		if (context.side.isServer())
		{
			return context.getServerHandler().player.mcServer;
		} 
		else if (context.side.isClient()) 
		{
			return Minecraft.getMinecraft();
		}
		else
		{
			throw new RuntimeException("Unknown side");
		}
	}

	public static EntityPlayer getPlayer(final MessageContext context) {
        if (context.side.isServer()) {
                return context.getServerHandler().player;
        } else if (context.side.isClient()) {
            return Minecraft.getMinecraft().player;
        } else {
        	throw new RuntimeException("Unknown side");
        }
	}
}
