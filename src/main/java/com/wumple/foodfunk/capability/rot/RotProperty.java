package com.wumple.foodfunk.capability.rot;

import java.util.Random;

import com.wumple.foodfunk.configuration.ConfigContainer;
import com.wumple.foodfunk.configuration.ConfigHandler;
import com.wumple.util.config.MatchingConfig;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class RotProperty extends MatchingConfig.Identifier
{
	protected static Random random = new Random();
    public String key;
    public int days = ConfigHandler.DAYS_NO_ROT;

    public RotProperty()
    {
    	super();
    }
    
    public RotProperty(String _key, String _rotID, int _days)
    {
    	super(_rotID);
    	key = _key;
        days = _days;
    }

    public long getRotTime()
    {
        if (ConfigContainer.zdebugging.debug)
        {
            double ticksPerDay = ConfigContainer.zdebugging.rotMultiplier * ConfigHandler.TICKS_PER_DAY;
            return days * (long) ticksPerDay;
        }

        return days * ConfigHandler.TICKS_PER_DAY;
    }

    public boolean doesRot()
    {
        return (days > ConfigHandler.DAYS_NO_ROT);
    }
    
    protected ItemStack forceRot(ItemStack stack)
    {
        if (id.isEmpty())
        {
            return ItemStack.EMPTY;
        }

        Item item = Item.REGISTRY.getObject(new ResourceLocation(id));

        if (item == null)
        {
            NonNullList<ItemStack> ores = OreDictionary.getOres(id);
            if (!ores.isEmpty())
            {
                ItemStack choice = ores.get(random.nextInt(ores.size()));
                return choice.copy();
            }
        }

        if (item == null)
        {
            return ItemStack.EMPTY;
        }

        return (meta == null) ? new ItemStack(item, stack.getCount())
                : new ItemStack(item, stack.getCount(), meta.intValue());
    }
}
