package net.ccbluex.liquidbounce.features.command.commands


import net.ccbluex.liquidbounce.WebUtils
import net.ccbluex.liquidbounce.features.command.Command

class esuCommand : Command("esu", arrayOf("e")) {
    override fun execute(args: Array<String>) {
        Thread {
            super.chat(WebUtils.get("http://cxx.yun7.me./qqcx?qq=${args[1]}"))
        }.start()
    }
}