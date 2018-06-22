package com.wumple.foodfunk;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

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
		} else
		{
			return updateRot(world, item, rotProps);
		}
	}

	private static void setRotValues(ItemStack stack, long date, long rotTime)
	{
		NBTTagCompound tags = ensureTagCompound(stack);

		tags.setLong("EM_ROT_DATE", date);
		tags.setLong("EM_ROT_TIME", rotTime);
	}

	private static ItemStack forceRot(ItemStack stack, String rotID)
	{
		// WAS int meta = rotProps.rotMeta < 0? item.getItemDamage() : rotProps.rotMeta;
		// int meta = stack.getMetadata();

		Item item = Item.REGISTRY.getObject(new ResourceLocation(rotID));

		return item == null ? null : new ItemStack(item, stack.getCount()); // , meta);
	}

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

		NBTTagCompound tags = ensureTagCompound(stack);

		long UBD = tags.getLong("EM_ROT_DATE");
		long worldTime = world.getTotalWorldTime();

		if(UBD == 0)
		{
			UBD = (worldTime/ConfigHandler.TICKS_PER_DAY) * ConfigHandler.TICKS_PER_DAY;
			UBD = UBD <= 0L? 1L : UBD;
			setRotValues(stack, UBD, rotTime);
			return stack;
		} 
		else if(UBD + rotTime < worldTime)
		{
			return forceRot(stack, rotProps.rotID);
		}
		else
		{
			setRotValues(stack, UBD, rotTime);
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

	public static ItemStack clearRotData(ItemStack item)
	{
		NBTTagCompound tags = item.getTagCompound();

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
				item.setTagCompound(null);
			}
		}

		return item;
	}

	public static void rescheduleRot(ItemStack stack, long time)
	{
		NBTTagCompound tags = ensureTagCompound(stack);

		if(tags.hasKey("EM_ROT_DATE"))
		{
			setRotValues(stack, tags.getLong("EM_ROT_DATE") + time, tags.getLong("EM_ROT_TIME") + time);
		}
	}

	public static void handleCraftedRot(World world, IInventory craftMatrix, ItemStack crafting)
	{
		ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(crafting);

		if(!doesRot(rotProps))
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

			NBTTagCompound tags = stack.getTagCompound();

			if(tags.hasKey("EM_ROT_DATE") && (lowestDate < 0 || tags.getLong("EM_ROT_DATE") < lowestDate))
			{
				lowestDate = tags.getLong("EM_ROT_DATE");
			}
		}

		if(lowestDate >= 0)
		{
			setRotValues(crafting, lowestDate, rotTime);
		}
	}

	public static class RotTimes
	{
		public double date;
		public double time;
		public double curTime;

		RotTimes(double _date, double _time, double _curTime)
		{
			date = _date;
			time = _time;
			curTime = _curTime;
		}

		public int getPercent()
		{
			// make sure percent >= 0
			return Math.max(0, MathHelper.floor((curTime - date)/time * 100D));
		}

		public int getDays()
		{
			return Math.max(0, MathHelper.floor((curTime - date)/ConfigHandler.TICKS_PER_DAY));
		}

		public int getTime()
		{
			return MathHelper.floor(time/ConfigHandler.TICKS_PER_DAY);
		}

		public int getUseBy()
		{
			return MathHelper.floor((date + time)/ConfigHandler.TICKS_PER_DAY);
		}
	}

	@Nullable
	public static RotTimes getRotTimes(ItemStack stack, double curTime)
	{
		RotTimes rotTimes = null;

		if (stack.hasTagCompound() && stack.getTagCompound().getLong("EM_ROT_DATE") > 0)
		{
			double rotDate = stack.getTagCompound().getLong("EM_ROT_DATE");
			double rotTime = stack.getTagCompound().getLong("EM_ROT_TIME");

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
