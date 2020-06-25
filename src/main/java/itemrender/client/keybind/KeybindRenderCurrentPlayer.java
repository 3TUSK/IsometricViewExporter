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
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;

import org.lwjgl.glfw.GLFW;

public class KeybindRenderCurrentPlayer {

    private final KeyBinding key;
    private final int size;
    private FBOHelper fbo;

    public KeybindRenderCurrentPlayer(int textureSize) {
        this.size = textureSize;
        key = new KeyBinding("item_render.key.currentplayer", GLFW.GLFW_KEY_P, "item_render.key");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (key.isPressed() && mc.renderViewEntity instanceof LivingEntity) {
            if (this.fbo == null) {
                this.fbo = new FBOHelper(this.size);
                this.fbo.init();
            }
            Renderer.renderEntity((LivingEntity) mc.renderViewEntity, fbo, "", true);
            this.fbo.clear();
        }
    }
}
