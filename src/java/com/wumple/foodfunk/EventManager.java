package com.wumple.foodfunk;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EventManager
{
	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(ConfigContainer.rotEnabled && !event.getWorld().isRemote)
		{
			if (event.getEntity() instanceof EntityItem)
			{
				EntityItem item = (EntityItem)event.getEntity();
				ItemStack rotStack = RotHandler.doRot(event.getWorld(), item.getItem());
				
				if(item.getItem() != rotStack)
				{
					item.setItem(rotStack);
				}
			} 
			else if (event.getEntity() instanceof EntityPlayer)
			{
				IInventory invo = ((EntityPlayer)event.getEntity()).inventory;
				RotHandler.rotInvo(event.getWorld(), invo);
			} 
			else if (event.getEntity() instanceof IInventory)
			{
				IInventory invo = (IInventory)event.getEntity();
				RotHandler.rotInvo(event.getWorld(), invo);
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		if(ConfigContainer.rotEnabled && event instanceof RightClickBlock && !event.getWorld().isRemote)
		{
			TileEntity tile = event.getEntityPlayer().world.getTileEntity(event.getPos());
			
			if(tile != null & tile instanceof IInventory)
			{
				RotHandler.rotInvo(event.getEntityPlayer().world, (IInventory)tile);
			}
		}
	}
		
	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event)
	{
		if(event.isCanceled() || event.getEntityPlayer().world.isRemote)
		{
			return;
		}
		
		if(!ConfigContainer.rotEnabled)
		{
			return;
		}
		
		if(event.getTarget() != null && event.getTarget() instanceof IInventory && ConfigContainer.rotEnabled)
		{
			IInventory chest = (IInventory)event.getTarget();
			
			RotHandler.rotInvo(event.getEntityPlayer().world, chest);
		}
	}
	
	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.getEntityLiving().world.isRemote)
		{
			return;
		}
		
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			InventoryPlayer invo = (InventoryPlayer)((EntityPlayer)event.getEntityLiving()).inventory;
			
			if(ConfigContainer.rotEnabled)
			{
				RotHandler.rotInvo(event.getEntityLiving().world, invo);
			}
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onItemTooltip(ItemTooltipEvent event)
	{
		// MAYBE saw bug once - tooltip not updating properly when in cold chest (% kept decreasing), might need:
		// http://www.minecraftforge.net/forum/topic/62217-1122-tile-entity-update/
		// http://www.minecraftforge.net/forum/topic/42612-solved110-itemhandler-tooltip-when-in-non-player-inventory-on-mp-server/?do=findComment&comment=229718
		
		ItemStack stack = event.getItemStack();
		Entity entity = event.getEntity();
		
		if(ConfigContainer.rotEnabled && (stack != null) && !stack.isEmpty() && (entity != null))
		{
			long time = entity.world.getTotalWorldTime();
			RotHandler.RotTimes rotTimes = RotHandler.getRotTimes(stack, time);
					
			if(rotTimes != null)
			{							
				event.getToolTip().add(
						new TextComponentTranslation(
								"misc.foodfunk.tooltip.rot", 
								rotTimes.getPercent() + "%", 
								rotTimes.getDays(),
								rotTimes.getTime()
							).getUnformattedText());
				//event.toolTip.add("Rotten: 0% (Day " + days + "/" + time + ")");
				//event.toolTip.add("Use-By: Day " + rotTimes.getUseBy());
			}
			
			// TODO: add "cold" or "frozen" to tooltip if in esky or freezer
		}
	}
	
	@SubscribeEvent
	public static void onCrafted(ItemCraftedEvent event) // Prevents exploit of making foods with almost rotten food to prolong total life of food supplies
	{
		if((!ConfigContainer.rotEnabled) || event.player.world.isRemote || event.crafting == null || event.crafting.isEmpty() || event.crafting.getItem() == null)
		{
			return;
		}
		
		RotHandler.handleCraftedRot(event.player.world, event.craftMatrix, event.crafting);
	}
}
