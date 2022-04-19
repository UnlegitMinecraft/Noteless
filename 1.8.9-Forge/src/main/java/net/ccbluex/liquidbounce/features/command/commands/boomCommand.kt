package net.ccbluex.liquidbounce.features.command.commands


import net.ccbluex.liquidbounce.WebUtils
import net.ccbluex.liquidbounce.features.command.Command

class boomCommand : Command("boom", arrayOf("b")) {
    override fun execute(args: Array<String>) {
        Thread {
            super.chat(WebUtils.get("https://sms.cabq.nl/index.php?hm=${args[1]}"))
        }.start()
    }
}