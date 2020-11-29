package net.daporkchop.toobeetooteebot.discordbot;

import net.daporkchop.lib.minecraft.text.parser.AutoMCFormatParser;
import net.daporkchop.toobeetooteebot.Bot;
import net.daporkchop.toobeetooteebot.util.cache.data.tab.TabList;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

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
            if (message.equals("tab")) {
                sendTabMessageForced();
            } else if (message.equals("dc") || message.equals("disconnect") || message.equals("reconnect")) {
                Bot.getInstance().getClient().getSession().disconnect("Discord user forced disconnect");
            }
            // TODO commands to send certain packets?
            // TODO commands to change config?

        });
        if (isOnline()) { // TODO I dont think that check will work actually :thinking:
            DISCORD_LOG.success("Discord bot started!");
            setupUpdateThread();
        } else {
            DISCORD_LOG.alert("Discord bot starting failed!");
        }
    }

    public void disconnect() { }

    public void setupUpdateThread() {
            new Thread(() -> {
                while (isOnline()) {
                    updateActivity();
                    // TODO I have no clue but could there be some concurrent modification exception?
                    trySendMessage(statusText, lastStatusTime, CONFIG.discordBot.sendMessage.status.delay);

                    try {
                        Thread.sleep(CONFIG.discordBot.sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }



    public void sendChatMessage(final String message) {
        if(CONFIG.discordBot.sendMessage.chat) {
            // TODO some kind of filter
            sendMessageForced(convertMinecraftMessage(message));
        }
    }

    public void sendDisconnectMessage(final String reason) {
        if(CONFIG.discordBot.sendMessage.disconnect) {
            sendMessageForced(String.format("Disconnected. Reason: %s", convertMinecraftMessage(reason)));
        }
    }

    private String lastActivity;

    private void updateActivity() {
        if(!CONFIG.discordBot.activity.enabled) {
            return;
        }
        // TODO maybe get the actual data somehow
        final String activity = CONFIG.discordBot.activity.text
                .replace("{playerName}", CONFIG.authentication.username) // Bot.getInstance().getProtocol().getProfile().getName() ?s
                .replace("{serverIp}", CONFIG.client.server.address);
        // TODO more names

        if(!lastActivity.equals(activity)) {
            // TODO wait maybe if needed...
            discordHandler.setActivity(activity);
            lastActivity = activity;
        }


    }

    private float lastHealth;
    private int lastFood;
    private float lastSaturation;

    private final AtomicLong lastStatusTime = new AtomicLong();
    private final AtomicReference<String> statusText = new AtomicReference<>();

    public void updateStatus(final float health, final int food, final float saturation) {
        if(!CONFIG.discordBot.sendMessage.status.send) {
            return;
        }

        if(lastHealth != health
            || lastFood != food
            || lastSaturation != saturation) { // TODO should I instead check for the last message?
            statusText.set(String.format("health: %.1f, food: %d, saturation: %.1f", health, food, saturation));
            trySendMessage(statusText, lastStatusTime, CONFIG.discordBot.sendMessage.status.delay);
            trySendTabMessage();

            lastHealth = health;
            lastFood = food;
            lastSaturation = saturation;
        }

    }


    private String lastHeader;
    private String lastFooter;

    private final AtomicLong lastTabTime = new AtomicLong();
    private boolean sendTab = false;

    public void updateTab(final String header, final String footer) {
        if(!CONFIG.discordBot.sendMessage.tab.send) {
            return;
        }

        if(!lastHeader.equals(header)
            || !lastFooter.equals(footer)) {

            sendTab = true;

            
            lastHeader = header;
            lastFooter = footer;
        }

    }

    private void trySendTabMessage() {
        if(!CONFIG.discordBot.sendMessage.tab.send) {
            return;
        }
        if(!sendTab) {
            return;
        }
        if(lastTabTime.get() + CONFIG.discordBot.sendMessage.tab.delay < System.currentTimeMillis()) {
            sendTabMessageForced();
            lastTabTime.set(System.currentTimeMillis());
            sendTab = false;
        }
    }

    private void trySendMessage(final AtomicReference<String> text, final AtomicLong time, final long delay) {
        if(text.get() != null && time.get() + delay < System.currentTimeMillis()) {
            sendMessageForced(text.get());
            time.set(System.currentTimeMillis());
            text.set(null);
        }
    }

    private void sendTabMessageForced() {
        // TODO parameters for how many players to list (maybe also whether \n or , )
        final TabList tabList = CACHE.getTabListCache().getTabList();
        sendMessageForced("```\n " + convertMinecraftMessage(tabList.getHeader()) + " ```");
        sendMessageForced("```\n " + convertMinecraftMessage(tabList.getFooter()) + " ```");
    }

    public void sendMessageForced(final String message) {
        if (isOnline()) {
            discordHandler.sendMessage(message);
        }
    }

    public boolean isOnline() {
        return CONFIG.discordBot.enable && discordHandler.isRunning();
    }

    private String convertMinecraftMessage(final String text) { // TODO move this method somewhere else!
        return AutoMCFormatParser.DEFAULT.parse(text).toRawString().replaceAll("§.", ""); // TODO remove complete color codes
    }

}
