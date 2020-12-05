package net.daporkchop.toobeetooteebot.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.Helpers;

import java.util.Objects;

import static net.daporkchop.toobeetooteebot.util.Constants.DISCORD_LOG;

/**
 * class that handles what needs to get sent to discord and the messages received from discord.
 */
public class DiscordHandler extends ListenerAdapter {

    private JDA jda;
    private long channelId;

    private MessageListener messageListener;
    // TODO a custom string as the status
    // TODO a setting what events to send... like chat...
    // TODO also send when online?

    public void build(final String token, final long channelId, final MessageListener listener) {
        try {
            jda = JDABuilder.createDefault(token).addEventListeners(this).build();
        } catch (Exception ignored) {}

        this.channelId = channelId;
        this.messageListener = listener;
        // TODO turn off if channelId is wrong
    }

    public boolean isRunning() { // TODO is this actually needed... if yes then use it in checks!!!
        return jda != null;
    }

    public void stop() {
        if(isRunning()) {
            // TODO just shutdown is better imo but it takes way too long
            jda.shutdownNow();
        }
    }

    @Override
    public void onMessageReceived(final MessageReceivedEvent event) {

        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getAuthor(), event.getMessage().getContentDisplay());
        } else {
            System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
        }

        if(event.getChannel().getIdLong() == channelId && !event.getAuthor().isBot()) {
            event.getMessage().addReaction("U+2705").queue(); // white_check_mark
            messageListener.onMessage(event.getMessage().getContentDisplay());
        }

    }

    public void sendMessage(final String text) {
        // TODO store the messages somehow and sent them together if necessary
        if(Helpers.isBlank(text)) {
            DISCORD_LOG.alert("Can't send an empty message!");
            return;
        }
        Objects.requireNonNull(jda.getTextChannelById(channelId)).sendMessage(text).queue();
    }

    public void setActivity(final String text) {
        // TODO wait and not spam
        jda.getPresence().setActivity(Activity.streaming(text, "https://2b2t.io/"));
    }

    public interface MessageListener {

        void onMessage(final String message);

    }

}
