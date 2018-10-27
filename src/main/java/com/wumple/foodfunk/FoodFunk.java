package com.wumple.foodfunk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wumple.foodfunk.capability.Messages;
import com.wumple.foodfunk.capability.preserving.Preserving;
import com.wumple.foodfunk.capability.rot.Rot;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.foodfunk.integration.theoneprobe.TOPCompatHandler;
import com.wumple.foodfunk.integration.waila.WailaCompatHandler;
import com.wumple.util.mod.ModBase;
import com.wumple.util.proxy.ISidedProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/*
 * FoodFunk mod - food rots over time (and also supports generic item rotting)
 * Inspired by the food rot feature from old discontinued EnviroMine mod - thanks to the authors!
 */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.MOD_VERSION, dependencies = Reference.DEPENDENCIES, updateJSON = Reference.UPDATEJSON, certificateFingerprint=Reference.FINGERPRINT)
public class FoodFunk extends ModBase
{
    @Mod.Instance(Reference.MOD_ID)
    public static FoodFunk instance;

    @SidedProxy(clientSide = "com.wumple.util.proxy.CombinedClientProxy", serverSide = "com.wumple.util.proxy.DedicatedServerProxy")
    public static ISidedProxy proxy;

    public static SimpleNetworkWrapper network;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        
        Rot.register();
        Preserving.register();

        TOPCompatHandler.register();
        WailaCompatHandler.register();

        network = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);
        Messages.register();

        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        ObjectHandler.RegistrationHandler.registerMoreOreNames();
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        // add any missing default config rot properties
        ConfigHandler.init();
        proxy.postInit(event);
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        super.onFingerprintViolation(event);
    }
    
    @Override
    public Logger getLoggerFromManager()
    {
        return LogManager.getLogger(Reference.MOD_ID);
    }
}
