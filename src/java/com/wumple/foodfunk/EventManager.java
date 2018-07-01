package com.wumple.foodfunk;

import com.wumple.foodfunk.capabilities.preserving.Preserving;
import com.wumple.foodfunk.capabilities.rot.IRot;
import com.wumple.foodfunk.capabilities.rot.Rot;
import com.wumple.foodfunk.capabilities.rot.RotHelper;
import com.wumple.foodfunk.configuration.ConfigContainer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
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
	    RotHandler.rot(event.getWorld(), event.getEntity());
	}
	
	@SubscribeEvent
	public static void onEntityItemPickup(EntityItemPickupEvent event)
	{
	    RotHandler.rot(event.getEntity().getEntityWorld(), event.getEntity());
	}

	@SubscribeEvent
	public static void onPlayerInteract(PlayerInteractEvent event)
	{
		if(event instanceof RightClickBlock)
		{
			TileEntity tile = event.getEntityPlayer().world.getTileEntity(event.getPos());

			RotHandler.rot(event.getEntityPlayer().world, tile);
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event)
	{
		if(event.isCanceled())
		{
			return;
		}

		RotHandler.rot(event.getEntityPlayer().world, event.getTarget());
	}

	@SubscribeEvent
	public static void onPlayerContainerOpen(PlayerContainerEvent.Open event)
	{
        if(event.isCanceled())
        {
            return;
        }
            
        RotHandler.rot(event.getEntityPlayer().world, event.getContainer());
	}
	
	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event)
	{		
	    EntityLivingBase entity = event.getEntityLiving();
	    
		if (entity.ticksExisted % Preserving.slowInterval  == 0)
		{
		    RotHandler.rot(event.getEntityLiving().world, entity);
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
			    String key = null;
			    
				if (rotTimes.isSet() && !beingCrafted)
				{				    
					if (rotTimes.getPercent() >= 100)
					{
					    key = "misc.foodfunk.tooltip.decaying";					}
					else
					{
						key = "misc.foodfunk.tooltip.rot";
					}
				}
				else if (rotTimes.isNoRot())
				{
					key = "misc.foodfunk.tooltip.preserved";					
				}
				else if (rotTimes.time > 0)
				{
					key = "misc.foodfunk.tooltip.fresh";
				}
				
                if (key != null)
                {
                    event.getToolTip().add(
                        new TextComponentTranslation( key, 
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
