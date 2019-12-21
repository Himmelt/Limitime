package org.soraworld.lime.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object LimitUtils {

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd-HH:mm")
    private val PATTERN = Pattern.compile("[+-]\\d+[mhd]")
    private val LIMITIME = Pattern.compile("\\[limitime:([+-]\\d+[mhd])+]")
    private val DEADLINE = Pattern.compile("\\[deadline:[0-9-:]+]")
    private val DATE = Pattern.compile("[0-9]{4}(-[0-9]{2}){3}:[0-9]{2}")

    private fun parse(exp: String): Int {
        val last = exp.length - 1
        var `val` = Integer.valueOf(exp.substring(1, last))
        if (exp[0] == '-') `val` *= -1
        if (exp[last] == 'h') `val` *= 60
        if (exp[last] == 'd') `val` *= 1440
        return `val`
    }

    fun limitime(limitime: String): Long {
        var time: Long = 0
        val matcher = PATTERN.matcher(limitime)
        while (matcher.find()) {
            time += parse(matcher.group()).toLong()
        }
        return time * 60000
    }

    private fun deadline(deadline: String): Boolean {
        val matcher = DATE.matcher(deadline)
        if (matcher.find()) {
            try {
                if (DATE_FORMAT.parse(matcher.group()).before(Date())) {
                    return true
                }
            } catch (e: Throwable) {
                println("parseException$deadline")
            }

        }
        return false
    }

    fun deadLore(date: Date): String {
        return "[deadline:" + DATE_FORMAT.format(date) + "]"
    }

    fun limeLore(limitime: String): String {
        return "[limitime:$limitime]"
    }

    fun setLore(player: Player, item: ItemStack, meta: ItemMeta, lore: List<String>) {
        meta.lore = lore
        item.itemMeta = meta
        player.itemInHand = item
    }

    fun isDead(item: ItemStack?): Boolean {
        if (item == null || item.itemMeta == null) return false
        val lore = item.itemMeta.lore
        var i = 0
        while (lore != null && i < lore.size) {
            val line = lore[i]
            val matcher = DEADLINE.matcher(line)
            if (matcher.find() && deadline(matcher.group())) {
                return true
            }
            i++
        }
        return false
    }

    fun hasLimitime(item: ItemStack?): Boolean {
        if (item != null && item.itemMeta != null) {
            val meta = item.itemMeta
            val lore = meta.lore
            var i = 0
            while (lore != null && i < lore.size) {
                val line = lore[i]
                val matcher = LIMITIME.matcher(line)
                if (matcher.find()) {
                    val date = Date()
                    date.time = date.time + limitime(matcher.group())
                    lore[i] = deadLore(date)
                    meta.lore = lore
                    item.itemMeta = meta
                    return true
                }
                i++
            }
        }
        return false
    }
}
