package ui;

import static ui.EscapeSequences.*;

public class ChessStyles {
    public static void resetText() {
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(SET_BG_COLOR_BLACK);
    }
}
