package com.wumple.foodfunk;

import com.wumple.foodfunk.capabilities.preserving.Preserving;
import com.wumple.foodfunk.capabilities.rot.IRot;
import com.wumple.foodfunk.capabilities.rot.Rot;
import com.wumple.foodfunk.capabilities.rot.RotHelper;
import com.wumple.foodfunk.configuration.ConfigContainer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EventManager
{
	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event)
	{		
		if(ConfigContainer.enabled && !event.getWorld().isRemote)
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
	public static void onEntityItemPickup(EntityItemPickupEvent event)
	{
	    World world = event.getEntity().getEntityWorld();
        if(ConfigContainer.enabled && !world.isRemote)
        {
            if (event.getEntity() instanceof EntityItem)
            {
                EntityItem item = (EntityItem)event.getEntity();
                
                ItemStack rotStack = RotHandler.doRot(world, item.getItem());

                if(item.getItem() != rotStack)
                {
                    item.setItem(rotStack);
                }
            } 
        }	
	}


	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		if(ConfigContainer.enabled && event instanceof RightClickBlock && !event.getWorld().isRemote)
		{
			TileEntity tile = event.getEntityPlayer().world.getTileEntity(event.getPos());

			if ((tile != null) && (tile instanceof IInventory))
			{
			    IInventory invo = (IInventory)tile;
			    
				RotHandler.rotInvo(event.getEntityPlayer().world, invo);
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

		if(!ConfigContainer.enabled)
		{
			return;
		}

		if (event.getTarget() instanceof IInventory && ConfigContainer.enabled)
		{
			IInventory invo = (IInventory)event.getTarget();

			RotHandler.rotInvo(event.getEntityPlayer().world, invo);
		}
	}

	@SubscribeEvent
	public static void onPlayerContainerOpen(PlayerContainerEvent.Open event)
	{
        if(event.isCanceled() || event.getEntityPlayer().world.isRemote)
        {
            return;
        }

        if(!ConfigContainer.enabled)
        {
            return;
        }

        if(event.getContainer() instanceof IInventory && ConfigContainer.enabled)
        {
            IInventory invo = (IInventory)event.getContainer();
            RotHandler.rotInvo(event.getEntityPlayer().world, invo);
        }	    
	}
	
	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.getEntityLiving().world.isRemote)
		{
			return;
		}
		
		if (event.getEntityLiving().ticksExisted % Preserving.slowInterval  == 0)
		{
			if(event.getEntityLiving() instanceof EntityPlayer)
			{
				InventoryPlayer invo = (InventoryPlayer)((EntityPlayer)event.getEntityLiving()).inventory;
	
				if(ConfigContainer.enabled)
				{
					RotHandler.rotInvo(event.getEntityLiving().world, invo);
				}
			}
		}
	}
	
	public static boolean isItemBeingCraftedBy(ItemStack stack, Entity entity)
	{
        boolean beingCrafted = false;

        EntityPlayer player = (EntityPlayer)(entity);
        if (player != null)
        {
            if (player.openContainer != null)
            {
                Slot slot = player.openContainer.getSlot(0);
                if ((slot != null) && (slot instanceof SlotCrafting) && (slot.getStack() == stack))
                {
                    beingCrafted = true;
                }
            }
        }

        return beingCrafted;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onItemTooltip(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		Entity entity = event.getEntity();

		if(ConfigContainer.enabled && (stack != null) && !stack.isEmpty() && (entity != null))
		{
			long curTime = entity.world.getTotalWorldTime();
			
			IRot rot = RotHelper.getRot(stack);
						
			RotHandler.RotTimes rotTimes = RotHandler.getRotTimes(rot, curTime);
			
			if(rotTimes != null)
			{	
			    boolean beingCrafted = isItemBeingCraftedBy(stack, entity);
			    
				if (rotTimes.isSet() && !beingCrafted)
				{
					if (rotTimes.getPercent() >= 100)
					{
						event.getToolTip().add(
								new TextComponentTranslation(
										"misc.foodfunk.tooltip.decaying", 
										rotTimes.getPercent() + "%", 
										rotTimes.getDaysLeft(),
										rotTimes.getDaysTotal()
										).getUnformattedText());
					}
					else
					{
						event.getToolTip().add(
								new TextComponentTranslation(
										"misc.foodfunk.tooltip.rot", 
										rotTimes.getPercent() + "%", 
										rotTimes.getDaysLeft(),
										rotTimes.getDaysTotal()
										).getUnformattedText());
					}
				}
				else if (rotTimes.isNoRot())
				{
					event.getToolTip().add(
							new TextComponentTranslation(
									"misc.foodfunk.tooltip.preserved", 
									rotTimes.getPercent() + "%", 
									rotTimes.getDaysLeft(),
									rotTimes.getDaysTotal()
									).getUnformattedText());
					
				}
				else if (rotTimes.time > 0)
				{
					event.getToolTip().add(
							new TextComponentTranslation(
									"misc.foodfunk.tooltip.fresh", 
									rotTimes.getPercent() + "%", 
									rotTimes.getDaysLeft(),
									rotTimes.getDaysTotal()
									).getUnformattedText());
				}
				
				if ( event.getFlags().isAdvanced() && ConfigContainer.zdebugging.debug )
				{
					event.getToolTip().add(
							new TextComponentTranslation(
									"misc.foodfunk.tooltip.advanced.datetime", 
									rotTimes.getDate(),
									rotTimes.getTime()
									).getUnformattedText() );
					event.getToolTip().add(
							new TextComponentTranslation(
									"misc.foodfunk.tooltip.advanced.expire", 
									rotTimes.getCurTime(),
									rotTimes.getExpirationTimestamp()
									).getUnformattedText() );
				}
			}			

			// TODO: add "cold" or "frozen" to tooltip if in esky or freezer
		}
	}

	@SubscribeEvent
	public static void onCrafted(ItemCraftedEvent event) // Prevents exploit of making foods with almost rotten food to prolong total life of food supplies
	{
		if((!ConfigContainer.enabled) || event.player.world.isRemote || event.crafting == null || event.crafting.isEmpty() || event.crafting.getItem() == null)
		{
			return;
		}

		RotHandler.handleCraftedRot(event.player.world, event.craftMatrix, event.crafting);
	}
	
	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event)
	{
	    long timestamp = event.world.getTotalWorldTime();
	    Rot.lastWorldTimestamp = timestamp;
	}
}
