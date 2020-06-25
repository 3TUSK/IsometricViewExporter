/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.client.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import itemrender.ItemRenderConfig;
import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ExportUtils {
    
    public static ExportUtils INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger("Item Render");
    private static final Marker MARKER = MarkerManager.getMarker("ExportUtil");

    private FBOHelper fboSmall;
    private FBOHelper fboLarge;
    private FBOHelper fboEntity;
    private ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    private List<ItemData> itemDataList = new ArrayList<ItemData>();
    private List<MobData> mobDataList = new ArrayList<MobData>();

    private boolean init = false;

    public void init() {
        if (init) {
            return;
        }
        init = true;
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        // Hardcoded value for mcmod.cn only, don't change this unless the website updates
        fboSmall = new FBOHelper(32);
        fboLarge = new FBOHelper(128);
        fboEntity = new FBOHelper(200);
    }


    public String getLocalizedName(ItemStack itemStack) {
        return itemStack.getDisplayName().getString();
    }

    public String getSmallIcon(ItemStack itemStack) {
        return Renderer.getItemBase64(itemStack, fboSmall, itemRenderer);
    }

    public String getLargeIcon(ItemStack itemStack) {
        return Renderer.getItemBase64(itemStack, fboLarge, itemRenderer);
    }

    public String getEntityIcon(EntityType<?> Entitymob){
        return Renderer.getEntityBase64(Entitymob, fboEntity);
    }
    
    private String getItemOwner(ItemStack itemStack) {
        ResourceLocation registryName = itemStack.getItem().getRegistryName();
        return registryName == null ? "unnamed" : registryName.getNamespace();
    }
    private String getEntityOwner(EntityType<?> entityType) {
        ResourceLocation registryName = entityType.getRegistryName();
        return registryName == null ? "unnamed" : registryName.getNamespace();
    }
    
    public void exportMods() throws IOException{
        Minecraft minecraft = Minecraft.getInstance();
        itemDataList.clear();
        mobDataList.clear();
        List<String> modList = new ArrayList<String>();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        ItemData itemData;
        MobData mobData;
        String identifier;

        for (Item item : ForgeRegistries.ITEMS) {
            String owner = item.getRegistryName().getNamespace();
            if ("minecraft".equals(owner) && !ItemRenderConfig.exportVanillaItems.get())
                continue;
            identifier = item.getRegistryName().toString();//.getUnlocalizedName() + "@" + itemStack.getMetadata();
            if (ItemRenderConfig.blacklist.get().contains(identifier)) continue;

            itemData = new ItemData(new ItemStack(item));
            itemDataList.add(itemData);
            if (!modList.contains(owner)) modList.add(owner);
        }
        for (EntityType<?> Entity : ForgeRegistries.ENTITIES) {
            if (Entity == null) continue;
            if (!(Entity.create(minecraft.world) instanceof LivingEntity)) continue;
            if (getEntityOwner(Entity).equals("minecraft") && !ItemRenderConfig.exportVanillaItems.get()) continue;

            mobData = new MobData(Entity);
            mobDataList.add(mobData);
            if (!modList.contains(getEntityOwner(Entity))) modList.add(getEntityOwner(Entity));
        }
        
        // Since refreshResources takes a long time, only refresh once for all the items
        minecraft.getLanguageManager().setCurrentLanguage(new Language("zh_cn", "中国", "简体中文", false));
        minecraft.gameSettings.language = "zh_cn";
        minecraft.getLanguageManager().onResourceManagerReload(minecraft.getResourceManager());
        minecraft.gameSettings.saveOptions();

        for (ItemData data : itemDataList) {
            if (ItemRenderConfig.debugMode.get())
                LOGGER.info(MARKER, I18n.format("itemrender.msg.addCN", data.getItemStack().getItem().getRegistryName()));
            data.setName(this.getLocalizedName(data.getItemStack()));
        }
        for (MobData data : mobDataList) {
            if (ItemRenderConfig.debugMode.get())
                LOGGER.info(MARKER, I18n.format("itemrender.msg.addCN", data.getMob().getRegistryName()));
            data.setName(I18n.format(data.getMob().getTranslationKey(), ObjectArrays.EMPTY_ARRAY));
        }

        minecraft.getLanguageManager().setCurrentLanguage(new Language("en_us", "US", "English", false));
        minecraft.gameSettings.language = "en_us";
        minecraft.getLanguageManager().onResourceManagerReload(minecraft.getResourceManager());
        minecraft.gameSettings.saveOptions();

        for (ItemData data : itemDataList) {
            if (ItemRenderConfig.debugMode.get())
                LOGGER.info(MARKER, I18n.format("itemrender.msg.addEN", data.getItemStack().getItem().getRegistryName()));
            data.setEnglishName(this.getLocalizedName(data.getItemStack()));
        }
        
        for (MobData data : mobDataList) {
            if (ItemRenderConfig.debugMode.get())
                LOGGER.info(MARKER, I18n.format("itemrender.msg.addEN", data.getMob().getRegistryName()));
            data.setEnglishname(I18n.format(data.getMob().getTranslationKey(), ObjectArrays.EMPTY_ARRAY));
        }
        
        File export;
        File export1;
        for (String modid : modList) {
            // Since 1.13+, namespace part of ResourceLocation MUST match regex [A-Za-z0-9.-_]+, 
            // so sanitization is no longer needed. We just concat them together.
            export = new File(minecraft.gameDir, "export/" + modid + "_item.json");
            if (!export.getParentFile().exists()) export.getParentFile().mkdirs();
            if (!export.exists()) export.createNewFile();
            PrintWriter pw = new PrintWriter(export, "UTF-8");

            for (ItemData data : itemDataList) {
                if (modid.equals(getItemOwner(data.getItemStack())))
                    pw.println(gson.toJson(data));
            }
            pw.close();

        }
        for (String modid : modList) {
        export1 = new File(minecraft.gameDir, "export/" + modid + "_entity.json");
        if (!export1.getParentFile().exists()) export1.getParentFile().mkdirs();
        if (!export1.exists()) export1.createNewFile();
        PrintWriter pw1 = new PrintWriter(export1, "UTF-8");

        for (MobData data : mobDataList) {
            if (modid.equals(getEntityOwner(data.getMob())))
                pw1.println(gson.toJson(data));
        }
        pw1.close();
        }
    }
}
