package raltsmc.desolation.registry;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import raltsmc.desolation.block.*;
import raltsmc.desolation.world.feature.DesolationConfiguredFeatures;

import java.util.Optional;

public final class DesolationBlocks {
    public static Block CHARRED_SOIL;
    public static Block COOLED_EMBER_BLOCK;
    public static Block EMBER_BLOCK;
    public static Block ASH_BLOCK;
    public static Block ASH_LAYER_BLOCK;
    public static Block ACTIVATED_CHARCOAL_BLOCK;
    public static Block SCORCHED_TUFT;
    public static Block ASH_BRAMBLE;
    public static Block CHARRED_BRANCHES;
    public static Block CHARRED_LOG;
    public static Block CHARRED_WOOD;
    public static Block STRIPPED_CHARRED_LOG;
    public static Block STRIPPED_CHARRED_WOOD;
    public static Block CHARRED_PLANKS;
    public static Block CHARRED_SAPLING;
    public static Block POTTED_CHARRED_SAPLING;
    public static Block CINDERFRUIT_PLANT;

	public static StandingSignBlock CHARRED_SIGN;
	public static WallSignBlock CHARRED_WALL_SIGN;
	public static CeilingHangingSignBlock CHARRED_HANGING_SIGN;
	public static WallHangingSignBlock CHARRED_WALL_HANGING_SIGN;

    public static Block CHARRED_SLAB;
    public static Block CHARRED_STAIRS;
    public static Block CHARRED_PRESSURE_PLATE;
    public static Block CHARRED_TRAPDOOR;
    public static Block CHARRED_FENCE;
    public static Block CHARRED_FENCE_GATE;
    public static Block CHARRED_BUTTON;
    public static Block CHARRED_DOOR;

    @SuppressWarnings("UnnecessaryReturnStatement")
    private DesolationBlocks() {
        return;
    }

    static void init() {
        CHARRED_SOIL = DesolationRegistries.register("charred_soil", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).mapColor(MapColor.COLOR_GRAY).sound(SoundType.GRAVEL));
        COOLED_EMBER_BLOCK = DesolationRegistries.register("cooled_ember_block", CooledEmberBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.DEEPSLATE).requiresCorrectToolForDrops());
        EMBER_BLOCK = DesolationRegistries.register("ember_block", properties -> new EmberBlock(COOLED_EMBER_BLOCK, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_ORANGE).lightLevel(state -> 8).requiresCorrectToolForDrops());
        ASH_BLOCK = DesolationRegistries.register("ash_block", AshBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops());
        ASH_LAYER_BLOCK = DesolationRegistries.register("ash", AshLayerBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(MapColor.COLOR_GRAY).strength(0.3f).requiresCorrectToolForDrops());
        ACTIVATED_CHARCOAL_BLOCK = DesolationRegistries.register("activated_charcoal_block", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.BASALT).mapColor(MapColor.COLOR_BLACK).strength(0.5f).requiresCorrectToolForDrops());
        SCORCHED_TUFT = DesolationRegistries.register("scorched_tuft", ScorchedTuftBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS).mapColor(MapColor.COLOR_GRAY).sound(SoundType.CROP));
        ASH_BRAMBLE = DesolationRegistries.register("ash_bramble", AshBrambleBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).mapColor(MapColor.COLOR_GRAY).strength(0.3f).sound(SoundType.CROP));
        CHARRED_BRANCHES = DesolationRegistries.register("charred_branches", CharredBranchBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).mapColor(MapColor.COLOR_GRAY).strength(0.3f).sound(SoundType.VINE).isValidSpawn((state, world, pos, entityType) -> false));
        CHARRED_LOG = DesolationRegistries.register("charred_log", CharredLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).mapColor(MapColor.COLOR_GRAY).strength(1.8f).sound(SoundType.BASALT));
        CHARRED_WOOD = DesolationRegistries.register("charred_wood", CharredLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_GRAY).strength(1.8f).sound(SoundType.BASALT));
        STRIPPED_CHARRED_LOG = DesolationRegistries.register("stripped_charred_log", CharredLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.COLOR_GRAY).strength(1.8f));
        STRIPPED_CHARRED_WOOD = DesolationRegistries.register("stripped_charred_wood", CharredLogBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).mapColor(MapColor.COLOR_GRAY).strength(1.8f));
        CHARRED_PLANKS = DesolationRegistries.register("charred_planks", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_GRAY));
        CHARRED_SAPLING = DesolationRegistries.register("charred_sapling", properties -> new CharredSaplingBlock(new TreeGrower("charred", Optional.empty(), Optional.of(DesolationConfiguredFeatures.TREE_CHARRED), Optional.empty()), properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));
        POTTED_CHARRED_SAPLING = DesolationRegistries.register("potted_charred_sapling", properties -> new FlowerPotBlock(CHARRED_SAPLING, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING).mapColor(MapColor.COLOR_GRAY));
        CINDERFRUIT_PLANT = DesolationRegistries.register("cinderfruit_plant", CinderfruitPlantBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).mapColor(MapColor.TERRACOTTA_GRAY).strength(0.1f).lightLevel(state -> 10).sound(SoundType.CROP));

        CHARRED_SIGN = DesolationRegistries.registerSignBlock("charred_sign", properties -> new StandingSignBlock(DesolationWoodTypes.CHARRED, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN));
        CHARRED_WALL_SIGN = DesolationRegistries.registerSignBlock("charred_wall_sign", properties -> new WallSignBlock(DesolationWoodTypes.CHARRED, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).overrideLootTable(CHARRED_SIGN.getLootTable()));
        CHARRED_HANGING_SIGN = DesolationRegistries.registerSignBlock("charred_hanging_sign", properties -> new CeilingHangingSignBlock(DesolationWoodTypes.CHARRED, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN));
        CHARRED_WALL_HANGING_SIGN = DesolationRegistries.registerSignBlock("charred_wall_hanging_sign", properties -> new WallHangingSignBlock(DesolationWoodTypes.CHARRED, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).overrideLootTable(CHARRED_HANGING_SIGN.getLootTable()));

        CHARRED_SLAB = DesolationRegistries.register("charred_slab", SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).mapColor(MapColor.COLOR_GRAY));
        CHARRED_STAIRS = DesolationRegistries.register("charred_stairs", properties -> new StairBlock(CHARRED_PLANKS.defaultBlockState(), properties), BlockBehaviour.Properties.ofFullCopy(CHARRED_PLANKS));
        CHARRED_PRESSURE_PLATE = DesolationRegistries.register("charred_pressure_plate", properties -> new PressurePlateBlock(DesolationBlockSetTypes.CHARRED, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE).mapColor(MapColor.COLOR_GRAY));
        CHARRED_TRAPDOOR = DesolationRegistries.register("charred_trapdoor", properties -> new TrapDoorBlock(DesolationBlockSetTypes.CHARRED, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).mapColor(MapColor.COLOR_GRAY).strength(3.0f).isValidSpawn((state, world, pos, entityType) -> false));
        CHARRED_FENCE = DesolationRegistries.register("charred_fence", FenceBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE).mapColor(MapColor.COLOR_GRAY));
        CHARRED_FENCE_GATE = DesolationRegistries.register("charred_fence_gate", properties -> new FenceGateBlock(DesolationWoodTypes.CHARRED, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE).mapColor(MapColor.COLOR_GRAY));
        CHARRED_BUTTON = DesolationRegistries.register("charred_button", properties -> new ButtonBlock(DesolationBlockSetTypes.CHARRED, 30, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON).mapColor(MapColor.COLOR_GRAY));
        CHARRED_DOOR = DesolationRegistries.register("charred_door", properties -> new DoorBlock(DesolationBlockSetTypes.CHARRED, properties), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).mapColor(MapColor.COLOR_GRAY));

        addFlammables();
        addStrippables();
    }

    private static void addFlammables() {
        FlammableBlockRegistry flammableRegistry = FlammableBlockRegistry.getDefaultInstance();

        // TODO: Need to ponder this some more...
        //flammableRegistry.add(CHARRED_BRANCHES, 30, 60);
    }

    private static void addStrippables() {
        StrippableBlockRegistry.register(CHARRED_LOG, STRIPPED_CHARRED_LOG);
        StrippableBlockRegistry.register(CHARRED_WOOD, STRIPPED_CHARRED_WOOD);
    }
}
