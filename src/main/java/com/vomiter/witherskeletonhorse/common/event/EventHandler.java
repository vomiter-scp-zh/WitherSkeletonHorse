package com.vomiter.witherskeletonhorse.common.event;

import com.vomiter.witherskeletonhorse.WitherSkeletonHorseMod;
import com.vomiter.witherskeletonhorse.common.command.ModCommand;
import com.vomiter.witherskeletonhorse.common.entity.WitherSkeletonHorse;
import com.vomiter.witherskeletonhorse.common.registry.ModEntityTypes;
import com.vomiter.witherskeletonhorse.data.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Optional;

public class EventHandler {
    private static final String WITHER_TRAP_COOLDOWN_KEY = "WitherSkeletonHorseTrapCooldownUntil";

    private static final int CHECK_INTERVAL = FMLEnvironment.production? 2000: 100;
    private static final int COOLDOWN_TICKS = 24000;

    private static final int HORIZONTAL_RADIUS = 64;
    private static final int VERTICAL_RADIUS = 3;
    private static final int SPAWN_ATTEMPTS = 64;

    private static final int AREA_RADIUS = 1; // 5x5
    private static final int AREA_HEIGHT = 3;

    public static void init() {
        final IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EventHandler::onRegisterCommands);
        bus.addListener(EventHandler::onPlayerTick);
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommand.register(event.getDispatcher());
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        ServerLevel level = (ServerLevel) player.level();

        if (player.tickCount % CHECK_INTERVAL != 0) return;

        if (!level.dimensionType().ultraWarm()) return;
        if (!level.getBiome(player.blockPosition()).is(Biomes.SOUL_SAND_VALLEY)) return;
        if (isOnWitherTrapCooldown(player, level)) return;

        if (!player.getInventory().hasAnyMatching(stack -> stack.is(ModTags.SUMMON_WITHER_TRAP))) return;

        DifficultyInstance difficulty = level.getCurrentDifficultyAt(player.blockPosition());
        double chance = difficulty.getEffectiveDifficulty() * 0.01;

        if (player.getRandom().nextDouble() > chance && FMLEnvironment.production) return;

        Optional<BlockPos> spawnPos = findTrapSpawnPos(level, player.blockPosition());

        if (spawnPos.isEmpty()) return;

        boolean spawned = spawnWitherSkeletonTrap(level, spawnPos.get());

        if (spawned) {
            setWitherTrapCooldown(player, level);
        }
    }

    private static boolean isOnWitherTrapCooldown(ServerPlayer player, ServerLevel level) {
        //if(!FMLEnvironment.production) return false;
        CompoundTag data = player.getPersistentData();
        long cooldownUntil = data.getLong(WITHER_TRAP_COOLDOWN_KEY);
        return level.getGameTime() < cooldownUntil;
    }

    private static void setWitherTrapCooldown(ServerPlayer player, ServerLevel level) {
        CompoundTag data = player.getPersistentData();
        data.putLong(WITHER_TRAP_COOLDOWN_KEY, level.getGameTime() + COOLDOWN_TICKS);
    }

    private static Optional<BlockPos> findTrapSpawnPos(ServerLevel level, BlockPos playerPos) {
        for (int attempt = 0; attempt < SPAWN_ATTEMPTS; attempt++) {
            if(!FMLEnvironment.production) WitherSkeletonHorseMod.LOGGER.info("attempt = {}", attempt);

            int dx = level.random.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
            int dz = level.random.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;

            if (dx * dx + dz * dz > HORIZONTAL_RADIUS * HORIZONTAL_RADIUS) {
                continue;
            }

            int dy = level.random.nextInt(VERTICAL_RADIUS * 2 + 1) - VERTICAL_RADIUS;

            BlockPos candidate = playerPos.offset(dx, dy, dz);

            if (isValidTrapSpawnArea(level, candidate)) {
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }

    private static boolean isValidTrapSpawnArea(ServerLevel level, BlockPos feetPos) {
        if (!level.hasChunkAt(feetPos)) return false;


        BlockPos groundPos = feetPos.below();
        BlockState groundState = level.getBlockState(groundPos);

        if (!groundState.isFaceSturdy(level, groundPos, Direction.UP)) {
            return false;
        }

        for (int x = -AREA_RADIUS; x <= AREA_RADIUS; x++) {
            for (int z = -AREA_RADIUS; z <= AREA_RADIUS; z++) {
                BlockPos floor = feetPos.offset(x, -1, z);
                BlockState floorState = level.getBlockState(floor);

                if (!floorState.isFaceSturdy(level, floor, Direction.UP)) {
                    return false;
                }

                for (int y = 0; y < AREA_HEIGHT; y++) {
                    BlockPos checkPos = feetPos.offset(x, y, z);
                    BlockState checkState = level.getBlockState(checkPos);

                    if (!checkState.getCollisionShape(level, checkPos).isEmpty()) {
                        return false;
                    }

                    if (!checkState.getFluidState().isEmpty()) {
                        return false;
                    }
                }
            }
        }

        AABB entityBox = new AABB(
                feetPos.getX() - AREA_RADIUS,
                feetPos.getY(),
                feetPos.getZ() - AREA_RADIUS,
                feetPos.getX() + AREA_RADIUS + 1,
                feetPos.getY() + AREA_HEIGHT,
                feetPos.getZ() + AREA_RADIUS + 1
        );

        return level.noCollision(entityBox);
    }

    private static boolean spawnWitherSkeletonTrap(ServerLevel level, BlockPos pos) {
        WitherSkeletonHorse horse = ModEntityTypes.WITHER_SKELETON_HORSE.get().create(level);

        if (horse == null) {
            return false;
        }
        if(!FMLEnvironment.production) WitherSkeletonHorseMod.LOGGER.info("wither skeleton horse at = {}", pos);

        horse.moveTo(
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                level.random.nextFloat() * 360.0F,
                0.0F
        );

        horse.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(pos),
                MobSpawnType.TRIGGERED,
                null,
                null
        );

        horse.setWitherTrap(true);
        horse.setTamed(false);

        level.addFreshEntity(horse);

        return true;
    }
}