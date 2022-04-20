package net.ccbluex.liquidbounce.utils.messages;


import net.minecraft.util.EnumChatFormatting;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author xDelsy
 */
public final class MessageFactory {

	public static @NotNull TextMessage text(@Nullable String text) {
		return TextMessage.of(text);
	}

	public static @NotNull TextMessage text(@Nullable String text, @Nullable EnumChatFormatting color) {
		return TextMessage.of(text, color);
	}

	public static @NotNull HelpMessage help(@NotNull String name, @NotNull String command, @NotNull HelpMessage.UsageMessage... subCommands) {
		return HelpMessage.of(name, command, subCommands);
	}

	public static @NotNull
	HelpMessage.UsageMessage usage(@NotNull String command, @NotNull String description) {
		return HelpMessage.UsageMessage.of(command, description);
	}

	public static @NotNull Message empty() {
		return EmptyMessage.get();
	}

	private MessageFactory() {
		throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
