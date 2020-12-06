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

package net.daporkchop.toobeetooteebot.client;

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.packet.PacketProtocol;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.toobeetooteebot.Bot;
import net.daporkchop.toobeetooteebot.client.ClientListener;
import net.daporkchop.toobeetooteebot.util.Constants;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static net.daporkchop.toobeetooteebot.util.Constants.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class PorkClientSession extends TcpClientSession {
    @Getter(AccessLevel.PRIVATE)
    protected final CompletableFuture<String> disconnectFuture = new CompletableFuture<>();
    protected final Bot bot;
    protected boolean serverProbablyOffline;

    public PorkClientSession(String host, int port, PacketProtocol protocol, Client client, @NonNull Bot bot) {
        super(host, port, protocol, client, null);
        this.bot = bot;
        this.addListener(new ClientListener(this.bot, this));
    }

    public String getDisconnectReason() {
        try {
            return this.disconnectFuture.get();
        } catch (ExecutionException e)  {
            return e.toString();
        } catch (Exception e) {
            PUnsafe.throwException(e);
            return null;
        }
    }

    @Override
    public void disconnect(String reason, Throwable cause, boolean wait) {
        super.disconnect(reason, cause, wait);
        serverProbablyOffline = reason.equals("Connection closed.") || reason.equals("Server closed.");
        if (cause == null) {
            this.disconnectFuture.complete(reason);
        } else if (cause instanceof IOException)    {
            this.disconnectFuture.complete(String.format("IOException: %s", cause.getMessage()));
        } else {
            CLIENT_LOG.alert(cause);
            this.disconnectFuture.completeExceptionally(cause);
        }
    }
}
