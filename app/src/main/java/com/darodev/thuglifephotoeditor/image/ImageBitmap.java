package com.darodev.thuglifephotoeditor.image;

import com.darodev.thuglifephotoeditor.R;

/**
 * Created by Dariusz Lelek on 11/7/2017.
 * dariusz.lelek@gmail.com
 */

public enum ImageBitmap {
    UNKNOWN("00", 0),
    IMG_1("01", R.drawable.img01),
    IMG_2("02", R.drawable.img02),
    IMG_3("03", R.drawable.img03),
    IMG_4("04", R.drawable.img04),
    IMG_5("05", R.drawable.img05),
    IMG_6("06", R.drawable.img06),
    IMG_7("07", R.drawable.img07),
    IMG_8("08", R.drawable.img08),
    IMG_9("10", R.drawable.img10),
    IMG_10("11", R.drawable.img11),
    IMG_11("12", R.drawable.img12),
    IMG_12("13", R.drawable.img13),
    IMG_13("20", R.drawable.img20),
    IMG_14("21", R.drawable.img21),
    IMG_15("22", R.drawable.img22),
    IMG_16("23", R.drawable.img23),
    IMG_17("31", R.drawable.img31),
    IMG_18("32", R.drawable.img32),
    IMG_19("33", R.drawable.img33),
    IMG_20("34", R.drawable.img34),
    IMG_21("41", R.drawable.img41),
    IMG_22("42", R.drawable.img42),
    IMG_23("43", R.drawable.img43),
    IMG_24("44", R.drawable.img44),
    IMG_25("45", R.drawable.img45),
    IMG_26("50", R.drawable.img50),
    IMG_27("51", R.drawable.img51),
    IMG_28("52", R.drawable.img52),
    IMG_29("53", R.drawable.img53),
    IMG_30("54", R.drawable.img54),
    IMG_31("61", R.drawable.img61),
    IMG_32("62", R.drawable.img62),
    IMG_33("63", R.drawable.img63),
    IMG_34("64", R.drawable.img64),
    IMG_35("65", R.drawable.img65),
    IMG_36("71", R.drawable.img71),
    IMG_37("72", R.drawable.img72),
    IMG_38("73", R.drawable.img73),
    IMG_39("74", R.drawable.img74),
    IMG_40("75", R.drawable.img75),
    IMG_41("80", R.drawable.img80),
    IMG_42("81", R.drawable.img81),
    IMG_43("82", R.drawable.img82),
    IMG_44("83", R.drawable.img83),
    IMG_45("84", R.drawable.img84),
    IMG_46("90", R.drawable.img90),
    IMG_47("91", R.drawable.img91),
    IMG_48("92", R.drawable.img92),
    IMG_49("93", R.drawable.img93),
    IMG_50("94", R.drawable.img94),
    IMG_51("95", R.drawable.img95);

    private String tag;
    private int resId;

    ImageBitmap(String tag, int resId) {
        this.tag = tag;
        this.resId = resId;
    }

    public String getTag() {
        return tag;
    }

    public int getResId() {
        return resId;
    }

    public static ImageBitmap getByTag(String tag){
        for(ImageBitmap imageBitmap : values()){
            if(imageBitmap.getTag().equals(tag)){
                return imageBitmap;
            }
        }
        return UNKNOWN;
    }
}
