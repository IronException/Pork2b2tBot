package net.daporkchop.toobeetooteebot.discordbot;

import static net.daporkchop.toobeetooteebot.util.Constants.CONFIG;
import static net.daporkchop.toobeetooteebot.util.Constants.DISCORD_LOG;

/**
 * class that handles the events from the server and forwards it to the DiscordHandler
 */
public class DiscordBot {

    private final DiscordHandler discordHandler;

    public DiscordBot() {
        discordHandler = new DiscordHandler();
    }

    public void connect() {
        discordHandler.build(CONFIG.discordBot.token);
        if(discordHandler.isRunning()) {
            DISCORD_LOG.success("Discord bot started!");
        } else {
            DISCORD_LOG.alert("Discord bot starting failed!");
        }
    }

    public void disconnect() {

    }

    public void onMessage(final String message) {

    }

}
