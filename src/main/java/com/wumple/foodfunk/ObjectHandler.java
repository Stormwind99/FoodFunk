package com.wumple.foodfunk;

import com.wumple.foodfunk.coldchest.esky.BlockEsky;
import com.wumple.foodfunk.coldchest.esky.TileEntityEsky;
import com.wumple.foodfunk.coldchest.esky.TileEntityEskyRenderer;
import com.wumple.foodfunk.coldchest.freezer.BlockFreezer;
import com.wumple.foodfunk.coldchest.freezer.TileEntityFreezer;
import com.wumple.foodfunk.coldchest.freezer.TileEntityFreezerRenderer;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.rottables.BlockMelonRottable;
import com.wumple.foodfunk.rottables.BlockPumpkinRottable;
import com.wumple.foodfunk.rottables.BlockStemCustom;
import com.wumple.foodfunk.rottables.RotTickingTileEntity;
import com.wumple.foodfunk.rottables.TileEntityMelonRottable;
import com.wumple.foodfunk.rottables.TileEntityPumpkinRottable;
import com.wumple.foodfunk.rotten.ItemBiodegradableItem;
import com.wumple.foodfunk.rotten.ItemRottedItem;
import com.wumple.foodfunk.rotten.ItemRottenFood;
import com.wumple.foodfunk.rotten.ItemSpoiledMilk;
import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
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
    
    // @ObjectHolder("foodfunk:rotted_item")
    public static Item rotted_item = null;

    // @ObjectHolder("foodfunk:biodegradable_item")
    public static Item biodegradable_item = null;
    
    // @ObjectHolder("foodfunk:esky")
    public static Item esky_item = null;

    // @ObjectHolder("foodfunk:freezer")
    public static Item freezer_item = null;

    // @ObjectHolder("minecraft:melon_seeds")
    public static Item melon_seeds_item = null;

    // @ObjectHolder("minecraft:pumpkin_seeds")
    public static Item pumpkin_seeds_item = null;
    
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
    
    // Blocks
    public static Block melon_block = null;
    public static Block pumpkin = null;
    public static Block melon_stem = null;
    public static Block pumpkin_stem = null;
    
    // ----------------------------------------------------------------------
    // Ore Dictionary

    protected final static String[] preservers = new String[]{"chest", "preserver"};
    protected final static String[] rottenfoods = new String[]{"food", "rotten", "compostable"};
    protected final static String[] rotteditems = new String[]{"rotten"};
    protected final static String[] rottedbiodegradables = new String[]{"rotten", "compostable"};
    
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

            if (ConfigContainer.rotting.replaceMelons)
            {
                // to make melons have a TileEntity to attach cap to, must replace vanilla MC block :-(
                melon_block = RegistrationHelpers.regHelper(registry, new BlockMelonRottable(), "minecraft:melon_block", false, true);
                pumpkin = RegistrationHelpers.regHelper(registry, new BlockPumpkinRottable(), "minecraft:pumpkin", false, true);

                BlockStemCustom t1 = new BlockStemCustom(melon_block);
                t1.setSoundType(SoundType.WOOD).setHardness(0.0F).setTranslationKey("pumpkinStem");
                melon_stem = RegistrationHelpers.regHelper(registry, t1, "minecraft:melon_stem", false, true);

                BlockStemCustom t2 = new BlockStemCustom(pumpkin);
                t2.setSoundType(SoundType.WOOD).setHardness(0.0F).setTranslationKey("pumpkinStem");
                pumpkin_stem = RegistrationHelpers.regHelper(registry, t2, "minecraft:pumpkin_stem", false, true);
            }
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            final IForgeRegistry<Item> registry = event.getRegistry();

            rotten_food = RegistrationHelpers.regHelperOre(registry, new ItemRottenFood(), rottenfoods);
            spoiled_milk = RegistrationHelpers.regHelperOre(registry, new ItemSpoiledMilk(), rottenfoods);
            rotted_item = RegistrationHelpers.regHelperOre(registry, new ItemRottedItem(), rotteditems);
            biodegradable_item = RegistrationHelpers.regHelperOre(registry, new ItemBiodegradableItem(), rottedbiodegradables);
            
            esky_item = RegistrationHelpers.registerItemBlockOre(registry, esky, preservers);
            freezer_item = RegistrationHelpers.registerItemBlockOre(registry, freezer, preservers);

            if (ConfigContainer.rotting.replaceMelons)
            {
                ItemSeeds s1 = new ItemSeeds(melon_stem, Blocks.FARMLAND);
                s1.setTranslationKey("seeds_melon");
                melon_seeds_item = RegistrationHelpers.regHelper(registry, s1, "minecraft:melon_seeds", false, true);
                ItemSeeds s2 = new ItemSeeds(pumpkin_stem, Blocks.FARMLAND);
                s2.setTranslationKey("seeds_pumpkin");
                pumpkin_seeds_item = RegistrationHelpers.regHelper(registry, s2, "minecraft:pumpkin_seeds", false, true);
            }
            
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
        	RegistrationHelpers.registerTileEntity(RotTickingTileEntity.class, "foodfunk:rottable");
        	
            if (ConfigContainer.rotting.replaceMelons)
            {
                RegistrationHelpers.cheat( () ->
                    {
                	// to make melons have a TileEntity to attach cap to, must create since none in vanilla MC :-(
                	RegistrationHelpers.registerTileEntity(TileEntityMelonRottable.class, "minecraft:melon_block");
                	RegistrationHelpers.registerTileEntity(TileEntityPumpkinRottable.class, "minecraft:pumpkin");
                    } );
            }
        }

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public static void registerRenders(ModelRegistryEvent event)
        {
        	RegistrationHelpers.registerRender(rotten_food);
        	RegistrationHelpers.registerRender(spoiled_milk);
        	RegistrationHelpers.registerRender(rotted_item);
        	RegistrationHelpers.registerRender(biodegradable_item);
        	RegistrationHelpers.registerRender(esky_item);
        	RegistrationHelpers.registerRender(freezer_item);

            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEsky.class, new TileEntityEskyRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFreezer.class, new TileEntityFreezerRenderer());
        }
    }
}
