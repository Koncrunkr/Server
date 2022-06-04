package ru.comgrid.server.util;

public interface Color{
    /**
     * @return red component value between 0 and 255
     */
    int getRed();

    /**
     * @return green component value between 0 and 255
     */
    int getGreen();

    /**
     * @return blue component value between 0 and 255
     */
    int getBlue();

    /**
     * @return alpha component value between 0 and 100
     */
    int getAlpha();

    static Color ofString(String color){
        return ColorUtil.ofString(color);
    }

    /**
     * @return "rgba(red, green, blue, alpha)", where alpha is normalized value
     */
    @Override
    String toString();

}
