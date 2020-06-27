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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.ObjectArrays;
import itemrender.ItemRenderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.ForgeConfigSpec;

public final class ConfigList extends AbstractOptionList<ConfigList.ConfigEntry<?>> {

    public ConfigList(Minecraft mc, ConfigScreen screen) {
        // width, height, top, bottom, itemHeight
        super(mc, screen.width + 45, screen.height, 40, screen.height - 32, 20);
        
        this.addEntry(new IntConfigEntry(ItemRenderConfig.mainBlockSize, mc.fontRenderer));
        this.addEntry(new IntConfigEntry(ItemRenderConfig.gridBlockSize, mc.fontRenderer));
        this.addEntry(new IntConfigEntry(ItemRenderConfig.mainEntitySize, mc.fontRenderer));
        this.addEntry(new IntConfigEntry(ItemRenderConfig.gridEntitySize, mc.fontRenderer));
        this.addEntry(new IntConfigEntry(ItemRenderConfig.playerSize, mc.fontRenderer));
        this.addEntry(new BooleanConfigEntry(ItemRenderConfig.debugMode, mc.fontRenderer));
        this.addEntry(new DoubleConfigEntry(ItemRenderConfig.renderScale, mc.fontRenderer));
        this.addEntry(new BooleanConfigEntry(ItemRenderConfig.exportVanillaItems, mc.fontRenderer));
        this.addEntry(new BooleanConfigEntry(ItemRenderConfig.useFancyPrinting, mc.fontRenderer));
    }

    public static abstract class ConfigEntry<E> extends AbstractOptionList.Entry<ConfigEntry<?>> {
        protected final FontRenderer font;
        protected final ForgeConfigSpec.ConfigValue<E> configEntry;
        protected final ForgeConfigSpec.ValueSpec configEntrySpec;

        protected ConfigEntry(ForgeConfigSpec.ConfigValue<E> configEntry, FontRenderer fontRenderer) {
            this.font = fontRenderer;
            this.configEntry = configEntry;
            this.configEntrySpec = ItemRenderConfig.theSpec.get(configEntry.getPath());
        }
    }

    public static abstract class NumberConfigEntry<T extends Number> extends ConfigEntry<T> {
        protected final TextFieldWidget input;

        protected NumberConfigEntry(ForgeConfigSpec.ConfigValue<T> configEntry, Function<String, T> parser, FontRenderer fontRenderer) {
            super(configEntry, fontRenderer);
            this.input = new TextFieldWidget(fontRenderer, 0, 0, 60, 20, "");
            this.input.setEnabled(true);
            this.input.setText(this.configEntry.get().toString());
            this.input.setResponder(input -> {
                try {
                    configEntry.set(parser.apply(input));
                } catch (Exception ignored) {}
            });
        }

        @Override
        public List<? extends IGuiEventListener> children() {
            return Collections.singletonList(this.input);
        }

        @Override
        public void render(int itemNum, int guiTop, int guiLeft, int width, int height, int mouseX, int mouseY, boolean isWithinRegion, float partialTick) {
            this.font.drawString(I18n.format(this.configEntrySpec.getTranslationKey(), ObjectArrays.EMPTY_ARRAY), guiLeft - 60, guiTop + 4, 0x00FFFFFF);
            this.input.x = guiLeft + 150;
            this.input.y = guiTop;
            this.input.setWidth(width / 3);
            this.input.setHeight(height);
            this.input.render(mouseX, mouseY, partialTick);
        }
    }

    public static final class IntConfigEntry extends NumberConfigEntry<Integer> {
        public IntConfigEntry(ForgeConfigSpec.IntValue configEntry, FontRenderer fontRenderer) {
            super(configEntry, Integer::parseInt, fontRenderer);
        }
    }

    public static final class DoubleConfigEntry extends NumberConfigEntry<Double> {
        public DoubleConfigEntry(ForgeConfigSpec.DoubleValue configEntry, FontRenderer fontRenderer) {
            super(configEntry, Double::parseDouble, fontRenderer);
        }
    }
    
    public static final class BooleanConfigEntry extends ConfigEntry<Boolean> {

        private final Button toggle;

        public BooleanConfigEntry(ForgeConfigSpec.BooleanValue configEntry, FontRenderer fontRenderer) {
            super(configEntry, fontRenderer);
            this.toggle = new Button(0, 0, 60, 20, I18n.format(configEntry.get() ? "item_render.gui.config.enabled" : 
                "item_render.gui.config.disabled", ObjectArrays.EMPTY_ARRAY), button -> {
                    boolean previous = configEntry.get();
                    configEntry.set(!previous);
                    button.setMessage(I18n.format(previous ? "item_render.gui.config.disabled" : 
                        "item_render.gui.config.enabled", ObjectArrays.EMPTY_ARRAY));
            });
        }

        @Override
        public List<? extends IGuiEventListener> children() {
            return Collections.singletonList(this.toggle);
        }

        @Override
        public void render(int itemNum, int guiTop, int guiLeft, int width, int height, int mouseX, int mouseY, boolean isWithinRegion, float partialTick) {
            this.font.drawString(I18n.format(this.configEntrySpec.getTranslationKey(), ObjectArrays.EMPTY_ARRAY), guiLeft - 60, guiTop + 4, 0x00FFFFFF);
            this.toggle.x = guiLeft + 150;
            this.toggle.y = guiTop - 2;
            this.toggle.setWidth(width / 3);
            this.toggle.render(mouseX, mouseY, partialTick);
        }

    }
}