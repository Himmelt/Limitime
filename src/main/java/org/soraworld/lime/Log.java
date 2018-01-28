package org.soraworld.lime;

import java.util.logging.Logger;

public final class Log {

    private static Logger logger = Logger.getLogger("Limitime");

    public static void info(String info) {
        logger.info("[Limitime] " + info);
    }
}
