package ru.comgrid.server.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public interface Color{

    Pattern rgbaPattern = Pattern.compile("rgba\\((\\d{1,3}), (\\d{1,3}), (\\d{1,3}), (\\d(?:\\.\\d+)?)\\)");
    List<RGBColor> beautifulColors = List.of(
        new RGBColor(0xc013e6, 1),
        new RGBColor(0x1806cb, 1),
        new RGBColor(0x0004df, 1),
        new RGBColor(0xe93030, 1),
        new RGBColor(0x00d4ff, 1),
        new RGBColor(0x55f53b, 1),
        new RGBColor(0xa21ef2, 1),
        new RGBColor(0xdaf728, 1)
    );

    static Color ofString(String rgba){
        var matcher = rgbaPattern.matcher(rgba);
        if(matcher.find()){
            return ofRGBA(
                Integer.parseInt(matcher.group(1)),
                Integer.parseInt(matcher.group(2)),
                Integer.parseInt(matcher.group(3)),
                Float.parseFloat(matcher.group(4))
            );
        }
        throw new IllegalArgumentException("Passed value is not rgba value: " + rgba);
    }

    static Color ofRGBA(int red, int green, int blue, float alpha){
        return new RGBColor(red, green, blue, alpha);
    }

    static Color ofRGBA(int red, int green, int blue, int alpha){
        return new RGBColor(red, green, blue, alpha);
    }

    static Color randomColor(){
        int first = ThreadLocalRandom.current().nextInt(beautifulColors.size());
        int second = ThreadLocalRandom.current().nextInt(beautifulColors.size());
        while(first == second){
            second = ThreadLocalRandom.current().nextInt(beautifulColors.size());
        }
        RGBColor firstColor = beautifulColors.get(first);
        RGBColor secondColor = beautifulColors.get(second);
        var interpolation = ThreadLocalRandom.current().nextFloat();
        return new RGBColor(
            (int) (firstColor.getRed() + (secondColor.getRed() - firstColor.getRed())*interpolation),
            (int) (firstColor.getGreen() + (secondColor.getGreen() - firstColor.getGreen())*interpolation),
            (int) (firstColor.getBlue() + (secondColor.getBlue() - firstColor.getBlue())*interpolation),
            30
        );
    }

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

    /**
     * @return rgba(red, green, blue, alpha), where alpha is normalized value
     */
    @Override
    String toString();
}
