package com.wumple.foodfunk;

import com.wumple.foodfunk.Reference;
import com.wumple.foodfunk.capability.preserving.Preserving;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
public class EventHandler
{
    /**
     * Attach the {@link IRot} capability to vanilla items.
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (ConfigHandler.Rotting.doesRot(stack)) {
            event.addCapability(RotProvider.ID, RotCapHelper.createProvider(stack));
        }
    }
    
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event)
    {		
        RotHandler.evaluateRot(event.getWorld(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEntityItemPickup(EntityItemPickupEvent event)
    {
        RotHandler.evaluateRot(event.getEntity().getEntityWorld(), event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event instanceof RightClickBlock)
        {
            TileEntity tile = event.getEntityPlayer().world.getTileEntity(event.getPos());

            RotHandler.evaluateRot(event.getEntityPlayer().world, tile);
        }
    }

    // might duplicate onPlayerInteract - remove if so
    @SubscribeEvent
    public static void onEntityInteract(EntityInteract event)
    {
        // think it is safe to rot even if (event.isCanceled())
        RotHandler.evaluateRot(event.getEntityPlayer().world, event.getTarget());
    }

    // likely duplicates onPlayerInteract - remove if so
    @SubscribeEvent
    public static void onPlayerContainerOpen(PlayerContainerEvent.Open event)
    {
        // think it is safe to rot even if (event.isCanceled())
        RotHandler.evaluateRot(event.getEntityPlayer().world, event.getContainer());
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingUpdateEvent event)
    {		
        EntityLivingBase entity = event.getEntityLiving();

        if (entity.ticksExisted % Preserving.slowInterval  == 0)
        {
            RotHandler.evaluateRot(event.getEntityLiving().world, entity);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        IRot rot = (stack != null) ? RotCapHelper.getRot(stack) : null;
        
        if (rot != null)
        {
            rot.doTooltip(stack, event.getEntity(), event.getFlags().isAdvanced(), event.getToolTip());
        }
    }
        
    @SubscribeEvent
    public static void onCrafted(ItemCraftedEvent event) // Prevents exploit of making foods with almost rotten food to prolong total life of food supplies
    {
        if((!ConfigContainer.enabled) || event.player.world.isRemote || event.crafting == null || event.crafting.isEmpty() || event.crafting.getItem() == null)
        {
            return;
        }

        RotHelper.handleCraftedRot(event.player.world, event.craftMatrix, event.crafting);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event)
    {
        long timestamp = event.world.getTotalWorldTime();
        Rot.setLastWorldTimestamp(timestamp);
    }
}
