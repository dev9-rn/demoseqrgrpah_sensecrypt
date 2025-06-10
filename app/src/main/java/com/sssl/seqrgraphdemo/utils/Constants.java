package com.sssl.seqrgraphdemo.utils;

public class Constants {


    public static final float FINGER_THRESHOLD =4.00f;

   public static final String CRYPTOGRAPH_IMAGES_DIR = "BBC_CODES";

    public static final String FACELINK_IMAGES_DIR = "FACE_LINK";

    public static final String FROM_WHERE = "fromWhere";
    public static final String FROM_DECODE_SCREEN = "fromDecode";
    public static final String FROM_SCAN_SCREEN = "fromScan";

    public static final String ID = "ID";


    public static final String FINGER_IMAGE_KEY_PREFIX = "finger_image_";
    public static final String FINGER_TEMPLATE_KEY_PREFIX = "finger_template_";

    public static final String CONTENT_TYPE_IMAGE_JPG = "image/jpeg";
    public static final String CONTENT_TYPE_IMAGE_BMP = "image/bmp";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";


 public static final String EXTRA_SCORE = "score";

 public static final String EXTRA_FACE_IMAGE_NAME = "faceImageName";

 public static final String EXTRA_DEMOGRAPHICS = "demographics";

    private Constants() {
    }




    ///Default values for Cryptograph generation

    public static final Boolean INCLUDE_FACE_TEMPLATE = true;

    public static final Boolean INCLUDE_COMPRESSED_FACE = true;

    public static final int COMPRESSION_LEVEL = 5;

    public static final Boolean INCLUDE_DEMOGRAPHICS = true;

    public static final int NUM_FINGERS_TO_INCLUDE=0;

    public static final int MAX_FINGER_TEMPLATE_SIZE=184;

    public static final int BLOCK_ROWS =0;

    public static final int BLOCK_ROWS_FOR_STRIP =4;

    public static final int BLOCK_COLS=0;

    public static final int ERROR_CORRECTION = 6;

    public static final int THICKNESS = 1;

    public static final int GRID_SIZE = 6;

    public static final Boolean HAS_EXPIRY_DATE = true;

    public static final String BARCODE_TITLE_ALIGNMENT = "center";

    public static final String BARCODE_TITLE_LOCATION = "bottom";

    public static final int BARCODE_TITLE_OFFSET = 10;


   public static final String FACELINK_IMAGES_FOLDER_NAME = "FaceLink";

}
