package raltsmc.desolation.world.gen.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import raltsmc.desolation.registry.DesolationFoliagePlacerTypes;

public class CharredFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<CharredFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            foliagePlacerParts(instance)
                    .and(IntProviders.codec(1, 512).fieldOf("foliage_height").forGetter(CharredFoliagePlacer::getFoliageHeight))
                    .apply(instance, CharredFoliagePlacer::new)
    );
    protected final IntProvider foliageHeight;

    public CharredFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider foliageHeight) {
        super(radius, offset);
        this.foliageHeight = foliageHeight;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return DesolationFoliagePlacerTypes.CHARRED_FOLIAGE_PLACER;
    }

    public IntProvider getFoliageHeight() {
        return this.foliageHeight;
    }

    @Override
    protected void createFoliage(WorldGenLevel world, FoliageSetter setter, RandomSource random, TreeConfiguration config, int trunkHeight, FoliageAttachment attachment, int foliageHeight, int radius, int offset) {
        for (int i = -foliageHeight; i <= foliageHeight; ++i) {
            int r = radius - (Math.abs(i) / (foliageHeight)) * (radius/2) + i/4;

            BlockPos blockPos = attachment.pos().offset(0, i, 0);

            for (BlockPos pos : BlockPos.betweenClosed(blockPos.offset(-r, 0, -r), blockPos.offset(r, 0, r))) {
                double chance;
                int dX = Math.abs(pos.getX() - blockPos.getX());
                int dZ = Math.abs(pos.getZ() - blockPos.getZ());
                int thisRadius = Math.max(dX, dZ);

                if (thisRadius <= 1) {
                    chance = 0.65;
                } else if (thisRadius <= 2) {
                    chance = 0.4;
                } else {
                    chance = 0.25;
                }

                if (random.nextDouble() < chance) {
                    tryPlaceLeaf(world, setter, random, config, pos);
                }
            }
        }
    }

    @Override
    public int foliageHeight(RandomSource random, int trunkHeight, TreeConfiguration config) {
        return this.foliageHeight.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int radius, int dx, int dy, int dz, boolean giantTrunk) {
        return radius == dz && dy == dz && (random.nextInt(2) == 0 || dx == 0);
    }
}
