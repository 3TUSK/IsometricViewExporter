/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender;

import itemrender.client.RenderTickHandler;
import itemrender.client.export.ExportUtils;
import itemrender.client.keybind.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import info.tritusk.isometric.ConfigScreen;

@Mod("itemrender")
public class ItemRenderMod {
    
    private static final Logger LOGGER = LogManager.getLogger("Item Render");

    public ItemRenderMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ItemRenderMod::noticeOnServer);
        bus.addListener(ItemRenderMod::init);
        ForgeConfigSpec.Builder specBuilder;
        new ItemRenderConfig(specBuilder = new ForgeConfigSpec.Builder());
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.CLIENT, ItemRenderConfig.theSpec = specBuilder.build());
        context.registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (serverVer, isDedicated) -> true));
        context.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ConfigScreen::new);
    }

    private static void noticeOnServer(FMLDedicatedServerSetupEvent event) {
        LOGGER.warn("Item Render is meant to be client-side only. ");
        LOGGER.warn("Installing this mod on dedicated server will do nothing. ");
        LOGGER.warn("Please remove this mod and restart your server. ");
    }

    private static void init(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());
        ExportUtils.INSTANCE = new ExportUtils();
        KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(ItemRenderConfig.mainBlockSize.get(), "", GLFW.GLFW_KEY_LEFT_BRACKET, "item_render.key.block");
        RenderTickHandler.keybindToRender = defaultRender;
        MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(ItemRenderConfig.mainEntitySize.get(), "", GLFW.GLFW_KEY_SEMICOLON, "item_render.key.entity"));
        MinecraftForge.EVENT_BUS.register(new KeybindRenderEntity(ItemRenderConfig.gridEntitySize.get(), "_grid", GLFW.GLFW_KEY_APOSTROPHE, "item_render.key.entity_grid"));
        MinecraftForge.EVENT_BUS.register(defaultRender);
        MinecraftForge.EVENT_BUS.register(new KeybindRenderInventoryBlock(ItemRenderConfig.gridBlockSize.get(), "_grid", GLFW.GLFW_KEY_RIGHT_BRACKET, "item_render.key.block_grid"));
        MinecraftForge.EVENT_BUS.register(new KeybindToggleRender());
        MinecraftForge.EVENT_BUS.register(new KeybindRenderCurrentPlayer(ItemRenderConfig.playerSize.get()));
        MinecraftForge.EVENT_BUS.register(new KeybindExport());
    }

}
