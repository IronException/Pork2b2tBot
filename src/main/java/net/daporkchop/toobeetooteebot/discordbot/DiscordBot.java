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

    private long lastTimeSentTab;

    public DiscordBot() {
        discordHandler = new DiscordHandler();
    }

    public void connect() {
        discordHandler.build(CONFIG.discordBot.token, CONFIG.discordBot.channelId, message -> {
            if (message.equals("tab")) {
                final TabList tabList = CACHE.getTabListCache().getTabList();
                sendTabMessageForced(tabList.getHeader(), tabList.getFooter());
            } else if (message.equals("dc") || message.equals("disconnect") || message.equals("reconnect")) {
                Bot.getInstance().getClient().getSession().disconnect("Discord user forced disconnect");
            }
            // TODO commands to send certain packets?
            // TODO commands to change config?

        });
        if (isOnline()) { // TODO I dont think that check will work actually :thinking:
            DISCORD_LOG.success("Discord bot started!");
            setupActivityThread();
        } else {
            DISCORD_LOG.alert("Discord bot starting failed!");
        }
    }

    public void disconnect() { }

    public void setupActivityThread() {
        if(!CONFIG.discordBot.activity.enabled) {
            return;
        }

        if(CONFIG.discordBot.activity.updateDelay > 0) {
            // TODO this is probably bad practice but Idk a better way :/
            new Thread(() -> {
                while (isOnline()) {
                    updateActivity();

                    try {
                        Thread.sleep(CONFIG.discordBot.activity.updateDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            // only call it once otherwise
            updateActivity();
        }
    }


    private void updateActivity() {
        // TODO maybe get the actual data somehow
        discordHandler.setActivity(CONFIG.discordBot.activity.text
                .replace("{playerName}", CONFIG.authentication.username) // Bot.getInstance().getProtocol().getProfile().getName() ?s
                .replace("{serverIp}", CONFIG.client.server.address));
        // TODO more names

    }

    public boolean isOnline() {
        return CONFIG.discordBot.enable && discordHandler.isRunning();
    }

    public void sendTabMessage(final String header, final String footer) {
        if(CONFIG.discordBot.sendMessage.tab.send) {
            // TODO check when the time is over in case there are no checks after that you still get the latest tab update

            final long currentTime = System.currentTimeMillis();
            if(currentTime - lastTimeSentTab > CONFIG.discordBot.sendMessage.tab.delay) {
                lastTimeSentTab = currentTime;

                sendTabMessageForced(header, footer);
            }
        }
    }

    private void sendTabMessageForced(final String header, final String footer) {
        sendMessageForced("```\n " + convertMinecraftMessage(header) + " ```");
        sendMessageForced("```\n " + convertMinecraftMessage(footer) + " ```");
    }

    public void sendChatMessage(final String message) {
        if(CONFIG.discordBot.sendMessage.chat) {
            sendMessageForced(convertMinecraftMessage(message));
        }
    }

    public void sendDisconnectMessage(final String reason) {
        if(CONFIG.discordBot.sendMessage.disconnect) {
            sendMessageForced(String.format("Disconnected. Reason: %s", convertMinecraftMessage(reason)));
        }
    }

    public void sendMessageForced(final String message) {
        if (isOnline()) {
            discordHandler.sendMessage(message);
        }
    }

    private String convertMinecraftMessage(final String text) { // TODO move this method somewhere else!
        return MCFormatParser.DEFAULT.parse(text).toRawString().replaceAll("§.", ""); // TODO remove complete color codes
    }

}
