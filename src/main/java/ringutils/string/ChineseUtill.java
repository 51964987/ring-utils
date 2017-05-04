package ringutils.string;

public class ChineseUtill {
    
    private static boolean isChinese(char c) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(c);
        if (sc == Character.UnicodeScript.HAN) {
            return true;
        }
        return false;
    }
    
    public static boolean isPunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (    // punctuation, spacing, and formatting characters
                ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                // symbols and punctuation in the unified Chinese, Japanese and Korean script
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                // fullwidth character or a halfwidth character
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                // vertical glyph variants for east Asian compatibility
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                // vertical punctuation for compatibility characters with the Chinese Standard GB 18030
                || ub == Character.UnicodeBlock.VERTICAL_FORMS
                // ascii
                || ub == Character.UnicodeBlock.BASIC_LATIN
                ) {
            return true;
        } else {
            return false;
        }
    }
    
    private static Boolean isUserDefined(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.NUMBER_FORMS
                || ub == Character.UnicodeBlock.ENCLOSED_ALPHANUMERICS
                || ub == Character.UnicodeBlock.LETTERLIKE_SYMBOLS
                || c == '\ufeff'
                || c == '\u00a0'
                )
            return true;
        return false;
    }
    
    public static Boolean isMessy(String str)  {
        float chlength = 0;
        float count = 0;
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(isPunctuation(c) || isUserDefined(c))
                continue;
            else {
                if(!isChinese(c)) {
                    count = count + 1;
                }
                chlength ++;
            }
        }
        float result = count / chlength;
        if(result > 0.3)
            return true;
        return false;
    }
    
}
