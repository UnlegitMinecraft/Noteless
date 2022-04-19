package net.ccbluex.liquidbounce.event;




public class UIRenderEvent extends Event {
    private float particalTicks;

    public UIRenderEvent(float particleTicks) {
        this.particalTicks = particleTicks;
    }

    public float getParticalTicks() {
        return particalTicks;
    }
}
