package net.daporkchop.multiauth;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.Map;

// NOTE: If you're using NanoHTTPD < 3.0.0 the namespace is different,
//       instead of the above import use the following:
// import fi.iki.elonen.NanoHTTPD;

public class WebServer extends NanoHTTPD {

    public static WebServer INSTANCE;

    public WebServer() throws IOException {
        super(8888);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        INSTANCE = this;
    }

    @Override
    public Response serve(IHTTPSession session) {
        switch (session.getUri())   {
            case "":
            case "/":
                return newFixedLengthResponse("<html><head><title>PorkAnarchy authentication</title></head><body><h1><strong>PorkAnarchy authentication page</strong></h1>\n" +
                        "<ol>\n" +
                        "<li>Connect to <span style=\"text-decoration: underline;\"><em>anarchy.daporkchop.net:10293</em></span> with Minecraft PC (Java) edition. Make sure you're logged in with the username you want to register with!</li>\n" +
                        "<li>You will be given a code to enter in the box below. Enter it and press \"Submit\".</li>\n" +
                        "<li>You will be given a temporary password to use to log in to PorkAnarchy. Make sure to change your password to one of your choice later!</li>\n" +
                        "</ol>\n" +
                        "<p>&nbsp;</p>\n" +
                        "<p><input id=\"input\" type=\"text\" /><button onclick=\"redirect()\">Submit</button></p>\n" +
                        "<script>\n" +
                        "function redirect() {\n" +
                        "window.location.replace(\"http://anarchy.daporkchop.net:8888/submit?key=\" + document.getElementById(\"input\").value);\n" +
                        "}\n" +
                        "</script>" +
                        "</body></html>");
            case "/submit":
            case "/submit/":
                Map<String, String> parameters = session.getParms();
                if (parameters.get("key") == null)  {
                    return newFixedLengthResponse("<html><body><p>No key given</p></body></html>");
                } else {
                    String key = parameters.get("key");
                    ServerManager.KeyEntry entry = ServerManager.keys.get(key);
                    if (entry == null)    {
                        return newFixedLengthResponse("Invalid key! Is it expired (>15 minutes old) or did you enter it wrong?");
                    } else {
                        String newPassword = ServerManager.getSaltString();
                        String hashed = MultiAuth.fakeHash(newPassword);
                        MultiAuth.registeredPlayers.put(entry.playername, hashed);
                        ServerManager.keys.remove(key);
                        return newFixedLengthResponse("<html><body><h3>Successfully registered!</h3>\n" +
                                "<p>You can now log in to PorkAnarchy! Your temporary password is:&nbsp;<strong>" + newPassword + "</strong><br>Remember it well or change it after you log in.</p>" +
                                "</body></html>");
                    }
                }
        }
        return newFixedLengthResponse("lol wut");
    }
}
