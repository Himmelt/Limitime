package org.soraworld.lime.config

import org.bukkit.ChatColor
import org.soraworld.lime.constant.Constant
import org.soraworld.violet.config.IIConfig
import java.io.File

class LimitConfig(path: File) : IIConfig(path) {

    override val adminPerm: String = Constant.PERM_ADMIN
    override val headColor: ChatColor = ChatColor.DARK_AQUA
    override var plainHead: String = "[" + Constant.PLUGIN_NAME + "] "

    var deathGone: Boolean = true
        private set

    override fun loadOptions() {
        deathGone = cfgYaml.getBoolean("deathGone", true)
    }

    override fun saveOptions() {
        cfgYaml.set("deathGone", deathGone)
    }

    override fun afterLoad() {}

}
