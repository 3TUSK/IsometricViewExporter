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

import itemrender.client.RenderTickHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;


import org.lwjgl.glfw.GLFW;

public class KeybindToggleRender {

    public final KeyBinding key;

    public KeybindToggleRender() {
        key = new KeyBinding("item_render.key.toggle", GLFW.GLFW_KEY_O, "item_render.key");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (key.isPressed()) {
            RenderTickHandler.renderPreview = !RenderTickHandler.renderPreview;
        }
    }
}
