package raltsmc.desolation.world.gen.surfacerules;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import raltsmc.desolation.registry.DesolationBiomes;
import raltsmc.desolation.registry.DesolationBlocks;

// Contains all of the surface rules used by Desolation
public class DesolationSurfaceRules {
	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationSurfaceRules() {
		return;
	}

    private static SurfaceRules.RuleSource block(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

	public static SurfaceRules.RuleSource createRules() {

        // Biome-level rules
        SurfaceRules.RuleSource charredForest = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 6, CaveSurface.FLOOR),
                SurfaceRules.ifTrue(SurfaceRules.isBiome(DesolationBiomes.CHARRED_FOREST,
                        DesolationBiomes.CHARRED_FOREST_CLEARING,
                        DesolationBiomes.CHARRED_FOREST_SMALL),
            SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
                    block(DesolationBlocks.CHARRED_SOIL)),
                    block(Blocks.DIRT))));

        // At the moment, there's just Charred Forest (and variants).  To add another, wrap them in SurfaceRules.sequence()
        return SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
                charredForest);
	}

    public static void init() { }
}
