# MultiAuth-PSPE

A simple authentication plugin designed for ProtocolSupport's mcpe branch. It has features to prevent Pocket Edition/Bedrock Edition players from stealing usernames from Java edition players.

### How it works:

Normally, this plugin works just like any other authentication plugin. New players register using `/register <password> <repeat password>`, and registered players can log in with `/login <password>`. However, imagine the following scenario:

A malicious user on Minecraft: Pocket Edition has registered the name of, say a famous YouTuber. They can do this without issue because they can change their name whenever they like. Now imagine that later on, the same YouTuber tries to join the server. Obviously, they can't, as their account has been registered ahead of time and they don't know the password.

MultiAuth-PSPE solves this issue by implementing a mechanism for Java edition players to *prove their ownership* of an account. It works like this:

- Java edition player logs in, can't register as name is taken
- Player is directed to a website that runs on the server
- Player is told to join a Java edition server IP. This server is also run inside of Spigot, however it isn't a real server. The moment the player connects to it, they are disconnected and given a code to enter on the website. Having done so, we know that the player is the rightful owner of that username, as the "verification server" as I'll call it is running in online mode.
- Having entered the code given from the verification server on the website, the player is given a random, temporary password to use to log in.
- After logging in with the temporary password, the player can then change their password to something else.
