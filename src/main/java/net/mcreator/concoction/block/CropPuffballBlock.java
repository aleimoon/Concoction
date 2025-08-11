package net.mcreator.concoction.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import java.util.Random;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.mcreator.concoction.init.ConcoctionModEntities; // You’ll need to have these entity types registered
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.mcreator.concoction.utils.Utils;

public class CropPuffballBlock extends CropBlock {
	public static final int MAX_AGE = 2;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

	public CropPuffballBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.CLAY).sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(AGE)) {
			default -> Block.box(0, 0, 0, 16, 1, 16);
			case 0 -> Block.box(0, 0, 0, 16, 1, 16);
			case 1 -> Block.box(6, 1, 6, 10, 4, 10);
			case 2 -> Block.box(4, 1, 4, 12, 7, 12);
		};
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}
	
	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return new ItemStack(ConcoctionModItems.PUFFBALL_SPORES.get());
	}

	@Override
	public boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
	    if (!(worldIn instanceof LevelReader)) return false;
	    return canSurvive(state, (LevelReader) worldIn, pos);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
	    BlockPos blockBelow = pos.below();
	    BlockState soil = worldIn.getBlockState(blockBelow);
	    return soil.isSolidRender(worldIn, blockBelow) || soil.getBlock() instanceof FarmBlock || soil.getBlock() instanceof SoullandBlock;
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return PathType.OPEN;
	}
	
	@Override
	protected ItemLike getBaseSeedId() {
		return ConcoctionModItems.PUFFBALL_SPORES.get();
	}

	@Override
    public int getMaxAge() {
        return MAX_AGE;
    }

	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}
	
	@Override
    public void entityInside(BlockState state, Level world, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        if (!world.isClientSide && entity instanceof LivingEntity livingEntity) {
            int age = state.getValue(AGE);
            if (age == MAX_AGE && !livingEntity.isCrouching()) {
                // Replace spiders with cordyceps variants
                if ((entity instanceof Spider || entity instanceof CaveSpider) &&
    !(entity.getType() == ConcoctionModEntities.CORDYCEPS_SPIDER.get() ||
      entity.getType() == ConcoctionModEntities.CORDYCEPS_CAVE_SPIDER.get())) {

    EntityType<? extends LivingEntity> replacementType = (entity instanceof CaveSpider)
            ? ConcoctionModEntities.CORDYCEPS_CAVE_SPIDER.get()
            : ConcoctionModEntities.CORDYCEPS_SPIDER.get();

    LivingEntity newEntity = replacementType.create(world);
    if (newEntity != null) {
        // Copy position & rotations
        newEntity.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
        newEntity.setYRot(entity.getYRot());
        newEntity.setXRot(entity.getXRot());

        if (entity instanceof LivingEntity le) {
            newEntity.setYHeadRot(le.getYHeadRot());
            newEntity.setXRot(le.getXRot());
        }

        // Copy health & name
        newEntity.setHealth(((LivingEntity) entity).getHealth());
        newEntity.setCustomName(entity.getCustomName());
        newEntity.setCustomNameVisible(entity.isCustomNameVisible());

        // Ensure it doesn't despawn
        if (newEntity instanceof Mob mobEntity) {
            mobEntity.setPersistenceRequired();
        }

        // Copy potion effects
        for (MobEffectInstance effect : ((LivingEntity) entity).getActiveEffects()) {
            newEntity.addEffect(new MobEffectInstance(effect));
        }

        // Spawn and remove old
        world.addFreshEntity(newEntity);
        entity.discard();

        // Update local references
        entity = newEntity;
        livingEntity = newEntity;
    }
}



                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0));
                if (livingEntity instanceof ServerPlayer serverPlayer) {
                    Utils.addAchievement(serverPlayer, "concoction:puffball_accident");
                }

                world.setBlock(pos, state.setValue(AGE, age - 2), 3);
                world.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (world instanceof ServerLevel serverWorld) {
                    serverWorld.sendParticles(ConcoctionModParticleTypes.SPORE_CLOUD.get(), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            10, 0.5, 0.5, 0.5, 0.01);
                }

                RandomSource random = world.getRandom();
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                for (int i = 0; i < 20; i++) {
                    int dx = random.nextInt(9) - 4;
                    int dy = random.nextInt(3) - 1;
                    int dz = random.nextInt(9) - 4;
                    mutablePos.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockState targetState = world.getBlockState(mutablePos);
                    if (targetState.isAir() && this.canSurvive(this.defaultBlockState().setValue(AGE, 0), world, mutablePos)) {
                        world.setBlock(mutablePos, this.defaultBlockState().setValue(AGE, 0), 3);
                        if (world instanceof ServerLevel serverWorld2) {
                            serverWorld2.sendParticles(ConcoctionModParticleTypes.SPORE_CLOUD.get(), mutablePos.getX() + 0.5, mutablePos.getY() + 1.0, mutablePos.getZ() + 0.5,
                                    5, 0.3, 0.3, 0.3, 0.01);
                        }
                    }
                }
            }
        }
        super.entityInside(state, world, pos, entity);
    }
}
