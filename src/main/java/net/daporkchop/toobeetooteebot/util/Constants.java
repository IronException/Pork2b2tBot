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

package net.daporkchop.toobeetooteebot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.binary.oio.appendable.PAppendable;
import net.daporkchop.lib.binary.oio.reader.UTF8FileReader;
import net.daporkchop.lib.binary.oio.writer.UTF8FileWriter;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.logging.LogAmount;
import net.daporkchop.lib.logging.Logger;
import net.daporkchop.lib.logging.Logging;
import net.daporkchop.lib.logging.impl.DefaultLogger;
import net.daporkchop.lib.minecraft.text.parser.AutoMCFormatParser;
import net.daporkchop.toobeetooteebot.client.PorkClientSession;
import net.daporkchop.toobeetooteebot.client.handler.incoming.*;
import net.daporkchop.toobeetooteebot.client.handler.incoming.entity.*;
import net.daporkchop.toobeetooteebot.client.handler.incoming.spawn.*;
import net.daporkchop.toobeetooteebot.server.PorkServerConnection;
import net.daporkchop.toobeetooteebot.server.handler.incoming.LoginStartHandler;
import net.daporkchop.toobeetooteebot.server.handler.incoming.ServerChatHandler;
import net.daporkchop.toobeetooteebot.server.handler.incoming.ServerKeepaliveHandler;
import net.daporkchop.toobeetooteebot.server.handler.incoming.movement.PlayerPositionHandler;
import net.daporkchop.toobeetooteebot.server.handler.incoming.movement.PlayerPositionRotationHandler;
import net.daporkchop.toobeetooteebot.server.handler.incoming.movement.PlayerRotationHandler;
import net.daporkchop.toobeetooteebot.server.handler.outgoing.LoginSuccessOutgoingHandler;
import net.daporkchop.toobeetooteebot.server.handler.postoutgoing.JoinGamePostHandler;
import net.daporkchop.toobeetooteebot.util.cache.DataCache;
import net.daporkchop.toobeetooteebot.util.handler.HandlerRegistry;
import net.daporkchop.toobeetooteebot.websocket.WebSocketServer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * @author DaPorkchop_
 */
@UtilityClass
public class Constants {
    public final String VERSION = "0.2.8";

    public final JsonParser JSON_PARSER = new JsonParser();
    public final Gson       GSON        = new GsonBuilder().setPrettyPrinting().create();

    public final DefaultLogger DEFAULT_LOG   = Logging.logger;
    public final Logger        AUTH_LOG      = DEFAULT_LOG.channel("Auth");
    public final Logger        CACHE_LOG     = DEFAULT_LOG.channel("Cache");
    public final Logger        CLIENT_LOG    = DEFAULT_LOG.channel("Client");
    public final Logger        CHAT_LOG      = DEFAULT_LOG.channel("Chat");
    public final Logger        GUI_LOG       = DEFAULT_LOG.channel("GUI");
    public final Logger        MODULE_LOG    = DEFAULT_LOG.channel("Module");
    public final Logger        SERVER_LOG    = DEFAULT_LOG.channel("Server");
    public final Logger        WEBSOCKET_LOG = DEFAULT_LOG.channel("WebSocket");

    public final File CONFIG_FILE = new File("config.json");

    public       Config          CONFIG;
    public final DataCache       CACHE;
    public final WebSocketServer WEBSOCKET_SERVER;

    public final HandlerRegistry<PorkClientSession> CLIENT_HANDLERS = new HandlerRegistry.Builder<PorkClientSession>()
            .setLogger(CLIENT_LOG)
            //
            // Inbound packets
            //
            .registerInbound(new AdvancementsHandler())
            // TODO AdvancementTab: idk what it does but it might only get sent when in the gui so it's fine not having it
            .registerInbound(new BossBarHandler())
            .registerInbound(new ChatHandler())
            // TODO Combat: idk seems like you can get the respawn screen with that?
            .registerInbound(new ClientKeepAliveHandler())
            // TODO Difficulty: should implement this
            // Disconnect: gets handled in another way
            .registerInbound(new JoinGameHandler())
            // KeepAlive: got renamed to ClientKeepAlive
            // PlayerListData: got renamed to TabListData
            // PlayerListEntry: got renamed to TabListEntry
            // TODO PluginMessage: idk but might only be temporary? well maybe not actually?
            // TODO ResourcePackSend: if server doesn't resend this this needs to be cached :worried:
            .registerInbound(new RespawnHandler())
            // TODO SetCompression: idk but might not matter if the proxy handles all ;)
            // SetCooldown: should only be temporary unless server sends some ridiculous amount
            .registerInbound(new StatisticsHandler())
            // TODO SwitchCameraPacket: doesn't get sent by servers usually unless you are in spectator and click on an entity but non vanilla servers might do some weird stuff
            // TabComplete: only gets sent when client is online so nothing to cache here (would be cool to add tab complete to the commands :sunglasses: and cancel it if you would be leaking that you use a hack client)
            .registerInbound(new TabListDataHandler())
            .registerInbound(new TabListEntryHandler())
            // TODO Title
            .registerInbound(new UnlockRecipesHandler())

            // WORLD PACKAGE
            // BlockBreakAnim: is it worth it to implement this?
            .registerInbound(new BlockChangeHandler())
            // TODO BlockValue
            .registerInbound(new ChunkDataHandler())
            // Explosion: changes are also handled by the server anyway so it doesn't matter
            .registerInbound(new GameStateHandler())
            // TODO MapData: should implement this because server only sends map data once
            .registerInbound(new MultiBlockChangeHandler())
            // NotifyClient: got renamed to GameState
            // TODO OpenTileEntityEditor:
            // PlayBuiltinSound: no need to resend sounds because they are only temporary
            // PlayEffect: no need to resend effects because they are only temporary
            // PlaySound: no need to resend sounds because they are only temporary
            // SpawnParticle: no need to resend particles because they are only temporary
            // TODO SpawnPosition: idk
            .registerInbound(new UnloadChunkHandler())
            .registerInbound(new UpdateTileEntityHandler())
            // TODO UpdateTime: should be implemented for accurate time (low prio)
            // TODO WorldBorder: should be implemented

            // WINDOW PACKAGE
            // no need to resend them because they are only temporary and for the open window?
            .registerInbound(new SetSlotHandler())
            .registerInbound(new SetWindowItemsHandler())

            // SCOREBOARD PACKAGE
            // TODO DisplayScoreboard
            // TODO ScoreboardObjective
            // TODO Team
            // TODO UpdateScore

            //ENTITY PACKAGE
            // TODO EntityAnimation
            .registerInbound(new EntityAttachHandler())
            .registerInbound(new EntityCollectItemHandler())
            .registerInbound(new EntityDestroyHandler())
            .registerInbound(new EntityEffectHandler())
            .registerInbound(new EntityEquipmentHandler())
            .registerInbound(new EntityHeadLookHandler())
            .registerInbound(new EntityMetadataHandler())
            // TODO EntityMovement
            .registerInbound(new EntityPositionHandler())
            .registerInbound(new EntityPositionRotationHandler())
            .registerInbound(new EntityPropertiesHandler())
            .registerInbound(new EntityRemoveEffectListener())
            .registerInbound(new EntityRotationHandler())
            .registerInbound(new EntitySetPassengersHandler())
            // TODO EntityStatus
            .registerInbound(new EntityTeleportHandler())
            // TODO EntityVelocity
            // TODO VehicleMove

            //SPAWN IN ENTITY PACKAGE
            .registerInbound(new SpawnExperienceOrbHandler())
            // TODO SpawnGlobalEntity
            .registerInbound(new SpawnMobHandler())
            .registerInbound(new SpawnObjectHandler())
            .registerInbound(new SpawnPaintingPacket())
            .registerInbound(new SpawnPlayerHandler())

            // PLAYER IN ENTITY PACKAGE
            // TODO PlayerAbilities
            // TODO PlayerChangeHeldItem
            .registerInbound(new PlayerHealthHandler())
            .registerInbound(new PlayerPosRotHandler())
            .registerInbound(new PlayerSetExperienceHandler())
            // TODO PlayerUseBed: doesn't get sent by servers usually unless you click on a bed but non vanilla servers might do some weird stuff

            // LOGIN PACKAGE
            .registerInbound(new LoginSuccessHandler())


            .build();

    public final HandlerRegistry<PorkServerConnection> SERVER_HANDLERS = new HandlerRegistry.Builder<PorkServerConnection>()
            .setLogger(SERVER_LOG)
            //
            // Inbound packets
            //
            .registerInbound(new LoginStartHandler())
            .registerInbound(new ServerChatHandler())
            .registerInbound(new ServerKeepaliveHandler())
            //PLAYER MOVEMENT
            .registerInbound(new PlayerPositionHandler())
            .registerInbound(new PlayerPositionRotationHandler())
            .registerInbound(new PlayerRotationHandler())
            //
            // Outbound packets
            //
            .registerOutbound(new LoginSuccessOutgoingHandler())
            //
            // Post-outbound packets
            //
            .registerPostOutbound(new JoinGamePostHandler())
            .build();

    static {
        String date = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss").format(Date.from(Instant.now()));
        File logFolder = PFiles.ensureDirectoryExists(new File("log"));
        DEFAULT_LOG.addFile(new File(logFolder, String.format("%s.log", date)), LogAmount.NORMAL)
                .enableANSI()
                .setFormatParser(AutoMCFormatParser.DEFAULT)
                .setLogAmount(LogAmount.NORMAL);

        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            DEFAULT_LOG.alert(String.format("Uncaught exception in thread \"%s\"!", thread), e);
        });

        loadConfig();

        if (CONFIG.log.printDebug)  {
            DEFAULT_LOG.setLogAmount(LogAmount.DEBUG);
        }
        if (CONFIG.log.storeDebug) {
            DEFAULT_LOG.addFile(new File(logFolder, String.format("%s-debug.log", date)), LogAmount.DEBUG);
        }

        SHOULD_RECONNECT = CONFIG.client.extra.autoReconnect.enabled;

        CACHE = new DataCache();
        WEBSOCKET_SERVER = new WebSocketServer();
    }

    public volatile boolean SHOULD_RECONNECT;

    public synchronized void loadConfig() {
        DEFAULT_LOG.info("Loading config...");

        Config config;
        if (PFiles.checkFileExists(CONFIG_FILE)) {
            try (Reader reader = new UTF8FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, Config.class);
            } catch (IOException e) {
                throw new RuntimeException("Unable to load config!", e);
            }
        } else {
            config = new Config();
        }

        CONFIG = config.doPostLoad();
        DEFAULT_LOG.info("Config loaded.");
    }

    public synchronized void saveConfig() {
        DEFAULT_LOG.info("Saving config...");

        if (CONFIG == null) {
            DEFAULT_LOG.warn("Config is not set, saving default config!");
            CONFIG = new Config().doPostLoad();
        }

        try (PAppendable out = new UTF8FileWriter(PFiles.ensureFileExists(CONFIG_FILE))) {
            GSON.toJson(CONFIG, out);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save config!", e);
        }

        DEFAULT_LOG.info("Config saved.");
    }
}
