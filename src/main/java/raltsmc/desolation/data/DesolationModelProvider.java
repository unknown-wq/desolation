package raltsmc.desolation.data;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import raltsmc.desolation.block.CinderfruitPlantBlock;
import raltsmc.desolation.registry.DesolationBlockFamilies;
import raltsmc.desolation.registry.DesolationBlocks;
import raltsmc.desolation.registry.DesolationItems;

import java.util.function.BiConsumer;

/**
 * 26.1 port. The vanilla model-datagen helper API is almost entirely {@code private} in 26.1, so
 * this provider pushes {@link BlockModelDefinitionGenerator}/{@link MultiVariant} objects directly to
 * the generators' output sinks (access-widened in {@code desolation.accesswidener}) and reuses a few
 * widened private helpers ({@code family}, {@code woodProvider}, {@code createDoor},
 * {@code createTrapdoor}). {@code BlockRenderLayerMap} was removed in 26.1, so render layers are
 * emitted as a {@code "render_type"} field injected into the block model JSON (see {@link #withRenderType}).
 */
public class DesolationModelProvider extends FabricModelProvider {
    private static final String CUTOUT = "minecraft:cutout";
    private static final String TRANSLUCENT = "minecraft:translucent";

    public DesolationModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        final BiConsumer<Identifier, ModelInstance> models = generator.modelOutput;

        // === Charred wood family ===
        // family(base) uploads the planks cube_all model + blockstate; generateFor() emits every
        // other family member (stairs/slab/fence/gate/pressure_plate/button/sign/wall_sign) plus
        // their item models. Door + trapdoor are skipped so we can re-emit them with a cutout render_type.
        BlockModelGenerators.BlockFamilyProvider family = generator.family(DesolationBlocks.CHARRED_PLANKS);
        family.skipGeneratingModelsFor.add(DesolationBlocks.CHARRED_DOOR);
        family.skipGeneratingModelsFor.add(DesolationBlocks.CHARRED_TRAPDOOR);
        family.generateFor(DesolationBlockFamilies.CHARRED);

        // Charred door (cutout): 8 door part models + blockstate + flat item model.
        TextureMapping doorMapping = TextureMapping.door(DesolationBlocks.CHARRED_DOOR);
        BiConsumer<Identifier, ModelInstance> doorOut = withRenderType(models, CUTOUT);
        MultiVariant doorBottomLeft = plain(ModelTemplates.DOOR_BOTTOM_LEFT.create(DesolationBlocks.CHARRED_DOOR, doorMapping, doorOut));
        MultiVariant doorBottomLeftOpen = plain(ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(DesolationBlocks.CHARRED_DOOR, doorMapping, doorOut));
        MultiVariant doorBottomRight = plain(ModelTemplates.DOOR_BOTTOM_RIGHT.create(DesolationBlocks.CHARRED_DOOR, doorMapping, doorOut));
        MultiVariant doorBottomRightOpen = plain(ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(DesolationBlocks.CHARRED_DOOR, doorMapping, doorOut));
        MultiVariant doorTopLeft = plain(ModelTemplates.DOOR_TOP_LEFT.create(DesolationBlocks.CHARRED_DOOR, doorMapping, doorOut));
        MultiVariant doorTopLeftOpen = plain(ModelTemplates.DOOR_TOP_LEFT_OPEN.create(DesolationBlocks.CHARRED_DOOR, doorMapping, doorOut));
        MultiVariant doorTopRight = plain(ModelTemplates.DOOR_TOP_RIGHT.create(DesolationBlocks.CHARRED_DOOR, doorMapping, doorOut));
        MultiVariant doorTopRightOpen = plain(ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(DesolationBlocks.CHARRED_DOOR, doorMapping, doorOut));
        generator.blockStateOutput.accept(BlockModelGenerators.createDoor(DesolationBlocks.CHARRED_DOOR,
                doorBottomLeft, doorBottomLeftOpen, doorBottomRight, doorBottomRightOpen,
                doorTopLeft, doorTopLeftOpen, doorTopRight, doorTopRightOpen));
        Item doorItem = DesolationBlocks.CHARRED_DOOR.asItem();
        Identifier charredDoorItem = ModelTemplates.FLAT_ITEM.create(doorItem, TextureMapping.layer0(doorItem), models);
        generator.itemModelOutput.accept(doorItem, ItemModelUtils.plainModel(charredDoorItem));

        // Charred trapdoor (cutout): top/bottom/open models + blockstate + parented item model.
        TextureMapping trapdoorMapping = TextureMapping.defaultTexture(DesolationBlocks.CHARRED_TRAPDOOR);
        BiConsumer<Identifier, ModelInstance> trapdoorOut = withRenderType(models, CUTOUT);
        Identifier trapdoorTop = ModelTemplates.TRAPDOOR_TOP.create(DesolationBlocks.CHARRED_TRAPDOOR, trapdoorMapping, trapdoorOut);
        Identifier trapdoorBottom = ModelTemplates.TRAPDOOR_BOTTOM.create(DesolationBlocks.CHARRED_TRAPDOOR, trapdoorMapping, trapdoorOut);
        Identifier trapdoorOpen = ModelTemplates.TRAPDOOR_OPEN.create(DesolationBlocks.CHARRED_TRAPDOOR, trapdoorMapping, trapdoorOut);
        generator.blockStateOutput.accept(BlockModelGenerators.createTrapdoor(DesolationBlocks.CHARRED_TRAPDOOR,
                plain(trapdoorTop), plain(trapdoorBottom), plain(trapdoorOpen)));
        generator.itemModelOutput.accept(DesolationBlocks.CHARRED_TRAPDOOR.asItem(), ItemModelUtils.plainModel(trapdoorBottom));

        // Logs / wood (+ stripped variants).
        generator.woodProvider(DesolationBlocks.CHARRED_LOG)
                .logWithHorizontal(DesolationBlocks.CHARRED_LOG).wood(DesolationBlocks.CHARRED_WOOD);
        generator.woodProvider(DesolationBlocks.STRIPPED_CHARRED_LOG)
                .logWithHorizontal(DesolationBlocks.STRIPPED_CHARRED_LOG).wood(DesolationBlocks.STRIPPED_CHARRED_WOOD);

        // Hanging sign + wall hanging sign (public helper).
        generator.createHangingSign(DesolationBlocks.CHARRED_PLANKS,
                DesolationBlocks.CHARRED_HANGING_SIGN, DesolationBlocks.CHARRED_WALL_HANGING_SIGN);

        // Charred sapling + potted sapling (cutout cross).
        Identifier saplingModel = ModelTemplates.CROSS.create(DesolationBlocks.CHARRED_SAPLING,
                TextureMapping.cross(DesolationBlocks.CHARRED_SAPLING), withRenderType(models, CUTOUT));
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(DesolationBlocks.CHARRED_SAPLING, plain(saplingModel)));
        Item saplingItemStack = DesolationBlocks.CHARRED_SAPLING.asItem();
        Identifier saplingItem = ModelTemplates.FLAT_ITEM.create(saplingItemStack,
                TextureMapping.layer0(DesolationBlocks.CHARRED_SAPLING), models);
        generator.itemModelOutput.accept(saplingItemStack, ItemModelUtils.plainModel(saplingItem));
        Identifier pottedSaplingModel = ModelTemplates.FLOWER_POT_CROSS.create(DesolationBlocks.POTTED_CHARRED_SAPLING,
                TextureMapping.plant(DesolationBlocks.CHARRED_SAPLING), withRenderType(models, CUTOUT));
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(DesolationBlocks.POTTED_CHARRED_SAPLING, plain(pottedSaplingModel)));

        // Leaves-like blocks (translucent).
        singletonWithRenderType(generator, DesolationBlocks.CHARRED_BRANCHES, TexturedModel.LEAVES, TRANSLUCENT);
        singletonWithRenderType(generator, DesolationBlocks.ASH_BRAMBLE, TexturedModel.LEAVES, TRANSLUCENT);

        // Misc. simple cube-all blocks.
        simpleCubeAll(generator, DesolationBlocks.ACTIVATED_CHARCOAL_BLOCK);
        simpleCubeAll(generator, DesolationBlocks.COOLED_EMBER_BLOCK);
        simpleCubeAll(generator, DesolationBlocks.EMBER_BLOCK);

        // Ash block (cube-all) + ash layer block (snow-like layered powder).
        Identifier ashCube = ModelTemplates.CUBE_ALL.create(DesolationBlocks.ASH_BLOCK,
                TextureMapping.cube(DesolationBlocks.ASH_BLOCK), models);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(DesolationBlocks.ASH_BLOCK, plain(ashCube)));
        generator.itemModelOutput.accept(DesolationBlocks.ASH_BLOCK.asItem(), ItemModelUtils.plainModel(ashCube));

        IntegerProperty layers = BlockStateProperties.LAYERS;
        Identifier[] ashHeights = new Identifier[9];
        String ashTexture = TextureMapping.getBlockTexture(DesolationBlocks.ASH_BLOCK).sprite().toString();
        for (int layer = 1; layer <= 7; layer++) {
            Identifier id = Identifier.fromNamespaceAndPath("desolation", "block/ash_height" + (layer * 2));
            JsonObject json = new JsonObject();
            json.addProperty("parent", "minecraft:block/snow_height" + (layer * 2));
            JsonObject textures = new JsonObject();
            textures.addProperty("particle", ashTexture);
            textures.addProperty("texture", ashTexture);
            json.add("textures", textures);
            models.accept(id, () -> json);
            ashHeights[layer] = id;
        }
        ashHeights[8] = ashCube;
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(DesolationBlocks.ASH_LAYER_BLOCK)
                .with(PropertyDispatch.initial(layers).generate(height -> plain(ashHeights[height]))));
        generator.itemModelOutput.accept(DesolationBlocks.ASH_LAYER_BLOCK.asItem(), ItemModelUtils.plainModel(ashHeights[1]));

        // Charred soil: randomly one of two cube-all textures; item model from _var1.
        Identifier soilVar1 = ModelTemplates.CUBE_ALL.createWithSuffix(DesolationBlocks.CHARRED_SOIL, "_var1",
                TextureMapping.cube(TextureMapping.getBlockTexture(DesolationBlocks.CHARRED_SOIL, "_var1")), models);
        Identifier soilVar2 = ModelTemplates.CUBE_ALL.createWithSuffix(DesolationBlocks.CHARRED_SOIL, "_var2",
                TextureMapping.cube(TextureMapping.getBlockTexture(DesolationBlocks.CHARRED_SOIL, "_var2")), models);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(DesolationBlocks.CHARRED_SOIL, random(soilVar1, soilVar2)));
        generator.itemModelOutput.accept(DesolationItems.CHARRED_SOIL, ItemModelUtils.plainModel(soilVar1));

        // Cinderfruit plant: age cross models (cutout); its item is cinderfruit_seeds (handled in item models).
        BiConsumer<Identifier, ModelInstance> cinderOut = withRenderType(models, CUTOUT);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(DesolationBlocks.CINDERFRUIT_PLANT)
                .with(PropertyDispatch.initial(CinderfruitPlantBlock.AGE).generate(age -> plain(
                        ModelTemplates.CROSS.createWithSuffix(DesolationBlocks.CINDERFRUIT_PLANT, "_age" + age,
                                TextureMapping.cross(TextureMapping.getBlockTexture(DesolationBlocks.CINDERFRUIT_PLANT, "_age" + age)),
                                cinderOut)))));

        // Scorched tuft: untinted cross randomly selected from three size models (cutout).
        BiConsumer<Identifier, ModelInstance> tuftOut = withRenderType(models, CUTOUT);
        Identifier tuftSmall = ModelTemplates.TINTED_CROSS.create(DesolationBlocks.SCORCHED_TUFT,
                TextureMapping.cross(TextureMapping.getBlockTexture(DesolationBlocks.SCORCHED_TUFT)), tuftOut);
        Identifier tuftMedium = ModelTemplates.TINTED_CROSS.createWithSuffix(DesolationBlocks.SCORCHED_TUFT, "_medium",
                TextureMapping.cross(TextureMapping.getBlockTexture(DesolationBlocks.SCORCHED_TUFT, "_medium")), tuftOut);
        Identifier tuftLarge = ModelTemplates.TINTED_CROSS.createWithSuffix(DesolationBlocks.SCORCHED_TUFT, "_large",
                TextureMapping.cross(TextureMapping.getBlockTexture(DesolationBlocks.SCORCHED_TUFT, "_large")), tuftOut);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(DesolationBlocks.SCORCHED_TUFT,
                random(tuftSmall, tuftMedium, tuftLarge)));
        generator.itemModelOutput.accept(DesolationBlocks.SCORCHED_TUFT.asItem(), ItemModelUtils.plainModel(tuftSmall));

        // Block item models missed by the family generator (base + fence gate + pressure plate).
        parentedBlockItem(generator, DesolationBlocks.CHARRED_PLANKS);
        parentedBlockItem(generator, DesolationBlocks.CHARRED_FENCE_GATE);
        parentedBlockItem(generator, DesolationBlocks.CHARRED_PRESSURE_PLATE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        // Spawn eggs (custom flat icons, as in the original mod).
        generatedItem(generator, DesolationItems.SPAWN_EGG_ASH_SCUTTLER);
        generatedItem(generator, DesolationItems.SPAWN_EGG_BLACKENED);

        // Misc. items.
        generatedItem(generator, DesolationItems.ACTIVATED_CHARCOAL);
        generatedItem(generator, DesolationItems.AIR_FILTER);
        generatedItem(generator, DesolationItems.ASH_PILE);
        generatedItem(generator, DesolationItems.CHARCOAL_BIT);
        generatedItem(generator, DesolationItems.CINDERFRUIT);
        generatedItem(generator, DesolationItems.CINDERFRUIT_SEEDS);
        generatedItem(generator, DesolationItems.HEART_OF_CINDER);
        generatedItem(generator, DesolationItems.MUSIC_DISC_ASHES);
        generatedItem(generator, DesolationItems.INFUSED_POWDER);
        generatedItem(generator, DesolationItems.PRIMED_ASH);
    }

    // === helpers ===

    /** Wraps a model output sink so every uploaded model JSON also carries a {@code render_type} field. */
    private static BiConsumer<Identifier, ModelInstance> withRenderType(BiConsumer<Identifier, ModelInstance> out, String renderType) {
        return (id, instance) -> out.accept(id, () -> {
            JsonObject json = instance.get().getAsJsonObject();
            json.addProperty("render_type", renderType);
            return json;
        });
    }

    private static MultiVariant plain(Identifier model) {
        return new MultiVariant(WeightedList.of(new Variant(model)));
    }

    private static MultiVariant random(Identifier... models) {
        WeightedList.Builder<Variant> builder = new WeightedList.Builder<>();
        for (Identifier model : models) {
            builder.add(new Variant(model));
        }
        return new MultiVariant(builder.build());
    }

    private static void simpleCubeAll(BlockModelGenerators generator, Block block) {
        Identifier model = ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(block), generator.modelOutput);
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plain(model)));
        generator.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model));
    }

    private static void singletonWithRenderType(BlockModelGenerators generator, Block block,
                                                TexturedModel.Provider provider, String renderType) {
        TexturedModel textured = provider.get(block);
        Identifier model = textured.getTemplate().create(block, textured.getMapping(), withRenderType(generator.modelOutput, renderType));
        generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plain(model)));
        generator.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model));
    }

    private static void parentedBlockItem(BlockModelGenerators generator, Block block) {
        generator.itemModelOutput.accept(block.asItem(),
                ItemModelUtils.plainModel(ModelTemplates.CUBE_ALL.getDefaultModelLocation(block)));
    }

    private static void generatedItem(ItemModelGenerators generator, Item item) {
        Identifier model = ModelTemplates.FLAT_ITEM.create(item, TextureMapping.layer0(item), generator.modelOutput);
        generator.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
    }

    @Override
    public String getName() {
        return "Desolation Models";
    }
}
