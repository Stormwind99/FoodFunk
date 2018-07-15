package com.wumple.util;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class TypeIdentifier
{
	protected static Random random = new Random();
    public String id = null;
    public Integer meta = null;

    public TypeIdentifier()
    {
    }

    public TypeIdentifier(String idIn)
    {
        setID(idIn);
    }
    
    public TypeIdentifier(String idIn, Integer metaIn)
    {
        setID(idIn);
        meta = metaIn;
    }

    public void setID(String key)
    {
        // metadata support - class:name@metadata
        int length = (key != null) ? key.length() : 0;
        if ((length >= 2) && (key.charAt(length - 2) == '@'))
        {
            String metastring = key.substring(length - 1);
            meta = Integer.valueOf(metastring);
            id = key.substring(0, length - 2);
        }
        else if ((length >= 3) && (key.charAt(length - 3) == '@'))
        {
            String metastring = key.substring(length - 2);
            meta = Integer.valueOf(metastring);
            id = key.substring(0, length - 3);
        }
        else
        {
            id = key;
            meta = null;
        }
    }
    
    protected ItemStack create(int count)
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

        return (meta == null) ? new ItemStack(item, count)
                : new ItemStack(item, count, meta.intValue());
    }
}