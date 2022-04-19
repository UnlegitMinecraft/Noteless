package net.ccbluex.liquidbounce.utils.messages;

import com.google.common.collect.Iterators;
import net.ccbluex.liquidbounce.utils.messages.Message;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * @author xDelsy
 */
final class EmptyMessage extends Message {

    private static EmptyMessage INSTANCE;

    static EmptyMessage get() {
        if (INSTANCE == null) INSTANCE = new EmptyMessage();
        return INSTANCE;
    }

    @Override
    public IChatComponent appendSibling(IChatComponent component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IChatComponent appendText(String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IChatComponent setChatStyle(ChatStyle style) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Spliterator<IChatComponent> spliterator() {
        return Spliterators.emptySpliterator();
    }



}
