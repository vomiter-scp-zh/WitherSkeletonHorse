package com.vomiter.witherskeletonhorse;

import net.minecraft.resources.ResourceLocation;

public class Helpers {
    public static ResourceLocation id(String namespace, String path){
        return new ResourceLocation(namespace, path);
    }

    public static ResourceLocation id(String path){
        return id(WitherSkeletonHorseMod.MOD_ID, path);
    }
}
