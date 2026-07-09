package raltsmc.desolation.entity;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import raltsmc.desolation.entity.ai.goal.AshAttackGoal;
import raltsmc.desolation.registry.DesolationItems;

import java.util.List;
import java.util.Optional;

public class BlackenedEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Boolean> MELEE_ATTACKING = SynchedEntityData.defineId(BlackenedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ASH_ATTACKING = SynchedEntityData.defineId(BlackenedEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.desolation.blackened_idle");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.desolation.blackened_hobble");
    private static final RawAnimation HEART_ANIM = RawAnimation.begin().thenLoop("animation.desolation.blackened_heartbeat");
    private static final RawAnimation MELEE_ANIM = RawAnimation.begin().thenLoop("animation.desolation.blackened_melee");
    private static final RawAnimation THROW_ANIM = RawAnimation.begin().thenLoop("animation.desolation.blackened_throw");

    public BlackenedEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(4, new AshAttackGoal(this, 1D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        // Attack every other living creature, but leave fellow Desolation mobs alone.
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, true,
                (entity, level) -> !(entity instanceof BlackenedEntity) && !(entity instanceof AshScuttlerEntity)));
    }

    public static AttributeSupplier.Builder createBlackenedAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.19D)
                .add(Attributes.ATTACK_DAMAGE, 6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MELEE_ATTACKING, false);
        builder.define(ASH_ATTACKING, false);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SKELETON_STEP, 0.5F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(DesolationItems.ASH_PILE));
    }

    public void tryAshAttack(LivingEntity target) {
            Level world = this.level();
            Vec3 eyePos = this.position().add(new Vec3(0, this.getEyeY() - this.getY(), 0).scale(0.75));
            Vec3 targetVector = eyePos.add(target.position().subtract(this.position()).normalize().scale(2.5));

            if (!world.isClientSide()) {
                AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(world, targetVector.x,
                        targetVector.y, targetVector.z);
                areaEffectCloudEntity.setDuration(30);
                areaEffectCloudEntity.setCustomParticle(ParticleTypes.WHITE_ASH);
                areaEffectCloudEntity.setPotionContents(new PotionContents(Optional.empty(), Optional.of(0xcccccc), List.of(), Optional.empty()));
                areaEffectCloudEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 2));
                areaEffectCloudEntity.setRadius(0.6F);
                areaEffectCloudEntity.setRadiusOnUse(0.6F);
                areaEffectCloudEntity.setRadiusPerTick(0.03F);
                areaEffectCloudEntity.setOwner(this);
                areaEffectCloudEntity.setWaitTime(0);
                areaEffectCloudEntity.playSound(SoundEvents.SNOW_BREAK, 1, 1);
                world.addFreshEntity(areaEffectCloudEntity);
            }
    }

    public boolean isMeleeAttacking() {
        return this.entityData.get(MELEE_ATTACKING);
    }

    public boolean isAshAttacking() {
        return this.entityData.get(ASH_ATTACKING);
    }

    public void setMeleeAttacking(boolean val) {
        this.entityData.set(MELEE_ATTACKING, val);
    }

    public void setAshAttacking(boolean val) {
        this.entityData.set(ASH_ATTACKING, val);
    }

    private <E extends GeoAnimatable> PlayState idlePredicate(AnimationTest<E> event) {
        if (!event.isMoving()) {
            event.setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    private <E extends GeoAnimatable> PlayState walkPredicate(AnimationTest<E> event) {
        if (event.isMoving()) {
            event.setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    private <E extends GeoAnimatable> PlayState heartPredicate(AnimationTest<E> event) {
        event.setAnimation(HEART_ANIM);
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState attackPredicate(AnimationTest<E> event) {
        if (this.isMeleeAttacking()) {
            event.setAnimation(MELEE_ANIM);
            return PlayState.CONTINUE;
        } else if (this.isAshAttacking()) {
            event.setAnimation(THROW_ANIM);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("idleController", 0, this::idlePredicate));
        controllerRegistrar.add(new AnimationController<>("walkController", 0, this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>("heartController", 0, this::heartPredicate));
        controllerRegistrar.add(new AnimationController<>("attackController", 0, this::attackPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
