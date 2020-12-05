package net.daporkchop.toobeetooteebot.util.handler;

import net.daporkchop.toobeetooteebot.Bot;
import java.util.function.Consumer;

import static net.daporkchop.toobeetooteebot.util.Constants.CACHE;
import static net.daporkchop.toobeetooteebot.util.Constants.CONFIG;

public class CommandHandler {


    /**
     *
     * @param message the command
     * @param result if a string should be returned (it should handle too long messages itself!)
     * @return whether the message sending should be cancelled.
     */
    public boolean handleCommand(final String message, final Consumer<String> result) {
        // TODO maybe also as parameter where the message is from / specific results... (eg embeds for discord...)
        return handleCommand(message, true, result, new CommandAcceptor() {
            @Override
            public void sendTabMessage() {

            }
        });
    }

    public boolean handleCommand(final String message, final boolean justNormalString, final Consumer<String> result, final CommandAcceptor special) {
        // TODO maybe change embed to a sorted hashmap with entries?
        if(!CONFIG.commands.enabled) {
            return false;
        }
        if(!message.startsWith(CONFIG.commands.prefix)) {
            return false;
        }

        if(checkCommands(message, justNormalString, result, special)) {
            return true;
        }

        return CONFIG.commands.cancelIfPrefix;
    }


    private boolean checkCommands(final String command, final boolean justNormalString, final Consumer<String> result, final CommandAcceptor specific) {
        if (command.equals("tab")) {
            if(justNormalString) {
                // TODO result.accept(tabData);
            } else {
                specific.sendTabMessage();
            }

        } else if (command.equals("dc") || command.equals("disconnect") || command.equals("reconnect")) {
            Bot.getInstance().getClient().getSession().disconnect("Discord user forced disconnect");
        } else if(command.equals("queue")) {
            // TODO sendMessageForced(calculateQueueData("position: {pos}\nestimated time: {time}").orElse("you are not in the queue"));
        } else if(command.equals("health")) {
            // TODO sendMessageForced(String.format("health: %.1f", CACHE.getPlayerCache().getThePlayer().getHealth()));
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

        void sendTabMessage();

    }
}
