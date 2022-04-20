package net.ccbluex.liquidbounce.cn.Insane.Module.fonts.api;

/**
 * @author Artyom Popov
 * @since June 30, 2020
 */
@FunctionalInterface
public interface FontManager {

	FontFamily fontFamily(FontType fontType);

	default FontRenderer font(FontType fontType, int size) {
		return fontFamily(fontType).ofSize(size);
	}
}
