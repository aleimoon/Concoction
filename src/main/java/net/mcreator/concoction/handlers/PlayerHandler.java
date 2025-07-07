package net.mcreator.concoction.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.*;
import net.mcreator.concoction.utils.ColoredVertexConsumer;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.world.entity.Pose;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@EventBusSubscriber
public class PlayerHandler {
    private static int tickCounter = 0;
    private static final int UPDATE_INTERVAL = 200;
    private static final int RENDER_DELAY_TICKS = 15;
    
    // Хранилище времени выхода из специального состояния для каждого игрока
    private static final Map<UUID, Long> playerSpecialStateExitTimes = new HashMap<>();
    
    // Тоггл для каждого игрока, указывающий, должен ли рендериться эффект
    private static final Map<UUID, Boolean> shouldRenderSpicyEffect = new HashMap<>();

    private static final TagKey<Item> SOULLAND_RELATION = TagKey.create(Registries.ITEM, ResourceLocation.parse("concoction:soulland_relation"));
    private static final TagKey<Block> WILDLIFE_PLANTS = TagKey.create(Registries.BLOCK, ResourceLocation.parse("concoction:wildlife_plants"));
    private static final TagKey<Item> SPECIAL_FOOD = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/dish"));
    private static final TagKey<Item> SPECIAL_SOUP = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/soup"));
    private static final TagKey<EntityType<?>> FARM_ANIMALS = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("c:farm_animals"));

    @SubscribeEvent
    public static void playerInventoryChangeEvent(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            boolean hasTaggedItem = false;
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && stack.is(SOULLAND_RELATION)) {
                    hasTaggedItem = true;
                    break;
                }
            }

            if (hasTaggedItem) {
                Utils.addAchievement(player, "concoction:nether_mutation");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerEatItem(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack itemStack = event.getItem();
            if (itemStack.is(SPECIAL_FOOD) || itemStack.is(SPECIAL_SOUP)) {
                Utils.addAchievement(player, "concoction:eat_dish");
            }
        }
    }

    @SubscribeEvent
    public static void playerBreaksBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {

            ItemStack itemStack = event.getPlayer().getInventory().getSelected();

            if (itemStack.getItem() instanceof OvergrownHoeItem) {
                event.setCanceled(itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1);
            }

            if (event.getState().is(WILDLIFE_PLANTS)) {
                Utils.addAchievement(player, "concoction:new_crops");
            }
        }
    }

    @SubscribeEvent
    public static void playerBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack itemStack = event.getEntity().getInventory().getSelected();
        if (itemStack.getItem() instanceof OvergrownHoeItem ||
                itemStack.getItem() instanceof OvergrownAxeItem ||
                itemStack.getItem() instanceof OvergrownPickaxeItem ||
                itemStack.getItem() instanceof OvergrownShovelItem ||
                itemStack.getItem() instanceof OvergrownSwordItem) {
            if (itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getEntity().getInventory().getSelected();

        if (itemStack.getItem() instanceof OvergrownHoeItem ||
                itemStack.getItem() instanceof OvergrownAxeItem ||
                itemStack.getItem() instanceof OvergrownShovelItem) {
            if (itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerBreaksBlock(PlayerInteractEvent.EntityInteract event) {
        ItemStack itemStack = event.getEntity().getInventory().getSelected();
        if (itemStack.getItem() instanceof OvergrownHoeItem ||
                itemStack.getItem() instanceof OvergrownAxeItem ||
                itemStack.getItem() instanceof OvergrownPickaxeItem ||
                itemStack.getItem() instanceof OvergrownShovelItem) {
            if (itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();
        
        // Проверяем, должен ли рендериться эффект для этого игрока
        if (player.hasEffect(ConcoctionModMobEffects.SPICY) && shouldRenderSpicyEffect.getOrDefault(playerUUID, false)) {
            PlayerRenderer playerRenderer = event.getRenderer();
            PlayerModel<AbstractClientPlayer> model = (PlayerModel<AbstractClientPlayer>) playerRenderer.getModel();

            boolean oldHeadVisible = model.head.visible;
            boolean oldHatVisible = model.hat.visible;

            model.head.visible = false;
            model.hat.visible = false;

            player.getPersistentData().putBoolean("spicy_head_visible", oldHeadVisible);
            player.getPersistentData().putBoolean("spicy_hat_visible", oldHatVisible);
        }
    }

    @SubscribeEvent
    public static void onGiveExperience(PlayerXpEvent.PickupXp event) {
        Player player = event.getEntity();
        if (player.hasEffect(ConcoctionModMobEffects.BITTERNESS)) {

            MobEffectInstance bitternessEffect = player.getEffect(ConcoctionModMobEffects.BITTERNESS);
            int experience = event.getOrb().getValue();
            event.getOrb().value = (int) ( experience * (1 + (0.5 * (bitternessEffect.getAmplifier() + 1))) );

        }
    }


    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();

        if (player.hasEffect(ConcoctionModMobEffects.SPICY) && shouldRenderSpicyEffect.getOrDefault(playerUUID, false)) {
        	
            PlayerRenderer playerRenderer = event.getRenderer();
            PlayerModel<AbstractClientPlayer> model = (PlayerModel<AbstractClientPlayer>) playerRenderer.getModel();
            boolean oldHeadVisible = player.getPersistentData().getBoolean("spicy_head_visible");
            boolean oldHatVisible = player.getPersistentData().getBoolean("spicy_hat_visible");
            model.head.visible = oldHeadVisible;
            model.hat.visible = oldHatVisible;
            PoseStack poseStack = event.getPoseStack();
            MultiBufferSource bufferSource = event.getMultiBufferSource();
            int packedLight = event.getPackedLight();

            poseStack.pushPose();
            float scale = 1.0f;
            poseStack.scale(scale, -scale, -scale);
            poseStack.translate(0, -1.4, 0);

            VertexConsumer vertexConsumer = bufferSource.getBuffer(
                    RenderType.entityTranslucent(playerRenderer.getTextureLocation((AbstractClientPlayer) player))
            );
            float netHeadYaw = event.getPartialTick() == 0 ? player.yHeadRot : Mth.lerp(event.getPartialTick(), player.yHeadRotO, player.yHeadRot);
            float headPitch = event.getPartialTick() == 0 ? player.getXRot() : Mth.lerp(event.getPartialTick(), player.xRotO, player.getXRot());
            model.setupAnim((AbstractClientPlayer) player, 0, 0, event.getPartialTick(), netHeadYaw, headPitch);

            float time = (player.level().getGameTime() + player.getId()) % 50;
			float pulse = 0.3f + 0.4f * (0.5f * (1.0f + Mth.sin((float) (time / 50.0f * 2 * Math.PI))));
			VertexConsumer coloredConsumer = new ColoredVertexConsumer(vertexConsumer, 1.0f, pulse, pulse);


            model.head.render(poseStack, coloredConsumer, packedLight, OverlayTexture.NO_OVERLAY);
            model.hat.render(poseStack, coloredConsumer, packedLight, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void entityDied(LivingDeathEvent event) {
        Entity source = event.getSource().getEntity();

        if (source instanceof LivingEntity entity) {
            ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            Level world = entity.level();
            int enchantmentLevel = itemStack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("concoction:butchering"))));
            if (enchantmentLevel > 0) {
                Entity damagedEntity = event.getEntity();
                if (damagedEntity.getType().is(FARM_ANIMALS)) {
                    if (new Random().nextInt(0, 10) < enchantmentLevel) {

                        if (damagedEntity instanceof Chicken) {
                            ItemEntity entityToSpawn = new ItemEntity(world,
                                    damagedEntity.getX(), damagedEntity.getY() + 0.5, damagedEntity.getZ(),
                                    new ItemStack(Items.FEATHER, 1));
                            entityToSpawn.setPickUpDelay(10);
                            world.addFreshEntity(entityToSpawn);
                        }

                        if (damagedEntity instanceof Cow) {
                            ItemEntity entityToSpawn = new ItemEntity(world,
                                    damagedEntity.getX(), damagedEntity.getY() + 0.5, damagedEntity.getZ(),
                                    new ItemStack(Items.LEATHER, 1));
                            entityToSpawn.setPickUpDelay(10);
                            world.addFreshEntity(entityToSpawn);
                        }

                        ItemEntity entityToSpawn = new ItemEntity(world,
                                damagedEntity.getX(), damagedEntity.getY() + 0.5, damagedEntity.getZ(),
                                new ItemStack(ConcoctionModItems.ANIMAL_FAT.get(), 1));
                        entityToSpawn.setPickUpDelay(10);
                        world.addFreshEntity(entityToSpawn);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void entityAttacked(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();

        if (source instanceof LivingEntity entity) {
            ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
            Level world = entity.level();
            int enchantmentLevel = itemStack.getEnchantmentLevel(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.parse("concoction:butchering"))));
            if (enchantmentLevel > 0) {
                Entity damagedEntity = event.getEntity();
                if (damagedEntity.getType().is(FARM_ANIMALS))
                {
                    event.setAmount((float) (event.getAmount() + (enchantmentLevel * 2.5)));
                }

                System.out.println(event.getAmount());
            }
        }

        if (source instanceof ServerPlayer player) {
            ItemStack itemStack = player.getInventory().getSelected();
            if (itemStack.getItem() instanceof OvergrownHoeItem ||
                    itemStack.getItem() instanceof OvergrownAxeItem ||
                    itemStack.getItem() instanceof OvergrownPickaxeItem ||
                    itemStack.getItem() instanceof OvergrownShovelItem ||
                    itemStack.getItem() instanceof OvergrownSwordItem) {
                if (itemStack.getMaxDamage() - itemStack.getDamageValue() <= 1) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerTickEvent(PlayerTickEvent.Pre event) {
        tickCounter++;
        Player player = event.getEntity();
        UUID playerUUID = player.getUUID();
        
        // Отслеживаем переход из специального состояния в обычное
        boolean isInSpecialState = player.isFallFlying() || player.isSwimming() || player.getPose() == Pose.SWIMMING;
        
        // Проверяем, был ли переход из специального состояния (true -> false)
        if (!isInSpecialState && player.getPersistentData().getBoolean("was_in_special_state")) {
            // Записываем время выхода из специального состояния
            playerSpecialStateExitTimes.put(playerUUID, player.level().getGameTime());
        }
        
        // Сохраняем текущее состояние для следующей проверки
        player.getPersistentData().putBoolean("was_in_special_state", isInSpecialState);
        
        // Обновляем тоггл для рендеринга эффекта
        if (player.hasEffect(ConcoctionModMobEffects.SPICY)) {
            boolean shouldRender = !isPlayerInSpecialState(player);
            
            // Установим флаг только если он изменился или еще не установлен
            if (!shouldRenderSpicyEffect.containsKey(playerUUID) || shouldRenderSpicyEffect.get(playerUUID) != shouldRender) {
                shouldRenderSpicyEffect.put(playerUUID, shouldRender);
            }
        } else {
            // Нет эффекта SPICY, удаляем запись, чтобы не хранить лишнее
            shouldRenderSpicyEffect.remove(playerUUID);
        }

        if (tickCounter > UPDATE_INTERVAL) {
            ItemStack mainHandItem = player.getInventory().getSelected();

            if (!player.level().isClientSide() && player.level().isDay() && player.level().canSeeSky(player.blockPosition().above()) && player instanceof ServerPlayer) {
                int multiplier = 1;

                if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
                    multiplier = 2;
                }
                
                checkAndRepairItem(mainHandItem, multiplier, player.level());
                ItemStack offHandItem = player.getOffhandItem();
                checkAndRepairItem(offHandItem, multiplier, player.level());
                tickCounter = 0;
            }
        }
    }

    private static void checkAndRepairItem(ItemStack itemStack, int multiplier, Level level) {
        if (itemStack.isEmpty()) return;
        if (itemStack.getItem() instanceof OvergrownHoeItem ||
                itemStack.getItem() instanceof OvergrownAxeItem ||
                itemStack.getItem() instanceof OvergrownPickaxeItem ||
                itemStack.getItem() instanceof OvergrownShovelItem ||
                itemStack.getItem() instanceof OvergrownSwordItem)
        {
            int currentDamage = itemStack.getDamageValue();
            if (currentDamage > 0) {
                if (itemStack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING)) != 0 ||
                        itemStack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING)) != 0) return;
                int newDamage = Math.max(0, currentDamage - 1*multiplier);
                if (newDamage != currentDamage) {
                    itemStack.setDamageValue(newDamage);
                }
            }
        }
    }

    // Вспомогательный метод для проверки особых состояний игрока
    private static boolean isPlayerInSpecialState(Player player) {
        UUID playerUUID = player.getUUID();
        boolean currentlyInSpecialState = player.isFallFlying() || // проверка на полет с элитрами
                                          player.isSwimming() ||    // проверка на плавание
                                          player.getPose() == Pose.SWIMMING; // проверка на ползание
        
        // Если игрок не в специальном состоянии, проверяем время задержки
        if (!currentlyInSpecialState) {
            Long exitTime = playerSpecialStateExitTimes.get(playerUUID);
            if (exitTime != null) {
                long currentTime = player.level().getGameTime();
                if (currentTime - exitTime < RENDER_DELAY_TICKS) {
                    // Если с момента выхода из специального состояния прошло меньше RENDER_DELAY_TICKS,
                    // считаем, что игрок всё ещё в специальном состоянии
                    return true;
                } else {
                    // Удаляем запись, так как задержка истекла
                    playerSpecialStateExitTimes.remove(playerUUID);
                }
            }
        }
        
        return currentlyInSpecialState;
    }

}
