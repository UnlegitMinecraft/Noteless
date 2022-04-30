package net.ccbluex.liquidbounce.cn.Insane.newdropdown.utils.normal;

public class TimerUtil {

    public long lastMS = System.currentTimeMillis();


    public void reset() {
        lastMS = System.currentTimeMillis();
    }


    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }


    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }


    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }

}
