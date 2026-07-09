package raltsmc.desolation.mixin.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import raltsmc.desolation.registry.DesolationBlocks;

import java.util.function.BiConsumer;

@Mixin(TrunkPlacer.class)
public class TrunkPlacerMixin {
    @Inject(method = "placeBelowTrunkBlock", at = @At("HEAD"), cancellable = true)
    private static void desolation$notAlwaysDirt(WorldGenLevel world, BiConsumer<BlockPos, BlockState> replacer, RandomSource random, BlockPos pos, TreeConfiguration config, CallbackInfo ci) {
        if (world.isStateAtPosition(pos, state ->
                state.is(DesolationBlocks.CHARRED_SOIL) ||
                state.is(DesolationBlocks.EMBER_BLOCK) ||
                state.is(DesolationBlocks.COOLED_EMBER_BLOCK))) {
            ci.cancel();
        }
    }
}
