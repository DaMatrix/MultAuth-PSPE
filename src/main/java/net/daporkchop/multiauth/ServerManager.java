package net.daporkchop.multiauth;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.*;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import org.bukkit.Bukkit;

import java.net.Proxy;
import java.util.*;

public class ServerManager {
    public static HashMap<String, KeyEntry> keys = new HashMap<>();
    public static HashMap<Session, String> usernames = new HashMap<>();

    public static Server server;

    public static void init()   {
        server = new Server("0.0.0.0", 10293, MinecraftProtocol.class, new TcpSessionFactory());
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, Proxy.NO_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, true); //authenticate
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoBuilder() {
            @Override
            public ServerStatusInfo buildInfo(Session session) {
                return new ServerStatusInfo(new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION), new PlayerInfo(Integer.MAX_VALUE, 0, new GameProfile[0]), new TextMessage("\u00A7cPorkAnarchy authentication server"), null);
            }
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, new ServerLoginHandler() {
            @Override
            public void loggedIn(Session session) {
                String authKey = getSaltString();
                String username = usernames.get(session);
                if (MultiAuth.registeredPlayers.containsKey(username))    {
                    session.disconnect("§cYou've already registered!!!");
                    return;
                }
                String prevKey = getPrevKey(username);
                if (prevKey != null)    {
                    session.disconnect("§6Your auth key is: §9" + prevKey);
                    return;
                }
                session.disconnect("§6Your auth key is: §9" + authKey);
                keys.put(authKey, new KeyEntry(System.currentTimeMillis(), usernames.remove(session)));
            }
        });

        server.addListener(new ServerListener() {
            @Override
            public void serverBound(ServerBoundEvent serverBoundEvent) {

            }

            @Override
            public void serverClosing(ServerClosingEvent serverClosingEvent) {

            }

            @Override
            public void serverClosed(ServerClosedEvent serverClosedEvent) {

            }

            @Override
            public void sessionAdded(SessionAddedEvent sessionAddedEvent) {
                sessionAddedEvent.getSession().addListener(new SessionListener() {
                    @Override
                    public void packetReceived(PacketReceivedEvent packetReceivedEvent) {
                        if (packetReceivedEvent.getPacket() instanceof LoginStartPacket)    {
                            usernames.put(sessionAddedEvent.getSession(), ((LoginStartPacket) packetReceivedEvent.getPacket()).getUsername());
                        }
                    }

                    @Override
                    public void packetSending(PacketSendingEvent packetSendingEvent) {

                    }

                    @Override
                    public void packetSent(PacketSentEvent packetSentEvent) {

                    }

                    @Override
                    public void connected(ConnectedEvent connectedEvent) {

                    }

                    @Override
                    public void disconnecting(DisconnectingEvent disconnectingEvent) {

                    }

                    @Override
                    public void disconnected(DisconnectedEvent disconnectedEvent) {

                    }
                });
            }

            @Override
            public void sessionRemoved(SessionRemovedEvent sessionRemovedEvent) {

            }
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 256);
        server.bind(true);
    }

    public static String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static class KeyEntry {
        public long time;
        public String playername;

        public KeyEntry(long a, String b)   {
            time = a;
            playername = b;
        }
    }

    public static String getPrevKey(String username)    {
        for (Map.Entry<String, KeyEntry> entry : keys.entrySet())    {
            if (entry.getValue().playername.equals(username))   {
                return entry.getKey();
            }
        }

        return null;
    }
}
