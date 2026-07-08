package raltsmc.desolation.data;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;

/**
 * NOTE (26.1 port): The vanilla model-datagen API was heavily reworked for 26.1. Almost all of
 * {@code BlockModelGenerators} / {@code ItemModelGenerators} helper methods (cube-all pools, log
 * pools, flower-pot plants, singletons, weighted variants, {@code registerParentedItemModel},
 * etc.) that the 1.21.6 version of this provider relied on are now {@code private}. The public
 * surface is limited to a handful of methods ({@code createTintedLeaves}, {@code createHangingSign},
 * {@code createParticleOnlyBlock}, {@code run}), which is not enough to regenerate this mod's models.
 *
 * <p>The idiomatic 26.1 approach for modded model datagen is to build
 * {@code BlockModelDefinitionGenerator} / {@code MultiVariant} objects and push them to the
 * generator's {@code blockStateOutput} / {@code modelOutput} / {@code itemModelOutput} — all of
 * which are {@code private} fields. Doing that requires access-widening those members in
 * {@code desolation.accesswidener} (registry/block domain), so it is intentionally left out of this
 * (mixins/datagen) domain's changes.
 *
 * <p>The provider is kept registered and compiling. When the access-wideners are added, port the
 * former generation logic here and emit {@code render_type} = {@code "translucent"} for
 * CHARRED_BRANCHES / ASH_BRAMBLE and {@code "cutout"} for CHARRED_SAPLING / POTTED_CHARRED_SAPLING /
 * CINDERFRUIT_PLANT / SCORCHED_TUFT / CHARRED_DOOR / CHARRED_TRAPDOOR (BlockRenderLayerMap was
 * removed in 26.1, so render layers must now live in the block model JSON).
 */
public class DesolationModelProvider extends FabricModelProvider {
    public DesolationModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        // See class javadoc: 26.1 model-datagen helpers are not accessible without access-wideners.
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        // See class javadoc: 26.1 model-datagen helpers are not accessible without access-wideners.
    }
}
