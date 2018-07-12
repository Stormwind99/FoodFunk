package com.wumple.foodfunk;

import com.wumple.foodfunk.coldchest.esky.BlockEsky;
import com.wumple.foodfunk.coldchest.esky.TileEntityEsky;
import com.wumple.foodfunk.coldchest.esky.TileEntityEskyRenderer;
import com.wumple.foodfunk.coldchest.freezer.BlockFreezer;
import com.wumple.foodfunk.coldchest.freezer.TileEntityFreezer;
import com.wumple.foodfunk.coldchest.freezer.TileEntityFreezerRenderer;
import com.wumple.foodfunk.rotten.ItemRottenFood;
import com.wumple.foodfunk.rotten.ItemSpoiledMilk;
import com.wumple.util.RegistrationHelpers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;


@ObjectHolder("foodfunk")
public class ObjectHandler
{
    // ----------------------------------------------------------------------
    // Blocks

    @ObjectHolder("foodfunk:esky")
    public static final Block esky = null;

    @ObjectHolder("foodfunk:freezer")
    public static final Block freezer = null;

    // ----------------------------------------------------------------------
    // Items

    // @ObjectHolder("foodfunk:rotten_food")
    public static Item rotten_food = null;

    // @ObjectHolder("foodfunk:spoiled_milk")
    public static Item spoiled_milk = null;

    // @ObjectHolder("foodfunk:esky")
    public static Item esky_item = null;

    // @ObjectHolder("foodfunk:freezer")
    public static Item freezer_item = null;

    // ----------------------------------------------------------------------
    // SoundEvents

    // @ObjectHolder("foodfunk:esky_open")
    public static SoundEvent esky_open = null;

    // @ObjectHolder("foodfunk:esky_close")
    public static SoundEvent esky_close = null;

    // @ObjectHolder("foodfunk:freezer_open")
    public static SoundEvent freezer_open = null;

    // @ObjectHolder("foodfunk:freezer_close")
    public static SoundEvent freezer_close = null;

    // TODO
    // @ObjectHolder("foodfunk:rotted_item")
    // public static final Item rotted_item = null;

    // ----------------------------------------------------------------------
    // Events

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            final IForgeRegistry<Block> registry = event.getRegistry();

            RegistrationHelpers.regHelper(registry, new BlockEsky());
            RegistrationHelpers.regHelper(registry, new BlockFreezer());
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            final IForgeRegistry<Item> registry = event.getRegistry();

            rotten_food = RegistrationHelpers.regHelper(registry, new ItemRottenFood());
            spoiled_milk = RegistrationHelpers.regHelper(registry, new ItemSpoiledMilk());

            esky_item = RegistrationHelpers.registerItemBlock(registry, esky);
            freezer_item = RegistrationHelpers.registerItemBlock(registry, freezer);

            registerTileEntities();
        }

        @SubscribeEvent
        public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event)
        {
            final IForgeRegistry<SoundEvent> registry = event.getRegistry();

            esky_open = RegistrationHelpers.registerSound(registry, "foodfunk:esky_open");
            esky_close = RegistrationHelpers.registerSound(registry, "foodfunk:esky_close");
            freezer_open = RegistrationHelpers.registerSound(registry, "foodfunk:freezer_open");
            freezer_close = RegistrationHelpers.registerSound(registry, "foodfunk:freezer_close");
        }

        public static void registerTileEntities()
        {
        	RegistrationHelpers.registerTileEntity(TileEntityEsky.class, "foodfunk:esky");
        	RegistrationHelpers.registerTileEntity(TileEntityFreezer.class, "foodfunk:freezer");
        }

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public static void registerRenders(ModelRegistryEvent event)
        {
        	RegistrationHelpers.registerRender(rotten_food);
        	RegistrationHelpers.registerRender(spoiled_milk);
        	RegistrationHelpers.registerRender(esky_item);
        	RegistrationHelpers.registerRender(freezer_item);

            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEsky.class, new TileEntityEskyRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFreezer.class, new TileEntityFreezerRenderer());
        }
    }
}
