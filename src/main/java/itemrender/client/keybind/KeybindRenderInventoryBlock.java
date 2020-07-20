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
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeybindRenderInventoryBlock {

    private final KeyBinding key;
    private final int size;
    public FBOHelper fbo;
    private String filenameSuffix = "";

    public KeybindRenderInventoryBlock(int textureSize, String filename_suffix, int keyVal, String des) {
        this.size = textureSize;
        filenameSuffix = filename_suffix;
        key = new KeyBinding(des, keyVal, "item_render.key");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (key.isPressed() && mc.player != null) {
            ItemStack current = mc.player.getHeldItemMainhand();
            if (!current.isEmpty()) {
                if (this.fbo == null) {
                    this.fbo = new FBOHelper(size);
                    this.fbo.init();
                }
                this.fbo.begin();
                if (Renderer.renderItem(current, this.fbo, filenameSuffix, mc.getItemRenderer())) {
                    mc.player.sendMessage(new StringTextComponent(String.format("Saved as rendered/item_%s%s.png", 
                        current.getItem().getRegistryName().toString().replace(':', '.'), filenameSuffix)));
                } else {
                    mc.player.sendMessage(new StringTextComponent("Failed to save rendered image, check your latest.log or debug.log for more information!"));
                }
                this.fbo.end();
                this.fbo.clear();
                
            }
        }
    }
}
