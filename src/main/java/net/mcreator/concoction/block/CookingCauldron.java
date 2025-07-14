package net.mcreator.concoction.block;

import io.netty.buffer.Unpooled;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.block.entity.CookingCauldronEntity;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModSounds;
import net.mcreator.concoction.utils.Utils;
import net.mcreator.concoction.world.inventory.BoilingCauldronInterfaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.biome.Biome;


import java.util.Map;

import static java.lang.Math.pow;

public class CookingCauldron extends LayeredCauldronBlock implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
    private long lastBoilingSoundTime = 0;
    private static final long BOILING_SOUND_INTERVAL_MS = 2500;

    public CookingCauldron(Biome.Precipitation precipitation, CauldronInteraction.InteractionMap interactions, Properties properties) {
        super(precipitation, interactions, properties);
        registerDefaultState(stateDefinition.any()
                .setValue(LIT, false)
                .setValue(COOKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT, COOKING);
    }

    private boolean isHotBlock(BlockState state) {
    Block block = state.getBlock();

    return block instanceof FireBlock
            || block == Blocks.LAVA
            || block instanceof MagmaBlock
            || CampfireBlock.isLitCampfire(state)
            || block == Blocks.SOUL_FIRE
            || block == Blocks.SOUL_CAMPFIRE;
}




    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        BlockState belowState = level.getBlockState(pos.below());
        boolean lit = isHotBlock(belowState);

        if (lit && !state.getValue(LIT)) {
            level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (fromPos.equals(pos.below())) {
            BlockState belowState = level.getBlockState(pos.below());
            boolean lit = isHotBlock(belowState);
            if (lit != state.getValue(LIT)) {
                level.setBlock(pos, state.setValue(LIT, lit), Block.UPDATE_CLIENTS);
            }
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof CookingCauldronEntity cauldron) {
            cauldron.neighborChanged(state, level, pos, block, fromPos);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (state.getValue(LIT)) {
            final boolean isCooking = state.getValue(COOKING);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastBoilingSoundTime >= BOILING_SOUND_INTERVAL_MS) {
                Player localPlayer = Minecraft.getInstance().player;
                if (localPlayer != null) {
                    double dx = pos.getX() + 0.5 - localPlayer.getX();
                    double dy = pos.getY() + 0.5 - localPlayer.getY();
                    double dz = pos.getZ() + 0.5 - localPlayer.getZ();
                    double distanceSq = dx * dx + dy * dy + dz * dz;
                    double maxDistance = 16.0;

                    if (distanceSq < maxDistance * maxDistance) {
                        double distance = Math.sqrt(distanceSq);
                        float volume = (float)Math.pow(1.0 - (distance / maxDistance), 3);
                        volume = Math.max(0.0F, Math.min(volume, 1.0F));

                        level.playLocalSound(
                            pos.getX() + 0.5,
                            pos.getY() + 0.5,
                            pos.getZ() + 0.5,
                            ConcoctionModSounds.CAULDRON_BOILING.get(),
                            SoundSource.BLOCKS,
                            0.6F * volume,
                            rand.nextFloat() * 0.7F + 0.6F,
                            false
                        );
                    }

                    lastBoilingSoundTime = currentTime;
                }
            }

            if (rand.nextInt(2) == 0) {
                level.addParticle(ParticleTypes.BUBBLE,
                        pos.getX() + 0.5 + pow(-1, rand.nextInt(2)) * rand.nextFloat() / 3f,
                        pos.getY() + 1,
                        pos.getZ() + 0.5 + pow(-1, rand.nextInt(2)) * rand.nextFloat() / 3f,
                        0.0, 0.1, 0.0);
                level.addParticle(ParticleTypes.BUBBLE_POP,
                        pos.getX() + 0.5 + pow(-1, rand.nextInt(2)) * rand.nextFloat() / 4f,
                        pos.getY() + 1,
                        pos.getZ() + 0.5 + pow(-1, rand.nextInt(2)) * rand.nextFloat() / 4f,
                        0.0, 0.1, 0.0);
            }

            if (isCooking) {
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        pos.getX() + 0.5 + pow(-1, rand.nextInt(2)) * rand.nextFloat() / 4f,
                        pos.getY() + 1,
                        pos.getZ() + 0.5 + pow(-1, rand.nextInt(2)) * rand.nextFloat() / 4f,
                        0.0, 0.07, 0.0);
            }
        }

        super.animateTick(state, level, pos, rand);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CookingCauldronEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null :
                (type == ConcoctionModBlockEntities.COOKING_CAULDRON.get())
                        ? (lvl, pos, st, entity) -> ((CookingCauldronEntity) entity).tick(lvl, pos, st)
                        : null;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack item, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        } else if (!player.isShiftKeyDown()) {
            MenuProvider containerProvider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.cooking_cauldron");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player playerEntity) {
                    FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                    packetBuffer.writeBlockPos(pos);
                    return new BoilingCauldronInterfaceMenu(windowId, inventory, packetBuffer);
                }
            };

            player.openMenu(containerProvider, (buf) -> buf.writeBlockPos(pos));
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.CONSUME;
    }

    public Map<String, String> decreesItemCountFromResult(Map<String, String> result) {
        int newCount = Integer.parseInt(result.get("count")) - 1;
        if (newCount <= 0) {
            return Map.of(
                    "id", "",
                    "count", "",
                    "interactionType", "",
                    "experience", "0"
            );
        } else {
            return Map.of(
                    "id", result.get("id"),
                    "count", String.valueOf(newCount),
                    "interactionType", result.get("interactionType"),
                    "experience", result.get("experience")
            );
        }
    }

        @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        Containers.dropContentsOnDestroy(oldState, newState, level, pos);
        super.onRemove(oldState, level, pos, newState, isMoving);
    }
}
