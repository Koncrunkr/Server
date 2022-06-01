package ru.comgrid.server.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public interface Color{

    Pattern rgbaPattern = Pattern.compile("rgba\\((%d), (%d), (%d), (%f)\\)");
    List<Integer> beautifulColors = List.of(
        0xc013e6,
        0x1806cb,
        0x0004df,
        0xe93030,
        0x00d4ff,
        0x55f53b,
        0xa21ef2,
        0xdaf728
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
        int firstColor = beautifulColors.get(first);
        int secondColor = beautifulColors.get(second);
        int between = (int) (firstColor + (secondColor - firstColor)/ThreadLocalRandom.current().nextFloat());
        return new RGBColor(between, 30);
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
