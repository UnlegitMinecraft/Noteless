package net.ccbluex.liquidbounce.features.module;


import lombok.Getter;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.normal.Main;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.objects.Drag;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.render.Scroll;


public enum ModuleCategory {

    COMBAT("Combat"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    WORLD("World"),
    MISC("Misc"),
    EXPLOIT("Exploit");


    public final String namee;
    public final int posX;
    public final boolean expanded;

    @Getter
    private final Scroll scroll = new Scroll();

    @Getter
    private final Drag drag;
    public int posY = 20;



    ModuleCategory(String name) {
        this.namee = name;
        posX = 40 + (Main.categoryCount * 120);
        drag = new Drag(posX, posY);
        expanded = true;
        Main.categoryCount++;
    }

}
