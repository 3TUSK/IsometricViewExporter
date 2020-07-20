package itemrender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ItemRenderConfig {

    public static final int DEFAULT_MAIN_BLOCK_SIZE = 128;
    public static final int DEFAULT_GRID_BLOCK_SIZE = 32;
    public static final int DEFAULT_MAIN_ENTITY_SIZE = 512;
    public static final int DEFAULT_GRID_ENTITY_SIZE = 128;
    public static final int DEFAULT_PLAYER_SIZE = 1024;

    public static ForgeConfigSpec theSpec;

    public static ForgeConfigSpec.IntValue mainBlockSize;
    public static ForgeConfigSpec.IntValue gridBlockSize;
    public static ForgeConfigSpec.IntValue mainEntitySize;
    public static ForgeConfigSpec.IntValue gridEntitySize;
    public static ForgeConfigSpec.IntValue playerSize;
    public static ForgeConfigSpec.BooleanValue debugMode;
    public static ForgeConfigSpec.DoubleValue renderScale;

    public static ForgeConfigSpec.BooleanValue exportVanillaItems;
    public static ForgeConfigSpec.BooleanValue useFancyPrinting;
    public static ForgeConfigSpec.ConfigValue<List<String>> blacklist;
    public static ForgeConfigSpec.ConfigValue<List<String>> langCodes;

    public ItemRenderConfig(ForgeConfigSpec.Builder specBuilder) {
        specBuilder.comment("Options used by Item Render Mod").push("General");
        mainBlockSize = specBuilder.comment("Main size of exported block image")
            .translation("item_render.config.main_block")
            .defineInRange("RenderBlockMain", DEFAULT_MAIN_BLOCK_SIZE, 16, Integer.MAX_VALUE);
        gridBlockSize = specBuilder.comment("Grid size of exported block image")
            .translation("item_render.config.grid_block")
            .defineInRange("RenderBlockGrid", DEFAULT_GRID_BLOCK_SIZE, 16, Integer.MAX_VALUE);
        mainEntitySize = specBuilder.comment("Main size of exported entity image")
            .translation("item_render.config.main_entity")
            .defineInRange("RenderEntityMain", DEFAULT_MAIN_ENTITY_SIZE, 512, Integer.MAX_VALUE);
        gridEntitySize = specBuilder.comment("Grid size of exported entity image")
            .translation("item_render.config.grid_entity")
            .defineInRange("RenderEntityGrid", DEFAULT_GRID_ENTITY_SIZE, 128, Integer.MAX_VALUE);
        playerSize = specBuilder.comment("Size of exported player image")
            .translation("item_render.config.player")
            .defineInRange("RenderPlayer", DEFAULT_PLAYER_SIZE, DEFAULT_PLAYER_SIZE, Integer.MAX_VALUE);
        debugMode = specBuilder.comment("Enable debug mode")
            .translation("item_render.config.debug")
            .define("DebugMode", false);
        renderScale = specBuilder.comment("Control entity/item rendering scale.")
            .translation("item_render.config.render_scale")
            .defineInRange("RenderScale", 1.0, 0.0, 2.0);
        specBuilder.pop();

        specBuilder.comment("Options for data exporting").push("Export");
        exportVanillaItems = specBuilder.comment("Export Vanilla items")
            .translation("item_render.config.vanilla")
            .define("ExportVanillaItems", false);
        useFancyPrinting = specBuilder.comment("Exported JSON files will have nice indentation if this set to true.",
            "Default to false to reduce the size of files.")
            .translation("item_render.config.fancy_printing")
            .define("FancyPrinting", false);
        blacklist = specBuilder.comment("Export blacklist, use registry name of blocks/items")
            .translation("item_render.config.blacklist")
            .define("Blacklist", Collections.emptyList());
        langCodes = specBuilder.comment("List of language codes that are to be used when exporting")
            .translation("item_render.config.lang_codes")
            .define("LangCodes", Arrays.asList("en_us", "ja_jp", "zh_cn"));
        specBuilder.pop();
    }
}