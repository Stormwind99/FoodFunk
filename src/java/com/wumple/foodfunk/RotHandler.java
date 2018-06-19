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
	public static ItemStack doRot(World world, ItemStack item)
	{
		//System.out.println("Rotting: " + item.getDisplayName());
		
		ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(item);
				
		if ( (rotProps == null) || (!rotProps.doesRot()) )
		{
			return clearRotData(item);
		} else
		{
			return updateRot(world, item, rotProps);
		}
	}
	
	public static ItemStack updateRot(World world, ItemStack stack, ConfigHandler.RotProperty rotProps)
	{
		if (rotProps == null)
		{
			clearRotData(stack);
			return stack;
		}
		
		long rotTime = rotProps.getRotTime();

		if(stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		long UBD = stack.getTagCompound().getLong("EM_ROT_DATE");
		
		if(UBD == 0)
		{
			UBD = (world.getTotalWorldTime()/ConfigHandler.TICKS_PER_DAY) * ConfigHandler.TICKS_PER_DAY;
			UBD = UBD <= 0L? 1L : UBD;
			stack.getTagCompound().setLong("EM_ROT_DATE", UBD);
			stack.getTagCompound().setLong("EM_ROT_TIME", rotTime);
			return stack;
		} 
		else if(UBD + rotTime < world.getTotalWorldTime())
		{
			/*
			if(rotProps == null)
			{
				return new ItemStack(ObjectHandler.rottenFood, stack.getCount());
			}
			else
			*/
			{
				// WAS int meta = rotProps.rotMeta < 0? item.getItemDamage() : rotProps.rotMeta;
				int meta = stack.getMetadata();
				
				Item item = Item.REGISTRY.getObject(new ResourceLocation(rotProps.rotID));
							
				return item == null ? null : new ItemStack(item, stack.getCount(), meta);
			}
		}
		else
		{
			stack.getTagCompound().setLong("EM_ROT_TIME", rotTime);
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
		} catch(Exception e)
		{
			FoodFunk.logger.log(Level.ERROR, "An error occured while attempting to rot inventory:", e);
			return;
		}
	}
	
	public static ItemStack clearRotData(ItemStack item)
	{
		if(item.getTagCompound() != null)
		{
			if(item.getTagCompound().hasKey("EM_ROT_DATE"))
			{
				item.getTagCompound().removeTag("EM_ROT_DATE");
			}
			if(item.getTagCompound().hasKey("EM_ROT_TIME"))
			{
				item.getTagCompound().removeTag("EM_ROT_TIME");
			}
		}
		
		return item;
	}
	
	public static void rescheduleRot(ItemStack stack, long time)
	{
		if(stack.getTagCompound() == null)
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tags = stack.getTagCompound();
		
		if(tags.hasKey("EM_ROT_DATE"))
		{
			tags.setLong("EM_ROT_DATE", tags.getLong("EM_ROT_DATE") + time);
			tags.setLong("EM_ROT_TIME", tags.getLong("EM_ROT_TIME") + time);
		}
	}
	
	public static void handleCraftedRot(World world, IInventory craftMatrix, ItemStack crafting)
	{
		ConfigHandler.RotProperty rotProps = ConfigHandler.getRotProperty(crafting);
		
		if(rotProps == null)
		{
			return; // Crafted item does not rot
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
			
			if(stack.getTagCompound().hasKey("EM_ROT_DATE") && (lowestDate < 0 || stack.getTagCompound().getLong("EM_ROT_DATE") < lowestDate))
			{
				lowestDate = stack.getTagCompound().getLong("EM_ROT_DATE");
			}
		}
		
		if(lowestDate >= 0)
		{
			if(crafting.getTagCompound() == null)
			{
				crafting.setTagCompound(new NBTTagCompound());
			}
			
			crafting.getTagCompound().setLong("EM_ROT_DATE", lowestDate);
			crafting.getTagCompound().setLong("EM_ROT_TIME", rotTime);
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
			return MathHelper.floor((curTime - date)/ConfigHandler.TICKS_PER_DAY);
		}
		
		public int getTime()
		{
			return MathHelper.floor(time/ConfigHandler.TICKS_PER_DAY);
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
}
