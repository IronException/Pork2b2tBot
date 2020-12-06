package net.daporkchop.toobeetooteebot.util.handler;

import net.daporkchop.toobeetooteebot.Bot;
import java.util.function.Consumer;

import static net.daporkchop.toobeetooteebot.util.Constants.*;

public class CommandHandler {


    /**
     *
     * @param message the command
     * @param result if a string should be returned (it should handle too long messages itself!)
     * @return whether the message sending should be cancelled.
     */
    public boolean handleCommand(final String message, final Consumer<String> result) {
        // TODO maybe also as parameter where the message is from / specific results... (eg embeds for discord...)
        return handleCommand(message, result, new CommandAcceptor() {
            @Override
            public void sendTabMessage() {
                result.accept("// TODO send tab here...");
            }
        });
    }

    public boolean handleCommand(final String message, final Consumer<String> result, final CommandAcceptor specific) {
        // TODO maybe change embed to a sorted hashmap with entries?
        if(!CONFIG.commands.enabled) {
            return false;
        }
        if(!message.startsWith(CONFIG.commands.prefix)) {
            return false;
        }

        if(checkCommands(message.substring(CONFIG.commands.prefix.length()), result, specific)) {
            return true;
        }

        if(CONFIG.commands.cancelIfPrefix) {

            result.accept("Unknown command: " + message);
            return true;
        }

        return false;
    }


    private boolean checkCommands(final String command, final Consumer<String> result, final CommandAcceptor specific) {
        if (command.equals("tab")) {
            specific.sendTabMessage();
        } else if (command.equals("dc") || command.equals("disconnect") || command.equals("reconnect")) {
            Bot.getInstance().getClient().getSession().disconnect("User forced disconnect", false);
        }  else if (command.equals("shutdown") || command.equals("reboot")) {
            SHOULD_RECONNECT = false;
            Bot.getInstance().getClient().getSession().disconnect("User forced disconnect", false);
        } else if(command.equals("queue")) {
            result.accept(QUEUE_HANDLER.calculateQueueData("position: {pos}\nestimated time: {time}").orElse("you are not in the queue"));
            // TODO make as specific
        } else if(command.equals("health")) {
            result.accept(String.format("health: %.1f", CACHE.getPlayerCache().getThePlayer().getHealth()));
            // TODO make as specific?
        } else if(command.equals("status")) {
            result.accept(String.format("gamemode: %s\ndimension: %s\ndifficulty: %s\nhealth: %.1f\nfood: %d\nsaturation: %.1f",
                    CACHE.getPlayerCache().getGameMode(),
                    CACHE.getPlayerCache().getDimension(),
                    CACHE.getPlayerCache().getDifficulty(),
                    CACHE.getPlayerCache().getThePlayer().getHealth(),
                    CACHE.getPlayerCache().getThePlayer().getFood(),
                    CACHE.getPlayerCache().getThePlayer().getSaturation()));
            // TODO send more information... and add specific or soemthing
        } else {
            return false; // if we couldn't find a command we just return... (so return true does not need to be added in the specific cases)
        }

        // TODO commands to send certain packets?
        // TODO commands to change config?
        return true;
    }


    public interface CommandAcceptor {

        void sendTabMessage(); // TODO maybe the data can be given in the parameters so it doesnt have to be gotten in the method itself... (no dublicate code)

    }
}
