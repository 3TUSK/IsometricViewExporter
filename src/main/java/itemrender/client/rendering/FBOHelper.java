/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender.client.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

public final class FBOHelper {

    Framebuffer frame;

    private int size = 128;

    public FBOHelper(int textureSize) {
        this.size = textureSize;
    }

    public void resize(int newSize) {
        this.frame.resize(this.size = newSize, newSize, true);
    }

    public void init() {
        if (this.frame == null) {
            this.frame = new Framebuffer(this.size, this.size, true, Minecraft.IS_RUNNING_ON_MAC);
        }
    }

    public void begin() {
        this.frame.bindFramebuffer(true);
        RenderSystem.enableTexture();
        this.frame.bindFramebufferTexture();

        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();

        RenderSystem.clearColor(1F, 1F, 1F, 1F);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, true); // We do check error here
        // We cannot call this because this call unbinds the current framebuffer!
        //this.frame.setFramebufferColor(0F, 0F, 0F, 0F);
        //this.frame.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);

        // GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        RenderSystem.enableDepthTest();
        RenderSystem.enableLighting();
        RenderSystem.enableRescaleNormal();
    }

    public void end() {
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        // GlStateManager.cullFace(GlStateManager.CullFace.BACK);

        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.popMatrix();

        this.frame.unbindFramebuffer();
    }

    public void clear() {
        RenderSystem.clearColor(1F, 1F, 1F, 1F);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, true); 
    }

    public void bindTexture() {
        this.frame.bindFramebufferTexture();
    }

    // This is only a separate function because the texture gets messed with
    // after you're done rendering to read the FBO
    public void unbindTexture() {
        this.frame.unbindFramebufferTexture();
    }

}
