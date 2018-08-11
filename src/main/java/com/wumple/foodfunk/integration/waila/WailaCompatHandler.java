package com.wumple.foodfunk.integration.waila;

import net.minecraftforge.fml.common.Loader;

public class WailaCompatHandler
{
    public static void register()
    {
        if (Loader.isModLoaded("Waila"))
        {
            WailaCompatibility.register();
        }
    }
}
