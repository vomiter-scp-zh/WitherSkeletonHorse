package com.vomiter.witherskeletonhorse;

import com.mojang.logging.LogUtils;
import com.vomiter.witherskeletonhorse.client.ClientEventHandler;
import com.vomiter.witherskeletonhorse.common.event.EventHandler;
import com.vomiter.witherskeletonhorse.common.registry.ModItems;
import com.vomiter.witherskeletonhorse.common.registry.ModRegistries;
import com.vomiter.witherskeletonhorse.data.ModDataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(WitherSkeletonHorseMod.MOD_ID)
public class WitherSkeletonHorseMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "witherskeletonhorse";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation modLoc(String path){
        return Helpers.id(WitherSkeletonHorseMod.MOD_ID, path);
    }

    public WitherSkeletonHorseMod(FMLJavaModLoadingContext context) {
        EventHandler.init();
        IEventBus modBus = context.getModEventBus();
        modBus.addListener(this::commonSetup);
        modBus.addListener(ModDataGenerator::generateData);
        modBus.addListener(ModItems::buildCreativeTabContents);
        ModRegistries.register(modBus);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if(FMLEnvironment.dist.isClient()){
            ClientEventHandler.init(modBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

}
