package org.soraworld.lime.command

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import org.soraworld.lime.config.LimitConfig
import org.soraworld.lime.util.LimitUtils
import org.soraworld.violet.command.CommandViolet
import org.soraworld.violet.command.IICommand
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class CommandLimit(name: String, perm: String?, config: LimitConfig) : CommandViolet(name, perm, config) {

    init {
        addSub(object : IICommand("append", perm, config, true, "app", "add") {
            private val PATTERN = Pattern.compile("([+-]\\d+[mhd])+")
            private val DEADLINE = Pattern.compile("\\[deadline:[0-9-:]+]")
            private val DURATION = Pattern.compile("\\[duration:([+-]\\d+[mhd])+]")
            private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd-HH:mm")
            private val DATE = Pattern.compile("[0-9]{4}(-[0-9]{2}){3}:[0-9]{2}")

            override fun execute(player: Player, args: MutableList<String>): Boolean {
                if (args.size == 1 && PATTERN.matcher(args[0]).matches()) {
                    val item = player.itemInHand
                    if (item != null) {
                        val meta: ItemMeta = item.itemMeta ?: Bukkit.getItemFactory().getItemMeta(item.type)
                        val lore: MutableList<String> = meta.lore ?: ArrayList()
                        for (i in lore.indices) {
                            val line = lore[i]
                            var matcher = DEADLINE.matcher(line)
                            if (matcher.find()) {
                                val mc = DATE.matcher(line)
                                if (mc.find()) {
                                    try {
                                        val date = DATE_FORMAT.parse(mc.group())
                                        date.time = date.time + LimitUtils.limitime(args[0])
                                        lore[i] = LimitUtils.deadLore(date)
                                        LimitUtils.setLore(player, item, meta, lore)
                                        config.send(player, "append deadline")
                                        return true
                                    } catch (e: Throwable) {
                                        config.send(player, "parseException")
                                    }
                                }
                                config.send(player, "invalid date format")
                                return true
                            }
                            matcher = DURATION.matcher(line)
                            if (matcher.find()) {
                                val mc = PATTERN.matcher(line)
                                if (mc.find()) {
                                    lore[i] = LimitUtils.limeLore(mc.group() + args[0])
                                    LimitUtils.setLore(player, item, meta, lore)
                                    config.send(player, "append limitime")
                                    return true
                                }
                                config.send(player, "not match")
                                return false
                            }
                        }
                        lore.add(LimitUtils.limeLore(args[0]))
                        LimitUtils.setLore(player, item, meta, lore)
                        config.send(player, "set limitime")
                    } else {
                        config.send(player, "emptyHand")
                    }
                }
                return true
            }
        })
    }

}
