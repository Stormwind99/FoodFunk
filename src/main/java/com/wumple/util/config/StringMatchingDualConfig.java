package com.wumple.util.config;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class StringMatchingDualConfig<U> extends DualMatchingConfig<String, U>
{

	public StringMatchingDualConfig(Map<String, String> config1In, String falseValue1In, Map<String, U> config2In,
			U falseValue2In)
	{
		super(config1In, falseValue1In, config2In, falseValue2In);
	}
	
	@Nullable
	protected static String itemToString(@Nullable Item item, @Nullable String backup)
	{
        String id = backup;

        if (item != null)
        {
            ResourceLocation resLoc = Item.REGISTRY.getNameForObject(item);
            if (resLoc != null)
            {
                id = resLoc.toString();
            }
        }

        return id;
	}

    public boolean addDefaultProperty(String name, @Nullable Item amount1In, U amount2In)
    {
        return addDefaultProperty(name, itemToString(amount1In, null), amount2In);
    }
    
    public boolean addDefaultProperty(Item item, String backup, @Nullable Item amount1In, U amount2In)
    {
          return addDefaultProperty(item, backup, itemToString(amount1In, null), amount2In);
    }
}
