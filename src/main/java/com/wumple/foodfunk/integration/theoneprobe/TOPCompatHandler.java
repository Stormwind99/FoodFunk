package com.wumple.foodfunk.integration.theoneprobe;

import net.minecraftforge.fml.common.Loader;

public class TOPCompatHandler
{
    public static void register()
    {
        if (Loader.isModLoaded("theoneprobe"))
        {
            TOPCompatibility.register();
        }
    }

}
