/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public final class CommandItemRender {

    public CommandItemRender(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("itemrender")
            .then(Commands.literal("scale")
                .then(Commands.argument("value", FloatArgumentType.floatArg(0F, 2F))
                    .executes(CommandItemRender::changeScale)
                ).executes(CommandItemRender::displayScale)
            ).executes(CommandItemRender::sendHelp)
        );
    }

    private static int sendHelp(CommandContext<CommandSource> context) {
        CommandSource sender = context.getSource();
        sender.sendFeedback(new StringTextComponent(TextFormatting.RED + "/itemrender scale [value]"), false);
        sender.sendFeedback(new StringTextComponent(TextFormatting.AQUA + "Execute this command to control entity/item rendering scale."), false);
        sender.sendFeedback(new StringTextComponent(TextFormatting.AQUA + "Scale Range: (0.0, 2.0]. Default: 1.0. Current: " + ItemRenderMod.renderScale), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int changeScale(CommandContext<CommandSource> context) {
        ItemRenderMod.renderScale = FloatArgumentType.getFloat(context, "scale");
        context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Scale: " + ItemRenderMod.renderScale), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int displayScale(CommandContext<CommandSource> context) {
        CommandSource sender = context.getSource();
        sender.sendFeedback(new StringTextComponent(TextFormatting.AQUA + "Current Scale: " + ItemRenderMod.renderScale), false);
        sender.sendFeedback(new StringTextComponent(TextFormatting.RED + "Execute /itemrender scale [value] to control entity/item rendering " + TextFormatting.RED + "scale."), false);    
        return Command.SINGLE_SUCCESS;
    }
}