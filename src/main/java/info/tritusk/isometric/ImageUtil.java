/*
 * This is free and unencumbered software released into the public domain.
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */

// SPDX-Identifier: Unlicense

package info.tritusk.isometric;

import java.io.File;
import java.nio.file.Path;
import java.util.Base64;

import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;

public final class ImageUtil {

    private static final Logger LOGGER = LogManager.getLogger("ImageUtil");

    public static NativeImage dumpFrom(Framebuffer frame) {
        NativeImage img = new NativeImage(frame.framebufferWidth, frame.framebufferHeight, true);
        RenderSystem.bindTexture(frame.framebufferTexture);
        img.downloadFromTexture(0, true);
        img.flip();
        return img;
    }

    public static void saveImage(NativeImage img, File dest) {
        saveImage(img, dest.toPath());
    }

    public static void saveImage(NativeImage img, Path path) {
        try {
            img.write(path);
        } catch (Exception e) {
            LOGGER.error("Error occured while saving image to {}. Details: ", path);
            LOGGER.catching(e);
        } finally {
            img.close();
        }
    }

    public static String base64(NativeImage img) {
        return base64(img, false);
    }

    public static String base64(NativeImage img, boolean urlSafe) {
        try {
            return (urlSafe ? Base64.getUrlEncoder() : Base64.getEncoder()).encodeToString(img.getBytes());
        } catch (Exception e) {
            LOGGER.error("Error occured while computing base64, will return empty string. Details: ");
            LOGGER.catching(e);
            return "";
        } finally {
            img.close();
        }
    }
    
}