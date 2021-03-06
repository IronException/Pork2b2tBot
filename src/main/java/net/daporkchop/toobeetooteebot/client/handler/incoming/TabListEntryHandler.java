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

import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import lombok.NonNull;
import net.daporkchop.toobeetooteebot.client.PorkClientSession;
import net.daporkchop.toobeetooteebot.util.handler.HandlerRegistry;

import java.util.function.Consumer;

import static net.daporkchop.toobeetooteebot.util.Constants.*;

/**
 * @author DaPorkchop_
 */
public class TabListEntryHandler implements HandlerRegistry.IncomingHandler<ServerPlayerListEntryPacket, PorkClientSession> {
    @Override
    public boolean apply(@NonNull ServerPlayerListEntryPacket packet, @NonNull PorkClientSession session) {
        Consumer<PlayerListEntry> consumer = entry -> {
            throw new IllegalStateException();
        };
        switch (packet.getAction()) {
            case ADD_PLAYER:
                consumer = CACHE.getTabListCache().getTabList()::add;
                break;
            case REMOVE_PLAYER:
                consumer = CACHE.getTabListCache().getTabList()::remove;
                break;
            case UPDATE_LATENCY:
                consumer = entry -> WEBSOCKET_SERVER.updatePlayer(CACHE.getTabListCache().getTabList().get(entry).setPing(entry.getPing()));
                break;
            case UPDATE_DISPLAY_NAME:
                consumer = entry -> WEBSOCKET_SERVER.updatePlayer(CACHE.getTabListCache().getTabList().get(entry).setDisplayName(entry.getDisplayName()));
                break;
            case UPDATE_GAMEMODE:
                consumer = entry -> WEBSOCKET_SERVER.updatePlayer(CACHE.getTabListCache().getTabList().get(entry).setGameMode(entry.getGameMode()));
                break;
        }
        for (PlayerListEntry entry : packet.getEntries()) {
            consumer.accept(entry);
        }
        return true;
    }

    @Override
    public Class<ServerPlayerListEntryPacket> getPacketClass() {
        return ServerPlayerListEntryPacket.class;
    }
}
