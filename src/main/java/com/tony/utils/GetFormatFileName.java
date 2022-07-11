package com.tony.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class GetFormatFileName {
    /**
     * @param
     */
    public final static String getFormatFileName(String prefix, int suffix){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("y-M-d-H-m-s");
        String format = sdf.format(date);
//        System.out.println(format);
//        System.out.println(prefix + "-" + format + ".png");
        return prefix + "-" + format + "-" + suffix + ".png";
    }
}
