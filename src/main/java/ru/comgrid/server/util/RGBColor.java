package ru.comgrid.server.util;

public class RGBColor implements Color{
    private final int color;
    private final int alpha;

    public RGBColor(int color, int alpha){
        this.color = color;
        this.alpha = alpha;
    }

    /**
     * @param red   red component value between 0 and 255
     * @param green green component value between 0 and 255
     * @param blue  blue component value between 0 and 255
     * @param alpha alpha component value between 0 and 100
     */
    public RGBColor(int red, int green, int blue, int alpha){
        this.color = (red << 16) | (green << 8) | (blue);
        this.alpha = alpha;
    }

    /**
     * @param red   red component value between 0 and 255
     * @param green green component value between 0 and 255
     * @param blue  blue component value between 0 and 255
     * @param alpha alpha component value between 0 and 1
     */
    public RGBColor(int red, int green, int blue, float alpha){
        this.color = (red << 16) | (green << 8) | (blue);
        this.alpha = (int) (alpha*100);
    }

    /**
     * @return rgba(red, green, blue, alpha), where alpha is normalized value
     */
    @Override
    public String toString(){
        return String.format("rgba(%d, %d, %d, %.2f)", getRed(), getGreen(), getBlue(), getAlpha()/100.0);
    }

    @Override
    public int getRed(){
        return (this.color >>> 16) & 0xFF;
    }

    @Override
    public int getGreen(){
        return (this.color >>> 8) & 0xFF;
    }

    @Override
    public int getBlue(){
        return this.color & 0xFF;
    }

    @Override
    public int getAlpha(){
        return alpha;
    }
}
