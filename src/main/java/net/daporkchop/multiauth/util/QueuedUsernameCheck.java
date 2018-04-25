package net.daporkchop.multiauth.util;

import java.util.function.Consumer;

public class QueuedUsernameCheck {
    public String username;
    public Consumer<Boolean> func;

    public QueuedUsernameCheck(String a, Consumer<Boolean> b)    {
        username = a;
        func = b;
    }
}
