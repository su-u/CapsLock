package capslock;

import javafx.scene.paint.Color;

/**
 *
 * @author RISCassembler
 */
enum ColorSequencer{
    instance;
    static private final Color[] ColorArray = {
        Color.DARKBLUE,
        Color.DARKCYAN,
        Color.DARKGOLDENROD,
        Color.DARKGRAY,
        Color.DARKGREEN,
        Color.DARKKHAKI,
        Color.DARKMAGENTA,
        Color.DARKOLIVEGREEN,
        Color.DARKORANGE,
        Color.DARKORCHID,
        Color.DARKRED,
        Color.DARKSALMON,
        Color.DARKSEAGREEN,
        Color.DARKSLATEBLUE,
        Color.DARKSLATEGRAY,
        Color.DARKSLATEGREY,
        Color.DARKTURQUOISE,
        Color.DARKVIOLET
    };
    static private int ColorIndex = 0;
    static private final int size = ColorArray.length;
    static Color get(){
        if(ColorIndex == size)ColorIndex = 0;
        return ColorArray[ColorIndex++];
    }
}