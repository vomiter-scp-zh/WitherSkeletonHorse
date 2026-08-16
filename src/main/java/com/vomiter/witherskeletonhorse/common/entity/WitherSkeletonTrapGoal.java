package com.vomiter.witherskeletonhorse.common.entity;

import com.vomiter.witherskeletonhorse.common.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class WitherSkeletonTrapGoal extends Goal {
    private final WitherSkeletonHorse horse;

    public WitherSkeletonTrapGoal(WitherSkeletonHorse p_30927_) {
        this.horse = p_30927_;
    }

    public boolean canUse() {
        return this.horse.level().hasNearbyAlivePlayer(this.horse.getX(), this.horse.getY(), this.horse.getZ(), 10.0D);
    }

    public void tick() {
        ServerLevel serverlevel = (ServerLevel) this.horse.level();
        serverlevel.getServer().tell(new net.minecraft.server.TickTask(serverlevel.getServer().getTickCount(), () -> {
            if (!this.horse.isAlive()) return;
            DifficultyInstance difficultyinstance = serverlevel.getCurrentDifficultyAt(this.horse.blockPosition());
            this.horse.setWitherTrap(false);
            this.horse.setTamed(true);
            this.horse.setAge(0);
            LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(serverlevel);
            if (lightningbolt != null) {
                lightningbolt.moveTo(this.horse.getX(), this.horse.getY(), this.horse.getZ());
                lightningbolt.setVisualOnly(true);
                serverlevel.addFreshEntity(lightningbolt);
                WitherSkeleton skeleton = this.createSkeleton(difficultyinstance, this.horse);
                if (skeleton != null) {
                    skeleton.startRiding(this.horse);
                    clearRiderHeadRoom(serverlevel, skeleton);
                    serverlevel.addFreshEntityWithPassengers(skeleton);

                    for (int i = 0; i < 3; ++i) {
                        AbstractHorse abstracthorse = this.createHorse(difficultyinstance);
                        if (abstracthorse != null) {
                            WitherSkeleton skeleton1 = this.createSkeleton(difficultyinstance, abstracthorse);
                            if (skeleton1 != null) {
                                skeleton1.startRiding(abstracthorse);
                                clearRiderHeadRoom(serverlevel, skeleton1);
                                abstracthorse.push(this.horse.getRandom().triangle(0.0D, 1.1485D), 0.0D, this.horse.getRandom().triangle(0.0D, 1.1485D));
                                serverlevel.addFreshEntityWithPassengers(abstracthorse);
                            }
                        }
                    }

                }
            }
        }));
    }

    @Nullable
    private AbstractHorse createHorse(DifficultyInstance difficultyInstance) {
        WitherSkeletonHorse witherSkeletonHorse = ModEntityTypes.WITHER_SKELETON_HORSE.get().create(this.horse.level());
        if (witherSkeletonHorse != null) {
            witherSkeletonHorse.finalizeSpawn((ServerLevel) this.horse.level(), difficultyInstance, MobSpawnType.TRIGGERED, null, null);
            witherSkeletonHorse.setPos(this.horse.getX(), this.horse.getY(), this.horse.getZ());
            witherSkeletonHorse.invulnerableTime = 60;
            witherSkeletonHorse.setPersistenceRequired();
            witherSkeletonHorse.setTamed(true);
            witherSkeletonHorse.setAge(0);
        }

        return witherSkeletonHorse;
    }

    @Nullable
    private WitherSkeleton createSkeleton(DifficultyInstance difficultyInstance, AbstractHorse abstractHorse) {
        WitherSkeleton witherSkeleton = EntityType.WITHER_SKELETON.create(abstractHorse.level());
        if (witherSkeleton != null) {
            witherSkeleton.finalizeSpawn((ServerLevel) abstractHorse.level(), difficultyInstance, MobSpawnType.TRIGGERED, (SpawnGroupData) null, (CompoundTag) null);
            witherSkeleton.setPos(abstractHorse.getX(), abstractHorse.getY(), abstractHorse.getZ());
            witherSkeleton.invulnerableTime = 60;
            witherSkeleton.setPersistenceRequired();
            if (witherSkeleton.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                witherSkeleton.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            }

            witherSkeleton.setItemSlot(EquipmentSlot.MAINHAND, EnchantmentHelper.enchantItem(witherSkeleton.getRandom(), Items.BOW.getDefaultInstance(), (int) (5.0F + difficultyInstance.getSpecialMultiplier() * (float) witherSkeleton.getRandom().nextInt(18)), false));
            witherSkeleton.setItemSlot(EquipmentSlot.HEAD, EnchantmentHelper.enchantItem(witherSkeleton.getRandom(), Items.IRON_HELMET.getDefaultInstance(), (int) (5.0F + difficultyInstance.getSpecialMultiplier() * (float) witherSkeleton.getRandom().nextInt(18)), false));
            witherSkeleton.setGlowingTag(true);
        }

        return witherSkeleton;
    }

    private void clearRiderHeadRoom(ServerLevel level, Entity rider) {
        AABB box = rider.getBoundingBox().inflate(0.25D, 0.1D, 0.25D);

        int minX = Mth.floor(box.minX);
        int maxX = Mth.floor(box.maxX);
        int minY = Mth.floor(box.minY);
        int maxY = Mth.floor(box.maxY + 1.0D);
        int minZ = Mth.floor(box.minZ);
        int maxZ = Mth.floor(box.maxZ);

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    mutablePos.set(x, y, z);

                    BlockState state = level.getBlockState(mutablePos);

                    if (canClearForRider(state)) {
                        level.destroyBlock(mutablePos, false);
                    }
                }
            }
        }
    }

    private boolean canClearForRider(BlockState state) {
        return !state.isAir()
                && state.getFluidState().isEmpty()
                && state.getDestroySpeed(this.horse.level(), BlockPos.ZERO) >= 0.0F;
    }
}
