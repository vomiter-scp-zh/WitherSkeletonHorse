package com.vomiter.witherskeletonhorse.common.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    static DeferredRegister<Item> ITEMS
            = ModRegistries.createRegistry(ForgeRegistries.ITEMS);
    public static RegistryObject<SpawnEggItem> WITHER_SKELETON_HORSE_SPAWN_EGG
            = ITEMS.register("wither_skeleton_horse_spawn_egg", () -> new ForgeSpawnEggItem(
                    ModEntityTypes.WITHER_SKELETON_HORSE,
            0xFFFFFF,
            0,
            new Item.Properties()
    ));

    public static void buildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.WITHER_SKELETON_HORSE_SPAWN_EGG);
        }
    }
}
