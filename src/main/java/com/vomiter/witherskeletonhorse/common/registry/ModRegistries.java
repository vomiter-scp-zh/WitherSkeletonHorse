package com.vomiter.witherskeletonhorse.common.registry;

import com.vomiter.witherskeletonhorse.WitherSkeletonHorseMod;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class ModRegistries {
    static List<DeferredRegister<?>> REGISTRIES = new ArrayList<>();
    public static <B> DeferredRegister<B> createRegistry(IForgeRegistry<B> b){
        return DeferredRegister.create(b, WitherSkeletonHorseMod.MOD_ID);
    }
    public static <B> DeferredRegister<B> createRegistry(ResourceKey b){
        return DeferredRegister.create(b, WitherSkeletonHorseMod.MOD_ID);
    }

    static void add(DeferredRegister<?> r){
        REGISTRIES.add(r);
    }

    public static void register(IEventBus modBus){
        add(ModItems.ITEMS);
        add(ModBlocks.BLOCKS);
        add(ModEntityTypes.ENTITY_TYPES);
        modBus.addListener(ModEntityTypes::onEntityAttributes);
        for (DeferredRegister<?> registry : REGISTRIES) {
            registry.register(modBus);
        }
    }
}
