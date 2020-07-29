package net.daporkchop.toobeetooteebot.discordbot;

import net.daporkchop.lib.minecraft.text.parser.MCFormatParser;
import net.daporkchop.toobeetooteebot.Bot;
import net.daporkchop.toobeetooteebot.util.cache.data.tab.TabList;

import static net.daporkchop.toobeetooteebot.util.Constants.*;

/**
 * class that handles the events from the server and forwards it to the DiscordHandler
 */
public class DiscordBot {

    private final DiscordHandler discordHandler;

    public DiscordBot() {
        discordHandler = new DiscordHandler();
    }

    public void connect() {
        discordHandler.build(CONFIG.discordBot.token, CONFIG.discordBot.channelId, message -> {
            if(message.equals("tab")) {
                final TabList tabList = CACHE.getTabListCache().getTabList();
                sendMinecraftMessage("```\n " + tabList.getHeader() + " ```");
                sendMinecraftMessage("```\n " + tabList.getFooter() + " ```");
            } else if(message.equals("dc") || message.equals("disconnect") || message.equals("reconnect")) {
                Bot.getInstance().getClient().getSession().disconnect("Discord user forced disconnect", false);
            }
        });
        if(discordHandler.isRunning()) { // TODO I dont think that check will work actually :thinking:
            DISCORD_LOG.success("Discord bot started!");
        } else {
            DISCORD_LOG.alert("Discord bot starting failed!");
        }
    }

    public void disconnect() {

    }


    public void sendMinecraftMessage(final String text) {
        sendMessage(MCFormatParser.DEFAULT.parse(text).toRawString());
    }

    public void sendMessage(final String message) { // TODO maybe use an enum as like an event type to figure out whether to send or not
        if (CONFIG.discordBot.enable) {
            discordHandler.sendMessage(message);
        }
    }

}
