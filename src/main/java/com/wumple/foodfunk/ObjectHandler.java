package com.wumple.foodfunk;

import com.google.common.collect.ImmutableList;
import com.wumple.foodfunk.coldchest.esky.BlockEsky;
import com.wumple.foodfunk.coldchest.esky.TileEntityEsky;
import com.wumple.foodfunk.coldchest.esky.TileEntityEskyRenderer;
import com.wumple.foodfunk.coldchest.freezer.BlockFreezer;
import com.wumple.foodfunk.coldchest.freezer.TileEntityFreezer;
import com.wumple.foodfunk.coldchest.freezer.TileEntityFreezerRenderer;
import com.wumple.foodfunk.coldchest.icebox.BlockIcebox;
import com.wumple.foodfunk.coldchest.icebox.TileEntityIcebox;
import com.wumple.foodfunk.coldchest.icebox.TileEntityIceboxRenderer;
import com.wumple.foodfunk.coldchest.larder.BlockLarder;
import com.wumple.foodfunk.coldchest.larder.TileEntityLarder;
import com.wumple.foodfunk.coldchest.larder.TileEntityLarderRenderer;
import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.rottables.BlockCarrotRottable;
import com.wumple.foodfunk.rottables.BlockMelonRottable;
import com.wumple.foodfunk.rottables.BlockPotatoRottable;
import com.wumple.foodfunk.rottables.BlockPumpkinRottable;
import com.wumple.foodfunk.rottables.BlockStemCustom;
import com.wumple.foodfunk.rottables.RotTickingTileEntity;
import com.wumple.foodfunk.rottables.TileEntityCarrotRottable;
import com.wumple.foodfunk.rottables.TileEntityMelonRottable;
import com.wumple.foodfunk.rottables.TileEntityPotatoRottable;
import com.wumple.foodfunk.rottables.TileEntityPumpkinRottable;
import com.wumple.foodfunk.rotten.ItemBiodegradableItem;
import com.wumple.foodfunk.rotten.ItemRottedItem;
import com.wumple.foodfunk.rotten.ItemRottenFood;
import com.wumple.foodfunk.rotten.ItemSpoiledMilk;
import com.wumple.util.base.function.Procedure;
import com.wumple.util.misc.RegistrationHelpers;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

// @ObjectHolder("foodfunk")
@Mod.EventBusSubscriber
public class ObjectHandler
{
    // ----------------------------------------------------------------------
    // Blocks

    //@ObjectHolder("foodfunk:esky")
    public static Block esky = null;

    //@ObjectHolder("foodfunk:freezer")
    public static Block freezer = null;

    //@ObjectHolder("foodfunk:larder")
    public static Block larder = null;
    
    //@ObjectHolder("foodfunk:icebox")
    public static Block icebox = null;
    
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

    // @ObjectHolder("foodfunk:larder")
    public static Item larder_item = null;

    // @ObjectHolder("foodfunk:icebox")
    public static Item icebox_item = null;
    
    // @ObjectHolder("minecraft:melon_seeds")
    public static Item melon_seeds_item = null;

    // @ObjectHolder("minecraft:pumpkin_seeds")
    public static Item pumpkin_seeds_item = null;
    
    // @ObjectHolder("minecraft:carrot")
    public static Item carrot_seed_food = null;

    // @ObjectHolder("minecraft:potato")
    public static Item potato_seed_food = null;
    
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

    // @ObjectHolder("foodfunk:larder_open")
    public static SoundEvent larder_open = null;

    // @ObjectHolder("foodfunk:larder_close")
    public static SoundEvent larder_close = null;
    
    // @ObjectHolder("foodfunk:icebox_open")
    public static SoundEvent icebox_open = null;

    // @ObjectHolder("foodfunk:icebox_close")
    public static SoundEvent icebox_close = null;

    
    // Blocks
    public static Block melon_block = null;
    public static Block pumpkin = null;
    public static Block melon_stem = null;
    public static Block pumpkin_stem = null;
    public static Block carrot_block = null;
    public static Block potato_block = null;
    
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
        
        public static boolean shouldReplaceSpecialThings()
        {
            // betterwithmods + applecore + replaceSpecialThings causes strange crash
            if (ConfigContainer.rotting.replaceSpecialThings && Loader.isModLoaded("betterwithmods") && Loader.isModLoaded("applecore"))
            {
                // Attempt to avoid strange crashes like:
                //   http://www.minecraftforge.net/forum/topic/61861-112-nullpointerexception-populating-the-searchtree/
                //   https://paste.dimdev.org/ujukusaneb.mccrash
                //   http://www.minecraftforge.net/forum/topic/58770-112-recipe-registry/
                
                FoodFunk.logger.warn("Disabling replaceSpecialThings to avoid crash with betterwithmods and applecore");
                return false;
            }
            
            return ConfigContainer.rotting.replaceSpecialThings;
        }
        
        public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing)
        {
            assert (thing != null);

            return thing;
        }
        
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            final IForgeRegistry<Block> registry = event.getRegistry();

            esky = regHelper(registry, new BlockEsky());
            freezer = regHelper(registry, new BlockFreezer());
            icebox = regHelper(registry, new BlockLarder());
            larder = regHelper(registry, new BlockIcebox());
            
            ImmutableList.of(
                    esky,
                    freezer,
                    icebox,
                    larder
                    ).forEach(block -> registry.register(block));

            if (shouldReplaceSpecialThings())
            {
                // to make melons have a TileEntity to attach cap to, must replace vanilla MC block :-(
                melon_block = regHelper(registry, new BlockMelonRottable(), "minecraft:melon_block", false, true);
                pumpkin = regHelper(registry, new BlockPumpkinRottable(), "minecraft:pumpkin", false, true);

                Block t1 = new BlockStemCustom(melon_block).setSoundType(SoundType.WOOD).setHardness(0.0F).setTranslationKey("pumpkinStem");
                melon_stem = regHelper(registry, t1, "minecraft:melon_stem", false, true);

                Block t2 = new BlockStemCustom(pumpkin).setSoundType(SoundType.WOOD).setHardness(0.0F).setTranslationKey("pumpkinStem");
                pumpkin_stem = regHelper(registry, t2, "minecraft:pumpkin_stem", false, true);
                
                // same for seed foods
                carrot_block = regHelper(registry, new BlockCarrotRottable(), "minecraft:carrots", false, true);
                potato_block = regHelper(registry, new BlockPotatoRottable(), "minecraft:potatoes", false, true);
                
                ImmutableList.of(
                        melon_block,
                        pumpkin,
                        melon_stem,
                        pumpkin_stem,
                        carrot_block,
                        potato_block
                        ).forEach(block -> registry.register(block));
            }
        }
        
        public static Item registerOreNames(Item thing, String[] oreNames)
        {
            assert (thing != null);

            for (String oreName : oreNames)
            {
                OreDictionary.registerOre(oreName, thing);
            }

            return thing;
        }
        
        public static Item registerOreNames(IForgeRegistry<Item> registry, Item thing, String[] oreNames)
        {
            assert (thing != null);

            return registerOreNames(thing, oreNames);
        }
        
        public static ItemStack registerOreNames(ItemStack thing, String[] oreNames)
        {
            assert (thing != null);

            for (String oreName : oreNames)
            {
                OreDictionary.registerOre(oreName, thing);
            }

            return thing;
        }
        
        public static void registerOreNames(Block block, Item item, String[] oreNames)
        {
            assert (block != null);
            assert (item!= null);

            for (String oreName : oreNames)
            {
                OreDictionary.registerOre(oreName, item);
                OreDictionary.registerOre(oreName, block);
            }
        }
        
        public static ItemBlock registerItemBlock(IForgeRegistry<Item> registry, Block block) //, String[] oreNames)
        {    
            assert (block != null);
            ItemBlock item = new ItemBlock(block);
            RegistrationHelpers.nameHelper(item, block.getRegistryName(), true);

            //registerOreNames(block, item, oreNames);

            return item;
        }
        
        public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, ResourceLocation loc, boolean doTransKey)
        {
            assert (thing != null);
            assert (loc != null);

            thing.setRegistryName(loc);

            if (doTransKey)
            {
                String dotname = loc.getNamespace() + "." + loc.getPath();
                
                if (thing instanceof Block)
                {
                    ((Block) thing).setTranslationKey(dotname);
                }
                else if (thing instanceof Item)
                {
                    ((Item) thing).setTranslationKey(dotname);
                }
            }
        }
        
        public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, ResourceLocation loc)
        {
            nameHelper(thing, loc, true);
        }

        public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, String name, boolean doTransKey)
        {
            assert (thing != null);
            assert (name != null);

            ResourceLocation loc = GameData.checkPrefix(name);
            nameHelper(thing, loc, doTransKey);
        }

        public static <T extends IForgeRegistryEntry<T>> void nameHelper(T thing, String name)
        {
            nameHelper(thing, name, true);
        }
        
        public static void cheat(Procedure proc)
        {
            Loader l = Loader.instance();
            ModContainer k = l.activeModContainer();
            l.setActiveModContainer(l.getMinecraftModContainer());
            proc.run();
            l.setActiveModContainer(k);
        }
        
        public static void run(Procedure proc)
        {
            proc.run();
        }
        
        public static <T extends IForgeRegistryEntry<T>> T regHelper(IForgeRegistry<T> registry, T thing,
                String name, boolean doTransKey, boolean shouldCheat)
        {
            assert (thing != null);
            assert (name != null);
           
            Procedure nameIt = () -> { nameHelper(thing, name, doTransKey); };
            if (shouldCheat) { cheat(nameIt); }
            else { run(nameIt); }

            return thing;
        }
        

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            final IForgeRegistry<Item> registry = event.getRegistry();

            rotten_food = regHelper(registry, new ItemRottenFood()); //, rottenfoods);
            spoiled_milk = regHelper(registry, new ItemSpoiledMilk()); //, rottenfoods);
            rotted_item = regHelper(registry, new ItemRottedItem()); //, rotteditems);
            biodegradable_item = regHelper(registry, new ItemBiodegradableItem()); //, rottedbiodegradables);
            
            esky_item = registerItemBlock(registry, esky); //, preservers);
            freezer_item = registerItemBlock(registry, freezer); //, preservers);
            larder_item = registerItemBlock(registry, larder); //, preservers);
            icebox_item = registerItemBlock(registry, icebox); //, preservers);
            
            ImmutableList.of(
                    rotten_food,
                    spoiled_milk,
                    rotted_item,
                    biodegradable_item,
                    esky_item,
                    freezer_item,
                    larder_item,
                    icebox_item
                    ).forEach(item -> registry.register(item));

            if (shouldReplaceSpecialThings())
            {
                Item s1 = new ItemSeeds(melon_stem, Blocks.FARMLAND).setTranslationKey("seeds_melon");
                melon_seeds_item = regHelper(registry, s1, "minecraft:melon_seeds", false, true);

                Item s2 = new ItemSeeds(pumpkin_stem, Blocks.FARMLAND).setTranslationKey("seeds_pumpkin");
                pumpkin_seeds_item = regHelper(registry, s2, "minecraft:pumpkin_seeds", false, true);
                
                Item s3 = new ItemSeedFood(3, 0.6F, carrot_block, Blocks.FARMLAND).setTranslationKey("carrots");
                carrot_seed_food = regHelper(registry, s3, "minecraft:carrot", false, true);
                
                Item s4 = new ItemSeedFood(1, 0.3F, potato_block, Blocks.FARMLAND).setTranslationKey("potato");
                potato_seed_food = regHelper(registry, s4, "minecraft:potato", false, true);
                
                ImmutableList.of(
                        melon_seeds_item,
                        pumpkin_seeds_item,
                        carrot_seed_food,
                        potato_seed_food
                        ).forEach(item -> registry.register(item));
            }

            registerTileEntities();
            
            // registerMoreOreNames();
        }

        @SubscribeEvent
        public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event)
        {
            final IForgeRegistry<SoundEvent> registry = event.getRegistry();

            esky_open = RegistrationHelpers.registerSound(registry, "foodfunk:esky_open");
            esky_close = RegistrationHelpers.registerSound(registry, "foodfunk:esky_close");
            freezer_open = RegistrationHelpers.registerSound(registry, "foodfunk:freezer_open");
            freezer_close = RegistrationHelpers.registerSound(registry, "foodfunk:freezer_close");
            larder_open = RegistrationHelpers.registerSound(registry, "foodfunk:larder_open");
            larder_close = RegistrationHelpers.registerSound(registry, "foodfunk:larder_close");
            icebox_open = RegistrationHelpers.registerSound(registry, "foodfunk:icebox_open");
            icebox_close = RegistrationHelpers.registerSound(registry, "foodfunk:icebox_close");
        }

        public static void registerTileEntities()
        {
        	RegistrationHelpers.registerTileEntity(TileEntityEsky.class, "foodfunk:esky");
        	RegistrationHelpers.registerTileEntity(TileEntityFreezer.class, "foodfunk:freezer");
            RegistrationHelpers.registerTileEntity(TileEntityLarder.class, "foodfunk:larder");
            RegistrationHelpers.registerTileEntity(TileEntityIcebox.class, "foodfunk:icebox");
        	RegistrationHelpers.registerTileEntity(RotTickingTileEntity.class, "foodfunk:rottable");
        	
            if (shouldReplaceSpecialThings())
            {
                RegistrationHelpers.cheat( () ->
                    {
                	// to make melons have a TileEntity to attach cap to, must create since none in vanilla MC :-(
                	RegistrationHelpers.registerTileEntity(TileEntityMelonRottable.class, "minecraft:melon_block");
                	RegistrationHelpers.registerTileEntity(TileEntityPumpkinRottable.class, "minecraft:pumpkin");
                	RegistrationHelpers.registerTileEntity(TileEntityCarrotRottable.class, "minecraft:carrot");
                	RegistrationHelpers.registerTileEntity(TileEntityPotatoRottable.class, "minecraft:potato");
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
        	RegistrationHelpers.registerRender(larder_item);
        	RegistrationHelpers.registerRender(icebox_item);

            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEsky.class, new TileEntityEskyRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFreezer.class, new TileEntityFreezerRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLarder.class, new TileEntityLarderRenderer());
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIcebox.class, new TileEntityIceboxRenderer());
        }
        
        // ----------------------------------------------------------------------
        // Ore Dictionary

        public static class Ids
        {
            public static final String listAllMetalIngots = "listAllmetalingots";
            public static final String listAllMetalBlocks = "listAllmetalblocks";
            public static final String listAllMelons = "listAllmelons";
            public static final String listAllSeedFoods = "listAllseedfoods";
            public static final String listAllSeed="listAllseed";
        }
        
        public static void registerMoreOreNames()
        {
            registerOreNames(rotten_food, rottenfoods);
            registerOreNames(spoiled_milk, rottenfoods);
            registerOreNames(rotted_item, rotteditems);
            registerOreNames(biodegradable_item, rottedbiodegradables);
            
            registerOreNames(esky, esky_item, preservers);
            registerOreNames(freezer, freezer_item, preservers);
            registerOreNames(larder, larder_item, preservers);
            registerOreNames(icebox, icebox_item, preservers);
            
            // MORE
            OreDictionary.registerOre(Ids.listAllMetalIngots, Items.IRON_INGOT);
            OreDictionary.registerOre(Ids.listAllMetalIngots, Items.GOLD_INGOT);
            // TODO: ingotCopper, other metal ingots
            OreDictionary.registerOre(Ids.listAllMetalBlocks, Blocks.IRON_BLOCK);
            OreDictionary.registerOre(Ids.listAllMetalBlocks, Blocks.GOLD_BLOCK);
            // TODO: blockCopper, other metal ingots
            
            if (shouldReplaceSpecialThings())
            {
                OreDictionary.registerOre(Ids.listAllMelons, melon_block);
                OreDictionary.registerOre(Ids.listAllMelons, pumpkin);
                
                OreDictionary.registerOre(Ids.listAllSeed, melon_seeds_item);
                OreDictionary.registerOre(Ids.listAllSeed, pumpkin_seeds_item);
                
                //OreDictionary.registerOre(Ids.listAllSeedFoods, new ItemStack(carrot_block, 1, OreDictionary.WILDCARD_VALUE));
                //OreDictionary.registerOre(Ids.listAllSeedFoods, new ItemStack(potato_block, 1, OreDictionary.WILDCARD_VALUE));
                OreDictionary.registerOre(Ids.listAllSeedFoods, carrot_seed_food);
                OreDictionary.registerOre(Ids.listAllSeedFoods, potato_seed_food);
            }
        }
    }
}

// Maybe replace to make planted version rottable:
/*
WHEAT_SEEDS = getRegisteredItem("wheat_seeds");
WHEAT = getRegisteredItem("wheat");
WHEAT = getRegisteredBlock("wheat");

BEETROOT_SEEDS = getRegisteredItem("beetroot_seeds");
BEETROOT = getRegisteredItem("beetroot");
BEETROOTS = getRegisteredBlock("beetroots");
 */
