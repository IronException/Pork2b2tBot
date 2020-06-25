package net.daporkchop.toobeetooteebot.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * class that handles what needs to get sent to discord and the messages received from discord.
 */
public class DiscordHandler {

    private JDA jda;

    public void build(final String token) {
        try {
            jda = JDABuilder.createDefault(token).addEventListeners(this).build();
        } catch (Exception ignored) {}
    }

    public boolean isRunning() {
        return jda != null;
    }




}
