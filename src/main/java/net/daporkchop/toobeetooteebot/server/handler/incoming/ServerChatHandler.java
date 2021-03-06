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

package net.daporkchop.toobeetooteebot.server.handler.incoming;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import lombok.NonNull;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.toobeetooteebot.server.PorkServerConnection;
import net.daporkchop.toobeetooteebot.util.handler.HandlerRegistry;

import java.lang.reflect.Field;

import static net.daporkchop.toobeetooteebot.util.Constants.*;

/**
 * @author DaPorkchop_
 */
public class ServerChatHandler implements HandlerRegistry.IncomingHandler<ClientChatPacket, PorkServerConnection> {
    protected static final long CLIENTCHATPACKET_MESSAGE_OFFSET = PUnsafe.pork_getOffset(ClientChatPacket.class, "message");

    @Override
    public boolean apply(@NonNull ClientChatPacket packet, @NonNull PorkServerConnection session) {
        if (packet.getMessage().startsWith("!"))   {
            if (packet.getMessage().startsWith("!!"))   {
                //allow sending ingame commands to bots or whatever
                PUnsafe.putObject(packet, CLIENTCHATPACKET_MESSAGE_OFFSET, packet.getMessage().substring(1));
                return true;
            } else if ("!dc".equalsIgnoreCase(packet.getMessage())) {
                session.getBot().getClient().getSession().disconnect("User forced disconnect", false);
                return false;
            } else if ("!reboot".equalsIgnoreCase(packet.getMessage())) {
                SHOULD_RECONNECT = false;
                session.getBot().getClient().getSession().disconnect("User forced disconnect", false);
                return false;
            } else {
                session.send(new ServerChatPacket(String.format("§cUnknown command: §o%s", packet.getMessage()), true));
                return false;
            }
        }
        return true;
    }

    @Override
    public Class<ClientChatPacket> getPacketClass() {
        return ClientChatPacket.class;
    }
}
