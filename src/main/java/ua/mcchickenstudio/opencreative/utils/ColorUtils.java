/*
 * OpenCreative+, Minecraft plugin.
 * (C) 2022-2026, McChicken Studio, mcchickenstudio@gmail.com
 *
 * OpenCreative+ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenCreative+ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ua.mcchickenstudio.opencreative.utils;

import org.jetbrains.annotations.NotNull;

/**
 * <h1>ColorUtils</h1>
 * This class represents utils for
 * manipulating with colors.
 */
public final class ColorUtils {

    /**
     * Parses string to RGB code.
     * <pre>
     * {@code
     * parseRGB("100,100,100"); // 100 100 100
     * parseRGB("250, 13, 2"); // 250 13 2
     * parseRGB("1 3 5"); // 1 3 5
     * parseRGB("999,1,99"); // Unknown colors, 255 255 255
     * }
     * </pre>
     *
     * @param string text to parse RGB colors.
     * @return int array with 3 elements: red, green, blue colors.
     */
    public static int[] parseRGB(@NotNull String string) {
        int[] rgbColor = new int[3];
        rgbColor[0] = 255;
        rgbColor[1] = 255;
        rgbColor[2] = 255;
        try {
            int red, green, blue;
            String[] colorString = new String[3];
            boolean isHexCode = false;
            if (string.contains(", ")) {
                colorString = string.split(", ");
            } else if (string.contains(",")) {
                colorString = string.split(",");
            } else if (string.contains(" ")) {
                colorString = string.split(" ");
            } else if (string.startsWith("#") && string.length() == 7) {
                colorString[0] = string.substring(1, 3);
                colorString[1] = string.substring(3, 5);
                colorString[2] = string.substring(5, 7);
                isHexCode = true;
            } else if (string.length() == 6) {
                colorString[0] = string.substring(0, 2);
                colorString[1] = string.substring(2, 4);
                colorString[2] = string.substring(4, 6);
                isHexCode = true;
            }
            if (colorString.length == 3) {
                red = isHexCode ? Integer.valueOf(colorString[0], 16) : Integer.parseInt(colorString[0]);
                green = isHexCode ? Integer.valueOf(colorString[1], 16) : Integer.parseInt(colorString[1]);
                blue = isHexCode ? Integer.valueOf(colorString[2], 16) : Integer.parseInt(colorString[2]);
                if (red >= 0 && red <= 255 && green >= 0 && green <= 255 && blue >= 0 && blue <= 255) {
                    rgbColor[0] = red;
                    rgbColor[1] = green;
                    rgbColor[2] = blue;
                }
            }
        } catch (Exception ignored) {
        }
        return rgbColor;
    }

}
