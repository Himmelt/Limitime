package io.github.himmelt.limitime;
/* Created by Kami on 2016/6/1.*/

import java.util.logging.Logger;

public final class Log {

    private static Logger logger = Logger.getLogger("Limitime");

    public static void info(String info) {
        logger.info("[Limitime] " + info);
    }
}
