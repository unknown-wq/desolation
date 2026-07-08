package raltsmc.desolation.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class AshItem extends ConfigurableFertilizerItem {
    public AshItem(Properties properties) {
        super(properties);
        setGrowChance(0.25);
        setGrowTries(1);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        Vec3 target = user.position()
                .add(new Vec3(0, user.getEyeY() - user.getY(), 0).scale(0.75))
                .add(user.getLookAngle().normalize().scale(2));
        if (!world.isClientSide()) {
            AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(world, target.x, target.y, target.z);
            areaEffectCloudEntity.setDuration(20);
            areaEffectCloudEntity.setCustomParticle(ParticleTypes.WHITE_ASH);
            areaEffectCloudEntity.setPotionContents(new PotionContents(Optional.empty(),
                    Optional.of(0xcccccc),
                    List.of(new MobEffectInstance(MobEffects.BLINDNESS, 40, 1)),
                    Optional.empty()));
            areaEffectCloudEntity.setRadius(0.5F);
            areaEffectCloudEntity.setRadiusOnUse(0.5F);
            areaEffectCloudEntity.setRadiusPerTick(0.03F);
            areaEffectCloudEntity.setOwner(user);
            areaEffectCloudEntity.setWaitTime(0);
            areaEffectCloudEntity.playSound(SoundEvents.SNOW_BREAK, 1, 1);
            world.addFreshEntity(areaEffectCloudEntity);
        }

        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
