#include <com_example_user_ndk_pep_MainActivity.h>

/*
 * Class:     com_example_user_ndk_pep_MainActivity
 * Method:    changeBrightness
 * Signature: ()Ljava/lang/String;
 */

static int rgb_clamp(int value) {
    if (value > 255) {
        return 255;
    }

    if (value < 0) {
        return 0;
    }

    return value;
}

static void brightness(AndroidBitmapInfo *info, void *pixels, float brightnessValue,
                       jboolean r_colour, jboolean g_colour, jboolean b_colour) {
    int xValue, yValue, red, green, blue;
    uint32_t *line;

    for (yValue = 0; yValue < info->height; yValue++) {
        line = (uint32_t *) pixels;

        for (xValue = 0; xValue < info->width; xValue++) {

            //extract the RGB values from the pixel

            if (r_colour) {
                red = (int) ((line[xValue] & 0x00FF0000) >> 16);
                red = rgb_clamp((int) (red + brightnessValue));
            }else
                red = 1;

            if (g_colour) {
                green = (int) ((line[xValue] & 0x0000FF00) >> 8);
                green = rgb_clamp((int) (green + brightnessValue));
            }else
                green = 1;

            if (b_colour) {
                blue = (int) (line[xValue] & 0x00000FF);
                blue = rgb_clamp((int) (blue + brightnessValue));
            }else
                blue = 1;

            // set the new pixel back in
            line[xValue] =
                    ((red << 16) & 0x00FF0000) |
                    ((green << 8) & 0x0000FF00) |
                    (blue & 0x000000FF);
        }

        pixels = (char *) pixels + info->stride;
    }
}

static void makeImageNegative(AndroidBitmapInfo *info, void *pixels, float brightnessValue,
                              jboolean r_colour, jboolean g_colour, jboolean b_colour) {
    int xValue, yValue, red, green, blue;
    uint32_t *line;

    for (yValue = 0; yValue < info->height; yValue++) {
        line = (uint32_t *) pixels;

        for (xValue = 0; xValue < info->width; xValue++) {

            //extract the RGB values from the pixel

            if (r_colour) {
                red = (int) ((line[xValue] & 0x00FF0000) >> 16);
                red = rgb_clamp((int) (255 - red));
            }
            else
                red = 1;

            if (g_colour) {
                green = (int) ((line[xValue] & 0x0000FF00) >> 8);
                green = rgb_clamp((int) (255 - green));
            }
            else
                green = 1;

            if (b_colour) {
                blue = (int) (line[xValue] & 0x00000FF);
                blue = rgb_clamp((int) (255 - blue));
            }
            else
                blue = 1;

            // set the new pixel back in
            line[xValue] =
                    ((red << 16) & 0x00FF0000) |
                    ((green << 8) & 0x0000FF00) |
                    (blue & 0x000000FF);
        }

        pixels = (char *) pixels + info->stride;
    }
}

//JNIEXPORT void JNICALL
JNIEXPORT void Java_com_example_user_ndk_1pep_MainActivity_changeBrightness(JNIEnv *env,
                                                                            jobject obj,
                                                                            jobject bitmap,
                                                                            jboolean makeNegative,
                                                                            jfloat brightnessValue,
                                                                            jbooleanArray rgb_exist) {

    jboolean *oop = (*env)->GetBooleanArrayElements(env, rgb_exist, NULL);

    AndroidBitmapInfo info;
    int ret;
    void *pixels;
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return;
    }

    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }

    if (makeNegative) {
        makeImageNegative(&info, pixels, brightnessValue, oop[0], oop[1], oop[2]);
    }
    else {
        brightness(&info, pixels, brightnessValue, oop[0], oop[1], oop[2]);
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}
