package ru.comgrid.server.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class ColorUtil{

    private static final Pattern rgbaPattern = Pattern.compile("rgba\\((\\d{1,3}), (\\d{1,3}), (\\d{1,3}), (\\d(?:\\.\\d+)?)\\)");
    private static final List<RGBColor> beautifulColors = List.of(
        new RGBColor(0xc013e6, 30),
        new RGBColor(0x1806cb, 30),
        new RGBColor(0x0004df, 30),
        new RGBColor(0xe93030, 30),
        new RGBColor(0x00d4ff, 30),
        new RGBColor(0x55f53b, 30),
        new RGBColor(0xa21ef2, 30),
        new RGBColor(0xdaf728, 30)
    );

    public static Color ofString(String rgba){
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

    public static Color ofRGBA(int red, int green, int blue, float alpha){
        return new RGBColor(red, green, blue, alpha);
    }

    public static Color ofRGBA(int red, int green, int blue, int alpha){
        return new RGBColor(red, green, blue, alpha);
    }

    public static Color randomColor(){
        int first = ThreadLocalRandom.current().nextInt(beautifulColors.size());
        int second = ThreadLocalRandom.current().nextInt(beautifulColors.size());
        while(first == second){
            second = ThreadLocalRandom.current().nextInt(beautifulColors.size());
        }
        RGBColor firstColor = beautifulColors.get(first);
        RGBColor secondColor = beautifulColors.get(second);
        var interpolationRate = ThreadLocalRandom.current().nextFloat();
        return new RGBColor(
            interpolate(firstColor.getRed(), secondColor.getRed(), interpolationRate),
            interpolate(firstColor.getGreen(), secondColor.getGreen(), interpolationRate),
            interpolate(firstColor.getBlue(), secondColor.getBlue(), interpolationRate),
            30
        );
    }

    private static int interpolate(int first, int second, float interpolationRate){
        return (int) (first + (second - first)*interpolationRate);
    }
}
