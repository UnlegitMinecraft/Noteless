

package net.ccbluex.liquidbounce.ui.font

import org.lwjgl.opengl.GL11

data class CachedFont(val displayList: Int, var lastUsage: Long, var deleted: Boolean = false) {
    protected fun finalize() {
        if (!deleted) {
            GL11.glDeleteLists(displayList, 1)
        }
    }
}