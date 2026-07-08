package raltsmc.desolation.entity.effect;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import raltsmc.desolation.Desolation;

public class CinderSoulStatusEffect extends MobEffect {
    public CinderSoulStatusEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xff5900);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "effect.cindersoul_strength"), 7.0D, AttributeModifier.Operation.ADD_VALUE)
                .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "effect.cindersoul_knockback"), 0.5D, AttributeModifier.Operation.ADD_VALUE)
                .addAttributeModifier(Attributes.ARMOR, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "effect.cindersoul_resistance"), 4.0D, AttributeModifier.Operation.ADD_VALUE)
                .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Identifier.fromNamespaceAndPath(Desolation.MOD_ID, "effect.cindersoul_knockback_resistance"), 1.5D, AttributeModifier.Operation.ADD_VALUE);
    }
}
