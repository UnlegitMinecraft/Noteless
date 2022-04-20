package net.ccbluex.liquidbounce.event;




public class WorldRenderEvent extends Event {
    private float partialTicks;
    public WorldRenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
