package com.vomiter.witherskeletonhorse.data;

import com.vomiter.witherskeletonhorse.Helpers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static TagKey<Item> SUMMON_WITHER_TRAP = ModTagProviders.ItemTags.create(Helpers.id("summon_wither_trap"));
}
