package org.soraworld.lime

import org.soraworld.lime.command.CommandLimit
import org.soraworld.lime.config.LimitConfig
import org.soraworld.lime.constant.Constant
import org.soraworld.lime.listener.EventListener
import org.soraworld.violet.VioletPlugin
import org.soraworld.violet.command.IICommand
import org.soraworld.violet.config.IIConfig
import java.io.File

class Limitime : VioletPlugin() {

    override fun registerCommand(): IICommand? {
        return if (iconfig is LimitConfig) CommandLimit(Constant.PLUGIN_ID, Constant.PERM_ADMIN, iconfig as LimitConfig)
        else null
    }

    override fun registerConfig(path: File): IIConfig {
        return LimitConfig(path)
    }

    override fun registerEvents() {
        if (iconfig is LimitConfig) registerEvent(EventListener(iconfig as LimitConfig))
    }

    override fun afterEnable() {}

    override fun beforeDisable() {}

}
