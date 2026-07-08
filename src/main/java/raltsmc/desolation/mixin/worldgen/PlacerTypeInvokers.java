package raltsmc.desolation.mixin.worldgen;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Holder for @Invoker mixins that expose the private static {@code register(String, MapCodec)}
 * factory methods on vanilla {@link TrunkPlacerType} / {@link FoliagePlacerType}.
 *
 * <p>Terraform's {@code PlacerTypes} helper was dropped during the 26.1 port, so trunk/foliage
 * placer types must be registered directly against the vanilla registries. Vanilla's factory
 * methods delegate to {@code Registry.register(registry, String, value)}, which resolves the
 * String via {@code Identifier.parse}, so a fully-qualified {@code "desolation:..."} id keeps the
 * mod namespace.
 *
 * <p>NOTE FOR MIXIN CONFIG (Agent 5): two nested mixins live here. Add BOTH of these to the
 * {@code mixins} array in {@code desolation.mixins.json} (a single {@code worldgen.PlacerTypeInvokers}
 * entry is NOT valid because each private member lives on a different target class):
 * <ul>
 *   <li>{@code "worldgen.PlacerTypeInvokers$TrunkPlacerTypeInvoker"}</li>
 *   <li>{@code "worldgen.PlacerTypeInvokers$FoliagePlacerTypeInvoker"}</li>
 * </ul>
 */
public final class PlacerTypeInvokers {
    private PlacerTypeInvokers() {}

    @Mixin(TrunkPlacerType.class)
    public interface TrunkPlacerTypeInvoker {
        // Static @Invoker: the body is a placeholder that Mixin replaces at load time with a
        // call to the private static TrunkPlacerType.register(String, MapCodec).
        @Invoker("register")
        static <P extends TrunkPlacer> TrunkPlacerType<P> desolation$register(String id, MapCodec<P> codec) {
            throw new AssertionError();
        }
    }

    @Mixin(FoliagePlacerType.class)
    public interface FoliagePlacerTypeInvoker {
        @Invoker("register")
        static <P extends FoliagePlacer> FoliagePlacerType<P> desolation$register(String id, MapCodec<P> codec) {
            throw new AssertionError();
        }
    }
}
