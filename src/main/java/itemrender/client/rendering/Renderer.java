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

import itemrender.ItemRenderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

import info.tritusk.isometric.ImageUtil;

import java.io.File;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

/**
 * Created by Jerrell Fang on 2/23/2015.
 *
 * @author Meow J
 */
public class Renderer {

    private static void doRenderEntity(LivingEntity entity, FBOHelper fbo, boolean renderPlayer) {
        double scale = ItemRenderConfig.renderScale.get();
        fbo.begin();

        AxisAlignedBB aabb = entity.getBoundingBox();
        double minX = aabb.minX - entity.getPosX();
        double maxX = aabb.maxX - entity.getPosX();
        double minY = aabb.minY - entity.getPosY();
        double maxY = aabb.maxY - entity.getPosY();
        double minZ = aabb.minZ - entity.getPosZ();
        double maxZ = aabb.maxZ - entity.getPosZ();

        double minBound = Math.min(minX, Math.min(minY, minZ));
        double maxBound = Math.max(maxX, Math.max(maxY, maxZ));

        double boundLimit = Math.max(Math.abs(minBound), Math.abs(maxBound));

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.ortho(-boundLimit * 0.75, boundLimit * 0.75, boundLimit * 0.25, -boundLimit * 1.25, -100D, 100D);

        RenderSystem.matrixMode(GL11.GL_MODELVIEW);

        // Render entity
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0F, 0F, 50F);

        if (renderPlayer)
            RenderSystem.scaled(-1.0, 1.0, 1.0);
        else
            RenderSystem.scaled(-scale, scale, scale);

        MatrixStack transform = new MatrixStack();
        transform.rotate(Vector3f.ZP.rotationDegrees(180F)); 
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        RenderHelper.enableStandardItemLighting();

        transform.rotate(Vector3f.XP.rotation((float) Math.asin(Math.tan(Math.toRadians(30)))));
        transform.rotate(Vector3f.YN.rotationDegrees(45F));

        entity.renderYawOffset = (float) Math.atan(1 / 40.0) * 20.0F;
        entity.rotationYaw = (float) Math.atan(1 / 40.0) * 40.0F;
        entity.rotationPitch = -((float) Math.atan(1 / 40.0)) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
        /*manager.setPlayerViewY(180.0F);*/ // Not sure, but my test result shows that it is no longer needed
        manager.setRenderShadow(false);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        manager.renderEntityStatic(entity, 0D, 0D, 0D, 0F, 1F, transform, buffer, 0x00F000F0);
        buffer.finish();
        manager.setRenderShadow(true);
        entity.renderYawOffset = f2;
        entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = f5;
        entity.rotationYawHead = f6;
        RenderSystem.popMatrix();
        RenderHelper.disableStandardItemLighting();

        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();

        fbo.end();
    }

    private static void doRenderItem(ItemStack itemStack, FBOHelper fbo, ItemRenderer itemRenderer) {
        double scale = ItemRenderConfig.renderScale.get();
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0D, 16D, 16D, 0D, -150D, 150D);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderHelper.setupGui3DDiffuseLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableColorMaterial();
        RenderSystem.enableLighting();
        RenderSystem.translated(8 * (1 - scale), 8 * (1 - scale), 0);
        RenderSystem.scaled(scale, scale, scale);
        itemRenderer.renderItemIntoGUI(itemStack, 0, 0);
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableLighting();
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();
    }

    public static boolean renderItem(ItemStack stack, FBOHelper fbo, String suffix, ItemRenderer itemRenderer) {
        Minecraft mc = Minecraft.getInstance();
        doRenderItem(stack, fbo, itemRenderer);
        try (NativeImage image = ImageUtil.dumpFrom(fbo.frame)) {
            return ImageUtil.saveImage(image, new File(mc.gameDir, String.format("rendered/item_%s_%s.png", 
                stack.getItem().getRegistryName().toString().replace(':', '.'), suffix)));
        } finally {
            fbo.unbindTexture();
        }
    }

    public static String getItemBase64(ItemStack stack, FBOHelper fbo, ItemRenderer itemRenderer) {
        doRenderItem(stack, fbo, itemRenderer);
        try (NativeImage image = ImageUtil.dumpFrom(fbo.frame)) {
            return ImageUtil.base64(image);
        } finally {
            fbo.unbindTexture();
        }
    }

    public static boolean renderEntity(LivingEntity entity, FBOHelper fbo, String filenameSuffix, boolean renderPlayer) {
        Minecraft mc = Minecraft.getInstance();
        doRenderEntity(entity, fbo, renderPlayer);
        String name = entity.getType().getRegistryName().toString().replace(':', '.');
        try (NativeImage image = ImageUtil.dumpFrom(fbo.frame)) {
            return ImageUtil.saveImage(image, new File(mc.gameDir, renderPlayer ? "rendered/player.png"
                : String.format("rendered/entity_%s%s.png", name, filenameSuffix)));
        } finally {
            fbo.unbindTexture();
        }
    }

    public static String getEntityBase64(EntityType<?> type, FBOHelper fbo) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity entity = type.create(minecraft.world);
        if (entity instanceof LivingEntity) {
            doRenderEntity((LivingEntity) entity, fbo, false);
            try (NativeImage image = ImageUtil.dumpFrom(fbo.frame)) {
                return ImageUtil.base64(image);
            } finally {
                fbo.unbindTexture();
            }
        } else {
            return "";
        }
    }
}
