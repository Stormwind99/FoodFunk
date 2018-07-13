package com.wumple.util;

public class Util
{
    public static <T> T as(Object o, Class<T> t)
    {
        return t.isInstance(o) ? t.cast(o) : null;
    }
    
    public static <T> T getValueOrDefault(T value, T defaultValue)
    {
        return value == null ? defaultValue : value;
    }
    
    public static boolean checkBoth(boolean one, boolean two)
    {
    	boolean ret = true;
        ret &= one;
        ret &= two;
        return ret;
    }
}
