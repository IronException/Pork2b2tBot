package net.daporkchop.toobeetooteebot.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * class that handles what needs to get sent to discord and the messages received from discord.
 */
public class DiscordHandler extends ListenerAdapter {

    private JDA jda;

    public void build(final String token) {
        try {
            jda = JDABuilder.createDefault(token).addEventListeners(this).build();
        } catch (Exception ignored) {}
    }

    public boolean isRunning() {
        return jda != null;
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getAuthor(), event.getMessage().getContentDisplay());
        } else {
            System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
        }
    }

}
