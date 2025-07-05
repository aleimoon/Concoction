package net.mcreator.concoction.item.food.types;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.init.ConcoctionModPotions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public enum FoodEffectType implements StringRepresentable {
    SWEET("sweet"),
    SPICY("spicy"),
    MINTY("minty"),
    GLOW("glow"),
    INSTABILITY("instability"),
    SALTY("saltness"),
    FLAMING("fiery_touch"),
    WARM("warming"),
    BITTER("bitterness");


    private final String name;
    private FoodEffectType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static FoodEffectType getByName(String name) {
        return switch (name) {
            case "sweet" -> SWEET;
            case "spicy" -> SPICY;
            case "minty" -> MINTY;
            case "glow" -> GLOW;
            case "instability" -> INSTABILITY;
            case "saltness" -> SALTY;
            case "fiery_touch" -> FLAMING;
            case "warming" -> WARM;
            case "bitterness" -> BITTER;
            default -> throw new IllegalArgumentException("Invalid name: " + name);
        };
    }

    public static MobEffectInstance getEffect(FoodEffectType type, int level, int duration, boolean isHidden) {
        return switch (type) {
            case SWEET -> new MobEffectInstance(ConcoctionModMobEffects.SWEETNESS, duration*20, level-1, false, !isHidden, true, null);
            case SPICY -> new MobEffectInstance(ConcoctionModMobEffects.SPICY, duration*20, level-1, false, !isHidden, true, null);
            case MINTY -> new MobEffectInstance(ConcoctionModMobEffects.MINTY_BREATH, duration*20, level-1, false, !isHidden, true, null);
            case GLOW ->  new MobEffectInstance(MobEffects.GLOWING, duration*20, level-1, false, !isHidden, true, null);
            case INSTABILITY -> new MobEffectInstance(ConcoctionModMobEffects.INSTABILITY, duration*20, level-1, false, !isHidden, true, null);
            case SALTY -> new MobEffectInstance(ConcoctionModMobEffects.SALTNESS, duration*20, level-1, false, !isHidden, true, null);
            case FLAMING -> new MobEffectInstance(ConcoctionModMobEffects.FIERY_TOUCH, duration*20, level*0, false, !isHidden, true, null);
            case WARM -> new MobEffectInstance(ConcoctionModMobEffects.WARMING, duration*20, level-1, false, !isHidden, true, null);
            case BITTER -> new MobEffectInstance(ConcoctionModMobEffects.BITTERNESS, duration*20, level-1, false, !isHidden, true, null);

        };
    }

    public Component getTooltip(int level, int duration, boolean isHidden) {
        MutableComponent effectName = Component.translatable("taste.concoction." + this.name);
        return effectName.withStyle(ChatFormatting.GRAY);
    }
}
