package com.vomiter.witherskeletonhorse.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.DyeableHorseArmorItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class CustomHorseArmorLayer extends RenderLayer<AbstractHorse, HorseModel<AbstractHorse>> {
    private final HorseModel<AbstractHorse> model;

    public CustomHorseArmorLayer(RenderLayerParent<AbstractHorse, HorseModel<AbstractHorse>> p_174496_, EntityModelSet p_174497_) {
        super(p_174496_);
        this.model = new HorseModel<>(p_174497_.bakeLayer(ModelLayers.HORSE_ARMOR));
    }

    public void render(PoseStack p_117032_, MultiBufferSource p_117033_, int p_117034_, AbstractHorse p_117035_, float p_117036_, float p_117037_, float p_117038_, float p_117039_, float p_117040_, float p_117041_) {
        ItemStack $$10 = p_117035_.getItemBySlot(EquipmentSlot.CHEST);
        if ($$10.getItem() instanceof HorseArmorItem horseArmorItem) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(p_117035_, p_117036_, p_117037_, p_117038_);
            this.model.setupAnim(p_117035_, p_117036_, p_117037_, p_117039_, p_117040_, p_117041_);
            float $$13;
            float $$14;
            float $$15;
            if (horseArmorItem instanceof DyeableHorseArmorItem) {
                int $$12 = ((DyeableHorseArmorItem) horseArmorItem).getColor($$10);
                $$13 = (float)($$12 >> 16 & 255) / 255.0F;
                $$14 = (float)($$12 >> 8 & 255) / 255.0F;
                $$15 = (float)($$12 & 255) / 255.0F;
            } else {
                $$13 = 1.0F;
                $$14 = 1.0F;
                $$15 = 1.0F;
            }

            VertexConsumer $$19 = p_117033_.getBuffer(RenderType.entityCutoutNoCull(horseArmorItem.getTexture()));
            this.model.renderToBuffer(p_117032_, $$19, p_117034_, OverlayTexture.NO_OVERLAY, $$13, $$14, $$15, 1.0F);
        }
    }
}
