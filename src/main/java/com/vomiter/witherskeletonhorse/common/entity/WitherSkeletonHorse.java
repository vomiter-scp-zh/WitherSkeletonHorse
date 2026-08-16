package com.vomiter.witherskeletonhorse.common.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WitherSkeletonHorse extends SkeletonHorse {
    private static final int TRAP_MAX_LIFE = 18000;
    private int animateTickTimer = 3;

    private final WitherSkeletonTrapGoal witherSkeletonTrapGoal = new WitherSkeletonTrapGoal(this);

    private boolean witherTrap;
    private int witherTrapTime;

    public WitherSkeletonHorse(EntityType<? extends SkeletonHorse> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }


    public boolean isWitherTrap() {
        return this.witherTrap;
    }

    public void setWitherTrap(boolean trap) {
        if (trap != this.witherTrap) {
            this.witherTrap = trap;

            if (trap) {
                this.goalSelector.addGoal(1, this.witherSkeletonTrapGoal);
            } else {
                this.goalSelector.removeGoal(this.witherSkeletonTrapGoal);
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide() && this.isWitherTrap() && this.witherTrapTime++ >= TRAP_MAX_LIFE) {
            this.discard();
        }

        if(!level().isClientSide() && onSoulSpeedBlock()){
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 3, true, false));
            addEffect(new MobEffectInstance(MobEffects.JUMP, 100, 5, true, false));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("WitherSkeletonTrap", this.isWitherTrap());
        tag.putInt("WitherSkeletonTrapTime", this.witherTrapTime);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setWitherTrap(tag.getBoolean("WitherSkeletonTrap"));
        this.witherTrapTime = tag.getInt("WitherSkeletonTrapTime");
    }

    @Override public void tick(){
        if(!level().isClientSide()){
            removeEffect(MobEffects.WITHER);
        }
        super.tick();
        if(level().isClientSide()) {
            if(animateTickTimer <= 0) {
                animateTick();
                animateTickTimer = 3;
            }
            else animateTickTimer--;
        }
    }
    public void animateTick() {
        Vec3 vec3 = this.getEyePosition();
        ParticleOptions particle = this.onSoulSpeedBlock()? ParticleTypes.SOUL: ParticleTypes.SMOKE;
        for(int i = 0; i < 3; ++i) {
            if (random.nextBoolean()) {
                level().addParticle(
                        particle,
                        vec3.x() + (random.nextFloat() - 0.5) * 2,
                        vec3.y() - getEyeHeight(),
                        vec3.z() + (random.nextFloat() - 0.5) * 2,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    public boolean isCurrentlyGlowing(){
        if(Optional.ofNullable(getFirstPassenger()).map(Entity::isCurrentlyGlowing).orElse(false)) return true;
        return super.isCurrentlyGlowing();
    }
}