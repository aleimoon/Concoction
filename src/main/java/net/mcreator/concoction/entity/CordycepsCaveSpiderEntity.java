package net.mcreator.concoction.entity;

import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.Difficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.EnumSet;

import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModEntities;

public class CordycepsCaveSpiderEntity extends CaveSpider {

    public CordycepsCaveSpiderEntity(EntityType<? extends CaveSpider> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Remove vanilla's "only aggressive in dark" targeting
        this.targetSelector.removeAllGoals(goal -> goal instanceof NearestAttackableTargetGoal<?>);

        // Always aggressive toward players
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));

        // Add custom goal to seek sky-exposed blocks
        this.goalSelector.addGoal(5, new MoveToSkyGoal(this));
    }

    public static void init(RegisterSpawnPlacementsEvent event) {
        event.register(
            ConcoctionModEntities.CORDYCEPS_CAVE_SPIDER.get(),
            SpawnPlacementTypes.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            (entityType, world, reason, pos, random) ->
                (world.getDifficulty() != Difficulty.PEACEFUL &&
                 Monster.isDarkEnoughToSpawn(world, pos, random) &&
                 Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
            RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }

    private void applyPoisonBasedOnDifficulty(LivingEntity target) {
        Difficulty diff = this.level().getDifficulty();
        int durationTicks = 0;
        if (diff == Difficulty.NORMAL) {
            durationTicks = 140; // 7 seconds
        } else if (diff == Difficulty.HARD) {
            durationTicks = 300; // 15 seconds
        }
        if (durationTicks > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, durationTicks, 0));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (!this.level().isClientSide && result && this.isAlive()) {
            if (this.getRandom().nextFloat() < 0.5f) { // 50% chance
                if (this.level() instanceof ServerLevel serverWorld) {
                    triggerSpores(serverWorld, this.blockPosition(), 3, 40); // radius, duration
                    spreadSporesBlocks(serverWorld, this.blockPosition(), 3, 12); // radius, attempts
                }
            }
        }
        return result;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide) {
            if (this.level() instanceof ServerLevel serverWorld) {
                triggerSpores(serverWorld, this.blockPosition(), 6, 80); // radius, duration
                spreadSporesBlocks(serverWorld, this.blockPosition(), 6, 25); // radius, attempts
                // Poison nearby entities on death (same rule as attack)
                poisonNearbyOnDeath(serverWorld, this.blockPosition(), 6);
            }
        }
    }

    private void poisonNearbyOnDeath(ServerLevel serverWorld, BlockPos center, double radius) {
        List<LivingEntity> nearby = serverWorld.getEntitiesOfClass(
            LivingEntity.class,
            new net.minecraft.world.phys.AABB(center).inflate(radius),
            e -> e != this
        );
        for (LivingEntity target : nearby) {
            applyPoisonBasedOnDifficulty(target);
        }
    }

    private void triggerSpores(ServerLevel serverWorld, BlockPos center, double radius, int duration) {
        serverWorld.sendParticles(
                ConcoctionModParticleTypes.SPORE_CLOUD.get(),
                center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                20, 0.5, 0.5, 0.5, 0.01
        );

        List<LivingEntity> nearby = serverWorld.getEntitiesOfClass(
                LivingEntity.class,
                new net.minecraft.world.phys.AABB(center).inflate(radius),
                e -> e != this
        );

        for (LivingEntity target : nearby) {
            target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 0));
        }
    }

    private void spreadSporesBlocks(ServerLevel serverWorld, BlockPos center, int radius, int attempts) {
        RandomSource random = serverWorld.getRandom();
        MutableBlockPos mutablePos = new MutableBlockPos();

        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dy = random.nextInt(3) - 1; // small vertical variation
            int dz = random.nextInt(radius * 2 + 1) - radius;

            mutablePos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);

            BlockState targetState = serverWorld.getBlockState(mutablePos);
            if (targetState.isAir()) {
                BlockState sporeState = ConcoctionModBlocks.CROP_PUFFBALL.get().defaultBlockState();
                if (sporeState.canSurvive(serverWorld, mutablePos)) {
                    serverWorld.setBlock(mutablePos, sporeState, 3);

                    serverWorld.sendParticles(
                        ConcoctionModParticleTypes.SPORE_CLOUD.get(),
                        mutablePos.getX() + 0.5,
                        mutablePos.getY() + 1.0,
                        mutablePos.getZ() + 0.5,
                        5, 0.3, 0.3, 0.3, 0.01
                    );
                }
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = CaveSpider.createAttributes();
        builder = builder.add(Attributes.MAX_HEALTH, 16.0); // 20 HP
        builder = builder.add(Attributes.ATTACK_DAMAGE, 1.0); // 1 damage
        return builder;
    }

    static class MoveToSkyGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final CordycepsCaveSpiderEntity spider;
        private BlockPos targetPos;

        public MoveToSkyGoal(CordycepsCaveSpiderEntity spider) {
            this.spider = spider;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            Level level = spider.level();
            for (int i = 0; i < 10; i++) {
                BlockPos randomPos = spider.blockPosition().offset(
                        spider.getRandom().nextInt(16) - 8,
                        spider.getRandom().nextInt(6) - 3,
                        spider.getRandom().nextInt(16) - 8
                );
                if (level.canSeeSky(randomPos.above())) {
                    targetPos = randomPos;
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            if (targetPos != null) {
                spider.getNavigation().moveTo(
                        targetPos.getX(),
                        targetPos.getY(),
                        targetPos.getZ(),
                        1.0
                );
            }
        }
        
    }
}
