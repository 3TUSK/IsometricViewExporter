package itemrender.export.neo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import itemrender.ItemRenderConfig;
import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class ExportWorker implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger("Item Render");
    private static final Marker MARKER = MarkerManager.getMarker("ExportWorker");

    private static FBOHelper itemFrame, entityFrame;
    private static boolean firstRun = true;

    private static void init() {
        (itemFrame = new FBOHelper(32)).init();
        (entityFrame = new FBOHelper(200)).init();
    }

    @Override
    public void run() {
        if (firstRun) {
            firstRun = false;
            init();
        }
        LOGGER.debug(MARKER, "Export process has started.");
        final Gson gson;
        if (ItemRenderConfig.useFancyPrinting.get()) {
            gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        } else {
            gson = new GsonBuilder().disableHtmlEscaping().create();
        }
        boolean exportVanilla = ItemRenderConfig.exportVanillaItems.get();
        Minecraft mc = Minecraft.getInstance();
        LanguageManager langManager = mc.getLanguageManager();
        String langCode = langManager.getCurrentLanguage().getCode();
        HashMap<String, ArrayList<ItemEntry>> itemData = new HashMap<>();
        HashMap<String, ArrayList<EntityEntry>> entityData = new HashMap<>();
        
        LOGGER.debug(MARKER, "Dumping item data");
        itemFrame.begin();
        for (Item item : ForgeRegistries.ITEMS) {
            String owner = item.asItem().getRegistryName().getNamespace();
            if ("minecraft".equals(owner) && !exportVanilla) {
                continue;
            }
            ArrayList<ItemEntry> entries = itemData.computeIfAbsent(owner, s -> new ArrayList<>());
            ItemEntry e = new ItemEntry();
            if (item instanceof BlockItem) {
                e.type = "block";
            }
            e.registryName = item.getRegistryName().toString();
            ItemStack instance = e.example = new ItemStack(item);
            e.maxStackSize = instance.getMaxStackSize();
            e.maxDamage = instance.getMaxDamage();
            (e.name = new Object2ObjectArrayMap<>(2)).put(langCode, I18n.format(instance.getDisplayName().getFormattedText(), ObjectArrays.EMPTY_ARRAY));
            (e.icon = new Object2ObjectArrayMap<>(2)).put("small", Renderer.getItemBase64(e.example, itemFrame, mc.getItemRenderer()));
            itemFrame.clear();
            entries.add(e);
        }
        itemFrame.end();

        itemFrame.resize(128);
        itemFrame.begin();
        for (ArrayList<ItemEntry> list : itemData.values()) {
            for (ItemEntry e : list) {
                e.icon.put("large", Renderer.getItemBase64(e.example, itemFrame, mc.getItemRenderer()));
                itemFrame.clear();
            }
        }
        itemFrame.end();
        itemFrame.resize(32);
        LOGGER.debug(MARKER, "Dumped item data");

        LOGGER.debug(MARKER, "Dumping entity data");
        entityFrame.begin();
        for (EntityType<?> entityType : ForgeRegistries.ENTITIES) {
            String owner = entityType.getRegistryName().getNamespace();
            if ("minecraft".equals(owner) && !exportVanilla) {
                continue;
            }
            ArrayList<EntityEntry> entries = entityData.computeIfAbsent(owner, s -> new ArrayList<>());
            EntityEntry e = new EntityEntry();
            e.entityType = entityType;
            e.registryName = entityType.getRegistryName().toString();
            (e.name = new Object2ObjectArrayMap<>(2)).put(langCode, I18n.format(entityType.getTranslationKey(), ObjectArrays.EMPTY_ARRAY));
            e.icon = Renderer.getEntityBase64(entityType, entityFrame);
            // Hack: create a fake entity to determine if it is "living".
            e.living = entityType.create(mc.world) instanceof LivingEntity;
            entries.add(e);
        }
        entityFrame.end();
        LOGGER.debug(MARKER, "Dumped item data");

        LOGGER.debug(MARKER, "Dumping translations");
        // Hack again: force reloading language to get translations
        langManager.setCurrentLanguage(langManager.getLanguage("zh_cn"));
        langManager.onResourceManagerReload(mc.getResourceManager());
        itemData.values().forEach(c -> c.forEach(e -> e.name.put("zh_cn", e.example.getDisplayName().getFormattedText())));
        entityData.values().forEach(c -> c.forEach(e -> e.name.put("zh_cn", I18n.format(e.entityType.getTranslationKey(), ObjectArrays.EMPTY_ARRAY))));
        // Recover
        langManager.setCurrentLanguage(langManager.getLanguage(langCode));
        langManager.onResourceManagerReload(mc.getResourceManager());
        LOGGER.debug(MARKER, "Dumped translations");

        LOGGER.debug(MARKER, "Locating export root");
        final Path exportRoot = mc.gameDir.toPath().resolve("export");
        if (!Files.isDirectory(exportRoot)) {
            try {
                Files.createDirectory(exportRoot);
            } catch (Exception e) {
                LOGGER.error(MARKER, "Error occured while creating {}. Details: ", exportRoot);
                LOGGER.catching(e);
                return;
            }
        }

        LOGGER.debug(MARKER, "Writing out item data");
        itemData.forEach((modId, entries) -> {
            final Path modRoot = exportRoot.resolve(modId);
            if (!Files.isDirectory(modRoot)) {
                try {
                    Files.createDirectory(modRoot);
                } catch (Exception e) {
                    LOGGER.error("Error occured while creating {}. Details: ", modRoot);
                    LOGGER.catching(e);
                    return;
                }
            }
            for (ItemEntry entry : entries) {
                final String name = entry.registryName.substring(entry.registryName.indexOf(':') + 1);
                final Path dest = modRoot.resolve(entry.type).resolve(name.concat(".json"));
                if (!Files.isDirectory(dest.getParent())) {
                    try {
                        Files.createDirectories(dest.getParent());
                    } catch (Exception e) {
                        LOGGER.error("Error occured while creating {}. Details: ", dest.getParent());
                        LOGGER.catching(e);
                    }
                }
                try {
                    Files.write(dest, gson.toJson(entry).getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    LOGGER.error(MARKER, "Error occured while creating {}. Details: ", dest);
                    LOGGER.catching(e);
                }
            }
        });
        LOGGER.debug(MARKER, "Writing out entity data");
        entityData.forEach((modId, entries) -> {
            final Path modRoot = exportRoot.resolve(modId);
            if (!Files.isDirectory(modRoot)) {
                try {
                    Files.createDirectory(modRoot);
                } catch (Exception e) {
                    LOGGER.error("Error occured while creating {}. Details: ", modRoot);
                    LOGGER.catching(e);
                    return;
                }
            }
            for (EntityEntry entry : entries) {
                final String name = entry.registryName.substring(entry.registryName.indexOf(':') + 1);
                final Path dest = modRoot.resolve(entry.type).resolve(name.concat(".json"));
                if (!Files.isDirectory(dest.getParent())) {
                    try {
                        Files.createDirectories(dest.getParent());
                    } catch (Exception e) {
                        LOGGER.error("Error occured while creating {}. Details: ", dest.getParent());
                        LOGGER.catching(e);
                    }
                }
                try {
                    Files.write(dest, gson.toJson(entry).getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    LOGGER.error(MARKER, "Error occured while creating {}. Details: ", dest);
                    LOGGER.catching(e);
                }
            }
        });
        LOGGER.debug(MARKER, "Export finished");
        mc.player.sendMessage(new StringTextComponent("Export finished."));
    }

}