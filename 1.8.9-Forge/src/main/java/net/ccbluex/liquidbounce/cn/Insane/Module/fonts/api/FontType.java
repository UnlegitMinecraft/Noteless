package net.ccbluex.liquidbounce.cn.Insane.Module.fonts.api;

/**
 * @author Artyom Popov
 * @since June 30, 2020
 */
@SuppressWarnings("SpellCheckingInspection")
public enum FontType {

	DM("diramight.ttf"),
	FIXEDSYS("tahoma.ttf"),
	ICONFONT("stylesicons.ttf"),
	FluxICONFONT("flux.ttf"),
	Check("check.ttf"),
	TenacityBold("Tenacity.ttf"),
	SF("SF.ttf"),
	GENSHIN("GENSHIN.ttf"),
	MAINMENU("mainmenu.ttf"),
	SFBOLD("SFBOLD.ttf"),
	CHINESE("black.ttf"),
	Tahoma("Tahoma.ttf"),
	TahomaBold("Tahoma-Bold.ttf"),
	SFTHIN("SFREGULAR.ttf"),
	OXIDE("oxide.ttf");

	private final String fileName;

	FontType(String fileName) {
		this.fileName = fileName;
	}

	public String fileName() { return fileName; }
}
