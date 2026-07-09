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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import raltsmc.desolation.entity.ai.goal.DigAshGoal;
import raltsmc.desolation.registry.DesolationItems;

import java.util.function.Predicate;

public class AshScuttlerEntity extends Animal implements GeoEntity {
    private static final EntityDataAccessor<Boolean> SEARCHING = SynchedEntityData.defineId(AshScuttlerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Predicate<ItemStack> ATTRACTING_INGREDIENT = stack -> stack.is(DesolationItems.CINDERFRUIT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation HEAD_ANIM = RawAnimation.begin().thenLoop("animation.desolation.ash_scuttler_head");
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenPlay("animation.desolation.ash_scuttler_walk");

    public AshScuttlerEntity(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new DigAshGoal(this, 0.3D,40,2));
        this.goalSelector.addGoal(2, new PanicGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new TemptGoal(this, 0.3D, ATTRACTING_INGREDIENT, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.2F));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SEARCHING, false);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob entity) {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENDERMITE_STEP, 0.5F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }

    public boolean isSearching() {
        return this.entityData.get(SEARCHING);
    }

    public void setSearching(boolean val) {
        this.entityData.set(SEARCHING, val);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        Level world = this.level();
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();
        if (item == DesolationItems.CINDERFRUIT && !this.isSearching()) {
            if (!world.isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                this.entityData.set(SEARCHING, true);
                return InteractionResult.SUCCESS;
            } else {
                double pVel = random.nextGaussian() * 0.02D;
                world.addParticle(ParticleTypes.HEART, this.getX(), this.getY(), this.getZ(), pVel, pVel, pVel);
                player.playSound(SoundEvents.NETHER_WART_PLANTED, 1.0F, 1.0F);
                return InteractionResult.CONSUME;
            }
        }
        return super.mobInteract(player, hand);
    }

    private <E extends GeoAnimatable> PlayState walkPredicate(AnimationTest<E> event) {
        if (event.isMoving()) {
            event.setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    private <E extends GeoAnimatable> PlayState headPredicate(AnimationTest<E> event) {
        if (!event.isMoving()) {
            event.setAnimation(HEAD_ANIM);
            return PlayState.CONTINUE;
        } else {
            return PlayState.STOP;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>("walkController", 0, this::walkPredicate));
        controllerRegistrar.add(new AnimationController<>("headController", 0, this::headPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
