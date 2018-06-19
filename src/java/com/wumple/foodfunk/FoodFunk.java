package com.wumple.foodfunk;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/*
 * FoodFunk mod - food rots over time (and also supports generic item rotting)
 * Originally based on food rot from old discontinued EnviroMine mod - thanks to the authors!
 */ 
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.DEPENDENCIES, updateJSON=Reference.UPDATEJSON)
public class FoodFunk {
    @Mod.Instance(Reference.MOD_ID)
    public static FoodFunk instance;
	public static Logger logger;
	
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	// add any missing default config rot properties
    	ConfigHandler.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new com.wumple.foodfunk.EventManager());
    }
}