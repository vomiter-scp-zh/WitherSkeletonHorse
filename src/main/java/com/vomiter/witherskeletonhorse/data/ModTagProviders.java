package com.vomiter.witherskeletonhorse.data;

import com.vomiter.witherskeletonhorse.Helpers;
import com.vomiter.witherskeletonhorse.WitherSkeletonHorseMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModTagProviders {
    DataGenerator generator;
    PackOutput output;
    CompletableFuture<HolderLookup.Provider> lookupProvider;
    ExistingFileHelper helper;

    public ModTagProviders(GatherDataEvent event){
        generator = event.getGenerator();
        output = generator.getPackOutput();
        lookupProvider = event.getLookupProvider();
        helper = event.getExistingFileHelper();

        var blockTags = new BlockTags();
        var itemTags = new ItemTags(blockTags);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), itemTags);

    }


    class BlockTags extends BlockTagsProvider{

        public BlockTags() {
            super(output, lookupProvider, WitherSkeletonHorseMod.MOD_ID, helper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider lookupProvider) {

        }
    }

    class ItemTags extends ItemTagsProvider {

        public ItemTags(BlockTags blockTags) {
            super(output, lookupProvider, blockTags.contentsGetter(), WitherSkeletonHorseMod.MOD_ID, helper);
        }
        static TagKey<Item> create(ResourceLocation id){
            return TagKey.create(
                    Registries.ITEM,
                    id
            );
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider p_256380_) {
            tag(ModTags.SUMMON_WITHER_TRAP)
                    .add(Items.TOTEM_OF_UNDYING)
                    .add(Items.ENCHANTED_GOLDEN_APPLE)
                    .add(Items.NETHER_STAR)
                    .add(Items.DRAGON_EGG);
        }
    }


}
