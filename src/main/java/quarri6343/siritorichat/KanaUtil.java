package quarri6343.siritorichat;

import java.util.HashMap;
import java.util.Map;

/**
 * 出典：
 * https://qiita.com/pilot/items/928e2cf82b6d20c5ba42
 */
public class KanaUtil {
    private static final Map<Character, Character> lower2upperMap = new HashMap<>();
    private static final Map<Character, Character> upper2lowerMap = new HashMap<>();

    private static final String LOWER_CHARS = "ぁぃぅぇぉゃゅょっァィゥェォャュョッｧｨｩｪｫｬｭｮｯ";
    private static final String UPPER_CHARS = "あいうえおやゆよつアイウエオヤユヨツｱｲｳｴｵﾔﾕﾖﾂ";
    static {
        char[] lowerChars = LOWER_CHARS.toCharArray();
        char[] upperChars = UPPER_CHARS.toCharArray();
        if (lowerChars.length != upperChars.length) {
            throw new RuntimeException("char count not match. lower=" + lowerChars.length + ", upper=" + upperChars.length);
        }
        for (int i = 0; i < lowerChars.length; i++) {
            lower2upperMap.put(lowerChars[i], upperChars[i]);
            upper2lowerMap.put(upperChars[i], lowerChars[i]);
        }
    }

    public static String toUpperCase(String s) {
        return conv(s, lower2upperMap);
    }

    public static String toLowerCase(String s) {
        return conv(s, upper2lowerMap);
    }

    private static String conv(String s, Map<Character, Character> convMap) {
        StringBuilder sb = new StringBuilder();
        s.chars().mapToObj(c -> (char) c).forEach(c -> {
            Character conv = convMap.get(c);
            if (conv == null) {
                sb.append(c);
            } else {
                sb.append(conv);
            }
        });
        return sb.toString();
    }
    
    public static String kataToHira(String str){
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char code = str.charAt(i);
            if ((code >= 0x30a1) && (code <= 0x30f3)) {
                buf.append((char) (code - 0x60));
            } else {
                buf.append(code);
            }
        }
        
        return buf.toString();
    }
}