package raltsmc.desolation.entity.ai.goal;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import raltsmc.desolation.entity.AshScuttlerEntity;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationItems;
import raltsmc.desolation.registry.DesolationLootTables;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class DigAshGoal extends MoveToBlockGoal {
    private static final Predicate<BlockState> ASH_PREDICATE;
    private static final long DIG_DURATION_TICKS = 20;
    private final AshScuttlerEntity mob;
    private final Level world;
    private final int range;
    private final int maxDY;
    private int digTick;

    public DigAshGoal(AshScuttlerEntity mob, double speed, int range, int maxDY) {
        super(mob, speed, range, maxDY);
        this.mob = mob;
        this.world = mob.level();
        this.range = range;
        this.maxDY = maxDY;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    public boolean canUse() {
        return mob.isSearching() && this.getNearestBlock(mob.blockPosition(), range, maxDY);
    }

    public void start() {
        super.start();
        digTick = 0;
    }

    public void stop() {
        super.stop();
        mob.setSearching(false);
    }

    public void tick() {
        Vec3 location = blockPos.getCenter();

        if (!location.closerThan(mob.position(), acceptedDistance())) {
            ++tryTicks;
            Vec3 vel = mob.getDeltaMovement();
            if (this.shouldRecalculatePath() && new Vec3(vel.x, 0, vel.z).lengthSqr() < 0.2f) {
                mob.setDeltaMovement(vel.x, 0.5f, vel.z);
                mob.getNavigation().moveTo(location.x, location.y, location.z, speedModifier);
            }
        } else {
            --tryTicks;
            if (++digTick >= DIG_DURATION_TICKS) {
                if (world instanceof ServerLevel serverLevel) {
                    assert world.getServer() != null;

                    world.destroyBlock(blockPos, false, mob, 1);
                    world.levelEvent(2001, blockPos, 0);

                    LootTable lootTable = world.getServer().reloadableRegistries().getLootTable(DesolationLootTables.ASH_SCUTTLER_DIG);
                    LootParams parameters = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, location)
                            .withParameter(LootContextParams.THIS_ENTITY, mob)
                            .create(LootContextParamSets.GIFT);

                    ObjectArrayList<ItemStack> list = lootTable.getRandomItems(parameters);
                    for (ItemStack itemStack : list) {
                        ItemEntity itemEntity = new ItemEntity(world, location.x, location.y, location.z, itemStack);
                        itemEntity.setDefaultPickUpDelay();
                        world.addFreshEntity(itemEntity);
                    }
                }
                stop();
            }
        }
    }

    public double acceptedDistance() {
        return 2.0D;
    }

    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        return ASH_PREDICATE.test(world.getBlockState(pos));
    }

    protected boolean getNearestBlock(BlockPos startPos, int maxRange, int maxDY) {
        Optional<BlockPos> closestAsh = BlockPos.findClosestMatch(startPos, maxRange, maxDY,
                (blockPos) -> world.getBlockState(blockPos).getBlock() == DesolationBlocks.ASH_LAYER_BLOCK
                        || world.getBlockState(blockPos).getBlock() == DesolationBlocks.ASH_BLOCK);
        if (closestAsh.isPresent()) {
            this.blockPos = closestAsh.get();
            return true;
        }
        if (world.isClientSide()) {
            double pVel = world.getRandom().nextGaussian() * 0.02D;
            world.addParticle(ParticleTypes.SMOKE, mob.getX(), mob.getY(), mob.getZ(), pVel, pVel, pVel);
        } else {
            Containers.dropItemStack(world, mob.getX(), mob.getY(), mob.getZ(),
                    new ItemStack(DesolationItems.CINDERFRUIT));
        }
        stop();
        return false;
    }

    static {
        ASH_PREDICATE = state -> state.is(DesolationBlocks.ASH_LAYER_BLOCK) || state.is(DesolationBlocks.ASH_BLOCK);
    }
}
