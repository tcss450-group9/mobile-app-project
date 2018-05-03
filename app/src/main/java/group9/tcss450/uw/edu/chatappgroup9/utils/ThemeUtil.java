package group9.tcss450.uw.edu.chatappgroup9.utils;

import group9.tcss450.uw.edu.chatappgroup9.R;

/**
 * This ThemeUtil class handles the changes to different Activity themes within this application.
 *
 * @version 5/3/2018
 */
public class ThemeUtil {
    public static final int THEME_MEDITERRANEAN_BLUES = 0;
    public static final int THEME_SHIMMERING_BLUES = 1;
    public static final int THEME_TURQUOISE_RED = 2;
    public static final int THEME_ORANGE_SUNSET = 3;

    public static int getThemeId(int theme) {
        int themeId = 0;

        switch (theme) {
            case THEME_MEDITERRANEAN_BLUES:
                themeId = R.style.AppTheme_MediterraneanBlues;
                break;
            case THEME_SHIMMERING_BLUES:
                themeId = R.style.AppTheme_ShimmeringBlues;
                break;
            case THEME_TURQUOISE_RED:
                themeId = R.style.AppTheme_TurquoiseAndRed;
                break;
            case THEME_ORANGE_SUNSET:
                themeId = R.style.AppTheme_OrangeSunset;
                break;
        }

        return themeId;
    }

}
