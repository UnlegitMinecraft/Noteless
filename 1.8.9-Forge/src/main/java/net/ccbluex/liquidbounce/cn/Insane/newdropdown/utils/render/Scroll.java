package net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.render;


import lombok.Getter;
import lombok.Setter;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.animations.Animation;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.animations.Direction;
import net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.animations.impl.SmoothStepAnimation;
import org.lwjgl.input.Mouse;

/**
 * @author cedo
 * @author Foggy
 */
public class Scroll {

    @Getter
    @Setter
    private float maxScroll = Float.MAX_VALUE, minScroll = 0, rawScroll;
    private float scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public void onScroll(int ms) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        rawScroll += Mouse.getDWheel() / 4f;
        rawScroll = Math.max(Math.min(minScroll, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    public boolean isScrollAnimationDone() {
        return scrollAnimation.isDone();
    }

    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }

}
