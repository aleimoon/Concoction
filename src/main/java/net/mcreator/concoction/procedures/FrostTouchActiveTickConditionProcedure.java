package net.mcreator.concoction.procedures;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class FrostTouchActiveTickConditionProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		boolean found = false;
		double sx = 0;
		double sy = 0;
		double sz = 0;
		if (!entity.isInWaterOrBubble()) {
			if (entity.onGround()) {
				if (!entity.isPassenger()) {
					sx = -1;
					found = false;
					for (int index0 = 0; index0 < 3; index0++) {
						sy = -1;
						for (int index1 = 0; index1 < 3; index1++) {
							sz = -1;
							for (int index2 = 0; index2 < 3; index2++) {
								if ((world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz))).getBlock() == Blocks.WATER) {
									world.setBlock(BlockPos.containing(x + sx, y + sy, z + sz), Blocks.FROSTED_ICE.defaultBlockState(), 3);
								} else if (world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).isFaceSturdy(world, BlockPos.containing(x + sx, y + sy, z + sz), Direction.UP)
										&& (world.isEmptyBlock(BlockPos.containing(x + sx, y + sy + 1, z + sz)) || (world.getBlockState(BlockPos.containing(x + sx, y + sy + 1, z + sz))).getBlock() == Blocks.FIRE)
										&& !((world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz))).getBlock() == Blocks.FROSTED_ICE) && Blocks.SNOW.defaultBlockState().canSurvive(world, BlockPos.containing(x + sx, y + sy + 1, z + sz))) {
									world.setBlock(BlockPos.containing(x + sx, y + sy + 1, z + sz), Blocks.SNOW.defaultBlockState(), 3);
								}
								sz = sz + 1;
							}
							sy = sy + 1;
						}
						sx = sx + 1;
					}
				}
			}
		}
	}
}
