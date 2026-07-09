package raltsmc.desolation.world.gen.surfacerules;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.tag.DesolationBiomeTags;

// Contains all of the surface rules used by Desolation
public class DesolationSurfaceRules {
	@SuppressWarnings("UnnecessaryReturnStatement")
	private DesolationSurfaceRules() {
		return;
	}

    private static SurfaceRules.RuleSource block(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

	public static SurfaceRules.RuleSource createRules(HolderGetter<Biome> biomes) {

        // Biome-level rules. 26.2's SurfaceRules.isBiome resolves biome keys eagerly against a
        // HolderGetter, which fails for this mod's data-driven biomes when the rule is built (they
        // are not in the registry yet). Instead we build the biome condition from a lazily-resolved
        // tag HolderSet, which binds during world generation once the biomes and tags are loaded.
        HolderSet<Biome> charredForests = biomes.getOrThrow(DesolationBiomeTags.CHARRED_FORESTS);
        SurfaceRules.RuleSource charredForest = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(0, false, 6, CaveSurface.FLOOR),
                SurfaceRules.ifTrue(new SurfaceRules.BiomeConditionSource(charredForests),
            SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.waterBlockCheck(0, 0),
                    block(DesolationBlocks.CHARRED_SOIL)),
                    block(Blocks.DIRT))));

        // At the moment, there's just Charred Forest (and variants).  To add another, wrap them in SurfaceRules.sequence()
        return SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(),
                charredForest);
	}

    public static void init() { }
}
