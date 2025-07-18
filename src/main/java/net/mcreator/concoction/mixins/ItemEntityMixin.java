package net.mcreator.concoction.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow public abstract ItemStack getItem();

    @Unique
    private int concoction$tickCounter = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (!itemEntity.getItem().is(ItemTags.create(ResourceLocation.parse("c:tableware")))) {
            return;
        }

        BlockPos itemPos = itemEntity.blockPosition();

        Optional<BlockPos> nearestChest = BlockPos
                .findClosestMatch(itemPos,10,10, (blockPos -> itemEntity
                        .level()
                        .getBlockState(blockPos)
                        .is(BlockTags.create(ResourceLocation.parse("concoction:kitchen_cabinets")))));

        if (nearestChest.isPresent()) {
            if (itemEntity.level().getBlockEntity(nearestChest.get()) instanceof Container container) {

                concoction$tickCounter++;

                for (int i = 0; i < container.getContainerSize(); i++) {
                    if (container.canPlaceItem(i, itemEntity.getItem()) && concoction$tickCounter > 200) {
                        concoction$tickCounter = 0;
                        if (!container.isEmpty()) {
                            ItemStack stack = container.getItem(i);
                            stack.setCount(stack.getCount() + itemEntity.getItem().getCount());
                            container.setItem(i, stack);
                        } else {
                            container.setItem(i, itemEntity.getItem().copy());
                        }
                        itemEntity.discard();
                        break;
                    }
                }
            }
        }
    }

}
