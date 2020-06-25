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
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class KeybindRenderEntity {

    private final KeyBinding key;
    private final int size;
    private FBOHelper fbo;
    private String filenameSuffix = "";

    public KeybindRenderEntity(int textureSize, String filename_suffix, int keyVal, String des) {
        this.size = textureSize;
        filenameSuffix = filename_suffix;
        key = new KeyBinding(des, keyVal, "item_render.key");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (key.isPressed()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.pointedEntity instanceof LivingEntity) {
                if (this.fbo == null) {
                    this.fbo = new FBOHelper(this.size);
                    this.fbo.init();
                }
                Renderer.renderEntity((LivingEntity) mc.pointedEntity, fbo, filenameSuffix, false);
                this.fbo.clear();
            }
        }
    }
}
