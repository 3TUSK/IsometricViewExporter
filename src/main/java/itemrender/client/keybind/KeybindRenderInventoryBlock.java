/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender.client.keybind;


import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeybindRenderInventoryBlock {

    public final KeyBinding key;
    /**
     * Key descriptions
     */
    private final String desc;
    /**
     * Default key values
     */
    private final int keyValue;
    public FBOHelper fbo;
    private String filenameSuffix = "";
    private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

    public KeybindRenderInventoryBlock(int textureSize, String filename_suffix, int keyVal, String des) {
        fbo = new FBOHelper(textureSize);
        filenameSuffix = filename_suffix;
        keyValue = keyVal;
        desc = des;
        key = new KeyBinding(desc, keyValue, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
            return;
        if (key.isPressed()) {
            Minecraft minecraft = FMLClientHandler.instance().getClient();
            if (minecraft.thePlayer != null) {
                ItemStack current = minecraft.thePlayer.getCurrentEquippedItem();
                if (current != null && current.getItem() != null) {
                    Renderer.renderItem(current, fbo, filenameSuffix, itemRenderer);
                }
            }
        }
    }
}