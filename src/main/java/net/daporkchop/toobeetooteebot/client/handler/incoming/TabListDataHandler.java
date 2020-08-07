/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2016-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.toobeetooteebot.client.handler.incoming;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import lombok.NonNull;
import net.daporkchop.toobeetooteebot.client.PorkClientSession;
import net.daporkchop.toobeetooteebot.discordbot.DiscordBot;
import net.daporkchop.toobeetooteebot.util.cache.data.tab.TabList;
import net.daporkchop.toobeetooteebot.util.handler.HandlerRegistry;

import static net.daporkchop.toobeetooteebot.util.Constants.*;

/**
 * @author DaPorkchop_
 */
public class TabListDataHandler implements HandlerRegistry.IncomingHandler<ServerPlayerListDataPacket, PorkClientSession> {
    @Override
    public boolean apply(@NonNull ServerPlayerListDataPacket packet, @NonNull PorkClientSession session) {
        if (DISCORD_BOT.isOnline()
                && doesChange(CACHE.getTabListCache().getTabList(), packet)) {
            DISCORD_BOT.sendMessage("```\n " + DISCORD_BOT.convertMinecraftMessage(packet.getHeader()) + " ```", DiscordBot.MessageType.TAB);
            DISCORD_BOT.sendMessage("```\n " + DISCORD_BOT.convertMinecraftMessage(packet.getFooter()) + " ```", DiscordBot.MessageType.TAB);
            // TODO kinda duplicate from tab command. In general maybe the checks that are done here should be moved?

        }
        CACHE.getTabListCache().getTabList()
                .setHeader(packet.getHeader())
                .setFooter(packet.getFooter());
        WEBSOCKET_SERVER.firePlayerListUpdate();
        return true;
    }

    private boolean doesChange(final TabList tabList, final ServerPlayerListDataPacket packet) {
        return !tabList.getHeader().equals(packet.getHeader())
                || !tabList.getFooter().equals(packet.getFooter());
    }

    @Override
    public Class<ServerPlayerListDataPacket> getPacketClass() {
        return ServerPlayerListDataPacket.class;
    }
}
