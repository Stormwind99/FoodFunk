package com.wumple.foodfunk;

import org.apache.logging.log4j.Logger;

import com.wumple.foodfunk.capabilities.preserving.Preserving;
import com.wumple.foodfunk.capabilities.rot.Rot;
import com.wumple.foodfunk.configuration.ConfigHandler;

import choonster.capability.foodfunk.Messages;
import choonster.proxy.ISidedProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/*
 * FoodFunk mod - food rots over time (and also supports generic item rotting)
 * Originally based on food rot from old discontinued EnviroMine mod - thanks to the authors!
 */ 
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.DEPENDENCIES, updateJSON=Reference.UPDATEJSON)
public class FoodFunk {
	@Mod.Instance(Reference.MOD_ID)
	public static FoodFunk instance;
	
    @SidedProxy(clientSide = "choonster.proxy.CombinedClientProxy", serverSide = "choonster.proxy.DedicatedServerProxy")
    public static ISidedProxy proxy;
	
	public static Logger logger;
	public static SimpleNetworkWrapper network;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	    logger = event.getModLog();
	    
	    Rot.register();
        Preserving.register();
        
		network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
		Messages.register();
		
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// add any missing default config rot properties
		ConfigHandler.init();
		proxy.postInit();
	}
}