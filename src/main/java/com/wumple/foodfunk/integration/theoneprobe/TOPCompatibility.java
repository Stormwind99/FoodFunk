package com.wumple.foodfunk.integration.theoneprobe;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TOPCompatibility
{
    private static boolean registered;

    public static void register()
    {
        if (registered) { return; }
        registered = true;
        FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "com.wumple.foodfunk.integration.theoneprobe.TOPProvider");
    }
}