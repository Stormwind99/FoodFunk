package com.wumple.foodfunk;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.wumple.foodfunk.capabilities.rot.IRot;
import com.wumple.foodfunk.capabilities.rot.RotHelper;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class RotHandler
{
	public static boolean doesPreserve(TileEntity it)
	{
		ResourceLocation loc = (it == null) ? null : TileEntity.getKey(it.getClass());
		String key = (loc == null) ? null : loc.toString();
		
		//FoodFunk.logger.info("RotHandler.doesPreserve key " + key);
		
		if ( (key != null) && ConfigContainer.preserving.ratios.containsKey(key) )
		{
			int ratio = ConfigContainer.preserving.ratios.get(key);
			if (ratio != 0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static int getPreservingRatio(TileEntity it)
	{
		ResourceLocation loc = (it == null) ? null : TileEntity.getKey(it.getClass());
		String key = (loc == null) ? null : loc.toString();
		
		int ratio = 0;
		if ( (key != null) && ConfigContainer.preserving.ratios.containsKey(key) )
		{
			ratio = ConfigContainer.preserving.ratios.get(key);
			
		}
		
		return ratio;
	}

	
	public static boolean doesRot(ConfigHandler.RotProperty rotProp)
	{
		return ((rotProp != null) && rotProp.doesRot());
	}

	public static ItemStack doRot(World world, ItemStack item)
	{
		//FoodFunk.logger.debug("Rotting: " + item.getDisplayName());

		ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(item);

		if ( !doesRot(rotProps) )
		{
			return clearRotData(item);
		}
		else
		{
			return updateRot(world, item, rotProps);
		}
	}

	/*
	private static void setRotValues(ItemStack stack, long date, long rotTime)
	{
		NBTTagCompound tags = ensureTagCompound(stack);

		tags.setLong("EM_ROT_DATE", date);
		tags.setLong("EM_ROT_TIME", rotTime);
	}
	*/

	private static ItemStack forceRot(ItemStack stack, String rotID)
	{
		// WAS int meta = rotProps.rotMeta < 0? item.getItemDamage() : rotProps.rotMeta;
		// int meta = stack.getMetadata();

		Item item = Item.REGISTRY.getObject(new ResourceLocation(rotID));

		return item == null ? ItemStack.EMPTY : new ItemStack(item, stack.getCount()); // , meta);
	}

	/*
	private static NBTTagCompound ensureTagCompound(ItemStack stack)
	{
		NBTTagCompound tags = stack.getTagCompound();

		if(tags == null)
		{
			tags = new NBTTagCompound();
			stack.setTagCompound(tags);
		}

		return stack.getTagCompound();
	}
	*/	

	public static ItemStack updateRot(World world, ItemStack stack, ConfigHandler.RotProperty rotProps)
	{
		// TODO integrate with Serene Seasons temperature system
		// If stack in a cold/frozen location, change rot appropriately (as if esky or freezer)
		// Might allow building a walk-in freezer like in RimWorld

		if (!doesRot(rotProps))
		{
			clearRotData(stack);
			return stack;
		}

		long rotTime = rotProps.getRotTime();

		IRot cap = RotHelper.getRot(stack);
	
		long UBD = cap.getDate();
		long worldTime = world.getTotalWorldTime();

		if(UBD == 0)
		{
			UBD = (worldTime/ConfigHandler.TICKS_PER_DAY) * ConfigHandler.TICKS_PER_DAY;
			UBD = UBD <= 0L? 1L : UBD;
			cap.setRot(UBD, rotTime); 
			//FoodFunk.logger.info("RotHandler.updateRot 1 " + stack.getItem().getRegistryName() + " date " + cap.getDate() + " time " + cap.getTime() + " cur " + worldTime);
			return stack;
		} 
		else if(UBD + rotTime < worldTime)
		{
			return forceRot(stack, rotProps.rotID);
		}
		else
		{
			cap.setRot(UBD, rotTime);
			//FoodFunk.logger.info("RotHandler.updateRot 2 " + stack.getItem().getRegistryName() + " date "  + cap.getDate() + " time " + cap.getTime() + " cur " + worldTime);
			return stack;
		}

	}

	public static void rotInvo(World world, IInventory inventory)
	{
		if ( (inventory == null) || (inventory.getSizeInventory() <= 0) )
		{
			return;
		}

		boolean flag = false;

		try
		{
			for(int i = 0; i < inventory.getSizeInventory(); i++)
			{
				ItemStack slotItem = inventory.getStackInSlot(i);

				if((slotItem != null) && (!slotItem.isEmpty()))
				{
					ItemStack rotItem = doRot(world, slotItem);

					if(rotItem == null || rotItem.isEmpty() || (rotItem.getItem() != slotItem.getItem()))
					{
						if (rotItem == null)
						{
							rotItem = ItemStack.EMPTY;
						}
						inventory.setInventorySlotContents(i, rotItem);
						flag = true;
					}
				}
			}

			if(flag && inventory instanceof TileEntity)
			{
				((TileEntity)inventory).markDirty();
			}
		}
		catch(Exception e)
		{
			FoodFunk.logger.log(Level.ERROR, "An error occured while attempting to rot inventory:", e);
			return;
		}
	}

	public static ItemStack removeDeprecatedRotData(ItemStack stack)
	{
		NBTTagCompound tags = stack.getTagCompound();

		if(tags != null)
		{
			if(tags.hasKey("EM_ROT_DATE"))
			{
				tags.removeTag("EM_ROT_DATE");
			}
			if(tags.hasKey("EM_ROT_TIME"))
			{
				tags.removeTag("EM_ROT_TIME");
			}

			// remove empty NBT tag compound from rotten items so they can be merged - saw this bug onces
			if (tags.hasNoTags())
			{
				stack.setTagCompound(null);
			}
		}
		
		return stack;
	}
	
	public static ItemStack clearRotData(ItemStack stack)
	{
		// IRot rot = RotHelper.getRot(stack);
		// TODO: no way to clean Rot capability easily - have to have provider start ignoring it
		
		// Remove old
		return removeDeprecatedRotData(stack);
	}

	public static ItemStack rescheduleRot(ItemStack stack, long time, long worldTime)
	{
		IRot cap = RotHelper.getRot(stack);
		
		if (cap != null)
		{
			cap.reschedule(time);
			//FoodFunk.logger.info("RotHandler.rescheduleRot 1 " + stack.getItem().getRegistryName() + " date " + cap.getDate() + " time " + cap.getTime() + " cur " + worldTime);
		}
		
		return stack;
	}

	public static void handleCraftedRot(World world, IInventory craftMatrix, ItemStack crafting)
	{
		ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(crafting);

		IRot ccap = RotHelper.getRot(crafting);
		
		if(!doesRot(rotProps) || (ccap == null))
		{
			return; // Crafted item doesn't rot
		}

		long rotTime = rotProps.getRotTime();

		long lowestDate = ConfigHandler.DAYS_NO_ROT;

		for(int i = 0; i < craftMatrix.getSizeInventory(); i++)
		{
			ItemStack stack = craftMatrix.getStackInSlot(i);

			if(stack == null || stack.isEmpty() || stack.getItem() == null || stack.getTagCompound() == null)
			{
				continue;
			}

			IRot cap = RotHelper.getRot(stack);

			if( (cap != null) && (lowestDate < 0 || cap.getDate() < lowestDate))
			{
				lowestDate = cap.getDate();
			}
		}

		if(lowestDate >= 0)
		{
			ccap.setRot(lowestDate, rotTime);
		}
	}

	public static class RotTimes
	{
		/*
		 *  The timestamp at which the stack is considered at 0% rot - creation time at first, but advanced by preserving containers
		 */
		public long date;
		/*
		 * The amount of time the item takes to rot.   The time at which becomes rotten is date + time
		 */
		public long time;
		/*
		 * The current time, being kept for consistent comparisons and convenient calculations
		 */
		public long curTime;

		RotTimes(long _date, long _time, long _curTime)
		{
			date = _date;
			time = _time;
			curTime = _curTime;
		}

		public int getPercent()
		{
			// make sure percent >= 0
			return Math.max(0, MathHelper.floor((double)(curTime - date)/time * 100D));
		}

		public int getDaysLeft()
		{
			return Math.max(0, MathHelper.floor((double)(curTime - date)/ConfigHandler.TICKS_PER_DAY));
		}

		public int getDaysTotal()
		{
			return MathHelper.floor((double)time/ConfigHandler.TICKS_PER_DAY);
		}

		public int getUseBy()
		{
			return MathHelper.floor((double)(date + time)/ConfigHandler.TICKS_PER_DAY);
		}
	}

	/*
	@Nullable
	public static RotTimes getRotTimes(ItemStack stack, long curTime)
	{
		RotTimes rotTimes = null;

		if (stack.hasTagCompound() && stack.getTagCompound().getLong("EM_ROT_DATE") > 0)
		{
			long rotDate = stack.getTagCompound().getLong("EM_ROT_DATE");
			long rotTime = stack.getTagCompound().getLong("EM_ROT_TIME");

			rotTimes = new RotTimes(rotDate, rotTime, curTime);
		}

		return rotTimes;
	}
	*/
	
	@Nullable
	public static RotTimes getRotTimes(IRot cap, long curTime)
	{
		RotTimes rotTimes = null;

		if (cap != null)
		{
			long rotDate = cap.getDate();
			long rotTime = cap.getTime();

			rotTimes = new RotTimes(rotDate, rotTime, curTime);
		}

		return rotTimes;
	}
	
	public static boolean isInTheCold(ItemStack stack)
	{
		// TODO walk up container tree, if any is cold chest then true.  If none, get world pos of topmost container.
		//     TODO Check isOnItemFrame
		// TODO if temperature mod, get temp from it
		//     TODO ToughAsNails:TemperatureHelper.getTargetAtPosUnclamped() ?  https://github.com/Glitchfiend/ToughAsNails
		// TODO if no temperature mod, check biome for temp?
		
		return false;
	}
}
