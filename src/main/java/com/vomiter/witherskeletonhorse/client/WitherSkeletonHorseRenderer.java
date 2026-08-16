package com.vomiter.witherskeletonhorse.client;

import com.vomiter.witherskeletonhorse.Helpers;
import com.vomiter.witherskeletonhorse.common.registry.ModEntityTypes;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.layers.HorseArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraftforge.client.event.EntityRenderersEvent;
import org.jetbrains.annotations.NotNull;

public class WitherSkeletonHorseRenderer extends UndeadHorseRenderer {
    public WitherSkeletonHorseRenderer(EntityRendererProvider.Context p_174432_, ModelLayerLocation p_174433_) {
        super(p_174432_, p_174433_);
        this.addLayer(new CustomHorseArmorLayer(this, p_174432_.getModelSet()));

    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull AbstractHorse p_116274_) {
        return Helpers.id("textures/entity/wither_skeleton_horse.png");
    }

    public static void register(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(
                ModEntityTypes.WITHER_SKELETON_HORSE.get(),
                (ctx) -> new WitherSkeletonHorseRenderer(ctx, ModelLayers.SKELETON_HORSE)
        );
    }

}
