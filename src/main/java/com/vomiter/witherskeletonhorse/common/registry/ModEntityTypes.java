package com.vomiter.witherskeletonhorse.common.registry;

import com.vomiter.witherskeletonhorse.common.entity.WitherSkeletonHorse;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModEntityTypes {
    static DeferredRegister<EntityType<?>> ENTITY_TYPES
            = ModRegistries.createRegistry(ForgeRegistries.ENTITY_TYPES);
    private static final Map<RegistryObject<? extends EntityType<? extends LivingEntity>>,
                Supplier<AttributeSupplier.Builder>> ATTRIBUTE_MAP = new HashMap<>();
    public static void onEntityAttributes(EntityAttributeCreationEvent event) {
        ATTRIBUTE_MAP.forEach((type, supplier) -> {
            event.put(type.get(), supplier.get().build());
        });
    }


    public static <T extends LivingEntity> RegistryObject<EntityType<T>> registerLiving(
            String name,
            EntityType.Builder<T> builder,
            java.util.function.Supplier<AttributeSupplier.Builder> attributes
    ) {
        RegistryObject<EntityType<T>> type = ENTITY_TYPES.register(
                name,
                () -> builder.build(name)
        );

        ATTRIBUTE_MAP.put(type, attributes);
        return type;
    }

    public static RegistryObject<EntityType<WitherSkeletonHorse>> WITHER_SKELETON_HORSE
            = registerLiving(
                    "wither_skeleton_horse",
            EntityType.Builder.of(WitherSkeletonHorse::new, MobCategory.CREATURE)
                    .sized(1.54F, 1.76F)
                    .clientTrackingRange(10)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE),
            WitherSkeletonHorse::createAttributes);
}
