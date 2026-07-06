package infinitygroup.thecamarsenal.command;

import infinitygroup.thecamarsenal.TheCamArsenal;
import infinitygroup.thecamarsenal.network.ReloadSkinsPayload;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import static net.minecraft.commands.Commands.literal;

public final class ArsenalCommands {
    private ArsenalCommands() {
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(literal("tca")
                .requires(source -> source.hasPermission(2))
                .then(literal("skins")
                        .then(literal("reload").executes(context -> {
                            PacketDistributor.sendToAllPlayers(new ReloadSkinsPayload());
                            context.getSource().sendSuccess(() -> Component.translatable("command." + TheCamArsenal.MODID + ".skins.reload"), true);
                            return 1;
                        }))));
    }
}
