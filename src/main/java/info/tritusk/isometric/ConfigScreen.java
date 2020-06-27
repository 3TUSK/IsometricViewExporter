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

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import itemrender.ItemRenderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public final class ConfigScreen extends Screen {

    private static final ITextComponent TITLE = new TranslationTextComponent("item_render.config", ObjectArrays.EMPTY_ARRAY);

    private final Screen parent;
    private ConfigList config;

    public ConfigScreen(Minecraft mc, Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.children.add(this.config = new ConfigList(this.minecraft, this));
        this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("item_render.gui.config.reset"), button -> {

        }));
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("item_render.gui.config.save"), button -> this.onClose()));
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        this.renderDirtBackground(0); // TODO What is it anyway?
        this.config.render(mouseX, mouseY, partialTick);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 0x00FFFFFF);
        super.render(mouseX, mouseY, partialTick);
    }

    @Override
    public void removed() {
        ItemRenderConfig.theSpec.save();
    }

    @Override
    public void onClose() {
        // Return to last screen.
        // If last screen is the game screen, we technically should just 
        // close our screen.
        this.minecraft.displayGuiScreen(this.parent instanceof IngameMenuScreen ? null : this.parent);
    }

}