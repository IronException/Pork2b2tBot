package net.daporkchop.toobeetooteebot.discordbot;

import net.daporkchop.lib.minecraft.text.parser.MCFormatParser;
import net.daporkchop.toobeetooteebot.Bot;
import net.daporkchop.toobeetooteebot.util.Config;
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
                sendMessage("```\n " + convertMinecraftMessage(tabList.getHeader()) + " ```", MessageType.FORCED);
                sendMessage("```\n " + convertMinecraftMessage(tabList.getFooter()) + " ```", MessageType.FORCED); // TODO maybe proxy methods for this?
            } else if(message.equals("dc") || message.equals("disconnect") || message.equals("reconnect")) {
                Bot.getInstance().getClient().getSession().disconnect("Discord user forced disconnect");
            }
            // TODO commands to send certain packets?
            // TODO commands to change config?
        });
        if(isOnline()) { // TODO I dont think that check will work actually :thinking:
            DISCORD_LOG.success("Discord bot started!");
        } else {
            DISCORD_LOG.alert("Discord bot starting failed!");
        }
    }

    public void disconnect() {

    }

    public boolean isOnline() {
        return CONFIG.discordBot.enable && discordHandler.isRunning();
    }


    public String convertMinecraftMessage(final String text) { // TODO move this method somewhere else!
        return MCFormatParser.DEFAULT.parse(text).toRawString().replaceAll("§.", ""); // TODO remove complete color codes
    }

    public void sendMessage(final String message, final MessageType type) {
        if (isOnline() && type.doesUserWantMessage()) {
            discordHandler.sendMessage(message);
        }
    }

    public void updateActivity() {
        if (isOnline()) {
            discordHandler.setActivity("// TODO activity setting");
            // TODO a setting what to show here? I want to be able to see queue pos + time there but at the same time allow the user to show other data...
        }
    }

    public enum MessageType {
        UNDEFINED { // in case it is needed
            @Override
            public boolean doesUserWantMessage() {
                return CONFIG.discordBot.sendMessage.undefined;
            }
        },
        FORCED {
            @Override
            public boolean doesUserWantMessage() {
                return true;
            }
        },
        CHAT {
            @Override
            public boolean doesUserWantMessage() {
                return CONFIG.discordBot.sendMessage.chat;
            }
        },
        DISCONNECT {
            @Override
            public boolean doesUserWantMessage() {
                return CONFIG.discordBot.sendMessage.disconnect;
            }
        },
        TAB {
            @Override
            public boolean doesUserWantMessage() {
                return CONFIG.discordBot.sendMessage.tab;
            }
        };

        public abstract boolean doesUserWantMessage();
    }

}