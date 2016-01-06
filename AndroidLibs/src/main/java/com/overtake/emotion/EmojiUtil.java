package com.overtake.emotion;

public final class EmojiUtil {

	private static java.util.regex.Pattern a = null;

	private EmojiUtil() {
	}

	public static int getEmojiPos(char emojiChar) {
		int i;
		if (emojiChar < '\uE001' || emojiChar > '\uE05A') {
			if (emojiChar < '\uE101' || emojiChar > '\uE15A') {
				if (emojiChar < '\uE201' || emojiChar > '\uE253') {
					if (emojiChar < '\uE301' || emojiChar > '\uE34D') {
						if (emojiChar < '\uE401' || emojiChar > '\uE44C') {
							if (emojiChar < '\uE501' || emojiChar > '\uE537') {
								i = -1;
							} else {
								i = (emojiChar + 416) - 58625;
							}
						} else {
							i = (emojiChar + 340) - 58369;
						}
					} else {
						i = (emojiChar + 263) - 58113;
					}
				} else {
					i = (emojiChar + 180) - 57857;
				}
			} else {
				i = (emojiChar + 90) - 57601;
			}
		} else {
			i = emojiChar - 57345;
		}
		return i;
	}

	public static boolean a(java.lang.String s) {
		boolean flag;
		label0: {
			flag = false;
			char ac[] = s.trim().toCharArray();
			int i = 0;
			do {
				if (i >= ac.length) {
					break label0;
				}
				if (EmojiUtil.getEmojiPos(ac[i]) >= 0) {
					break;
				}
				i++;
			} while (true);
			flag = true;
		}
		return flag;
	}

	public static boolean b(java.lang.String s) {
		boolean flag = false;
		if (a != null) {
			java.util.regex.Matcher matcher = a.matcher(s);
			if (matcher != null) {
				flag = matcher.matches();
			}
		}
		return flag;
	}
}
