package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber
public class ItemHandler {

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
//        ItemEntity itemEntity = event.getEntity();
//        if (!itemEntity.getItem().is(ItemTags.create(ResourceLocation.parse("c:tableware")))) {
//            return;
//        }
//
//        BlockPos itemPos = itemEntity.blockPosition();
//
//        BlockEntity nearestChest = itemEntity.level().getBlockEntity(BlockPos.findClosestMatch(itemPos,10,10, (blockPos -> itemEntity.level().getBlockState(blockPos).is(Blocks.CHEST))).get());
//
//        if (nearestChest instanceof Container container) {
//            for (int i = 0; i < container.getContainerSize(); i++) {
//                if (container.getItem(i).isEmpty()) {
//                    container.setItem(i, itemEntity.getItem().copy());
//                    itemEntity.discard();
//                    break;
//                }
//            }
//        }

    }

}
