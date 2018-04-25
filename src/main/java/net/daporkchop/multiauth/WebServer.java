/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.daporkchop.multiauth;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class WebServer extends NanoHTTPD {

    public static WebServer INSTANCE;

    public WebServer() throws IOException {
        super(Config.webPort);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        INSTANCE = this;
    }

    @Override
    public Response serve(IHTTPSession session) {
        switch (session.getUri())   {
            case "":
            case "/":
                return newFixedLengthResponse("<html><head><title>Server authentication</title></head><body><h1><strong>" + Config.name + "authentication page</strong></h1>\n" +
                        "<ol>\n" +
                        "<li>Connect to <span style=\"text-decoration: underline;\"><em>" + Config.verificationAddress + "</em></span> with Minecraft PC (Java) edition. Make sure you're logged in with the username you want to register with!</li>\n" +
                        "<li>You will be given a code to enter in the box below. Enter it and press \"Submit\".</li>\n" +
                        "<li>You will be given a temporary password to use to log in to the server. Make sure to change your password to one of your choice later!</li>\n" +
                        "</ol>\n" +
                        "<p>&nbsp;</p>\n" +
                        "<p><input id=\"input\" type=\"text\" /><button onclick=\"redirect()\">Submit</button></p>\n" +
                        "<script>\n" +
                        "function redirect() {\n" +
                        "window.location.replace(\"http://" + Config.webAddress + "/submit?key=\" + document.getElementById(\"input\").value);\n" +
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
                        byte[] hashed = MultiAuth.hash(newPassword);
                        MultiAuth.registeredPlayers.put(entry.playername, hashed);
                        ServerManager.keys.remove(key);
                        return newFixedLengthResponse("<html><body><h3>Successfully registered!</h3>\n" +
                                "<p>You can now log in to the server! Your temporary password is:&nbsp;<strong>" + newPassword + "</strong><br>Remember it well or change it after you log in.</p>" +
                                "</body></html>");
                    }
                }
        }
        return newFixedLengthResponse("lol wut");
    }
}
