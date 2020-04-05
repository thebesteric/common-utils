package org.wesoft.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 图片指纹工具类
 *
 * @author Eric Joe
 * @version Ver 1.1
 * @info 指纹工具类
 * @build 2020-01-23 15:17
 */
public class FingerPrint {

    private static final int WIDTH = 8, HEIGHT = 8;

    private static final String HEX_STR = "0123456789ABCDEF";

    private String hashCode;

    public FingerPrint(String filename) {
        this(readImage(filename));
    }

    public FingerPrint(BufferedImage source) {

        // 第一步，缩小尺寸。
        // 将图片缩小到 8x8 的尺寸，总共 64 个像素。
        // 这一步的作用是去除图片的细节，只保留结构、明暗等基本信息，摒弃不同尺寸、比例带来的图片差异。
        BufferedImage thumb = thumbnails(source, WIDTH, HEIGHT, false);

        // 第二步，简化色彩。
        // 将缩小后的图片，转为 64 级灰度。也就是说，所有像素点总共只有64种颜色。
        int[] pixels = new int[WIDTH * HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                pixels[i * HEIGHT + j] = rgbToGray(thumb.getRGB(i, j));
            }
        }

        // 第三步，计算平均值。
        // 计算所有64个像素的灰度平均值。
        int avgPixel = average(pixels);

        // 第四步，比较像素的灰度。
        // 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为 1；小于平均值，记为 0
        int[] comps = new int[WIDTH * HEIGHT];
        for (int i = 0; i < comps.length; i++) {
            if (pixels[i] >= avgPixel) {
                comps[i] = 1;
            } else {
                comps[i] = 0;
            }
        }

        // 第五步，计算哈希值。
        // 将上一步的比较结果，组合在一起，就构成了一个64位的整数，这就是这张图片的指纹。组合的次序并不重要，只要保证所有图片都采用同样次序就行了。
        StringBuilder hashCode = new StringBuilder();
        for (int comp : comps) {
            hashCode.append(binaryToHex(comp));
        }

        // 得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。
        this.hashCode = hashCode.toString();
    }

    /**
     * 读取图片
     *
     * @param filename filename
     */
    public static BufferedImage readImage(String filename) {
        BufferedImage sourceImage = null;
        try {
            File inputFile = new File(filename);
            sourceImage = ImageIO.read(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sourceImage;
    }

    public static BufferedImage thumbnails(BufferedImage source, int width, int height, boolean b) {
        int type = source.getType();
        BufferedImage target;
        double sx = (double) width / source.getWidth();
        double sy = (double) height / source.getHeight();

        if (b) {
            if (sx > sy) {
                sx = sy;
                width = (int) (sx * source.getWidth());
            } else {
                sy = sx;
                height = (int) (sy * source.getHeight());
            }
        }

        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = source.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            target = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else {
            target = new BufferedImage(width, height, type);
        }

        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
        g.dispose();
        return target;
    }

    /**
     * 二进制转为十六进制
     *
     * @param binary binary
     */
    private static char binaryToHex(int binary) {
        char ch;
        switch (binary) {
            case 0:
                ch = '0';
                break;
            case 1:
                ch = '1';
                break;
            case 2:
                ch = '2';
                break;
            case 3:
                ch = '3';
                break;
            case 4:
                ch = '4';
                break;
            case 5:
                ch = '5';
                break;
            case 6:
                ch = '6';
                break;
            case 7:
                ch = '7';
                break;
            case 8:
                ch = '8';
                break;
            case 9:
                ch = '9';
                break;
            case 10:
                ch = 'a';
                break;
            case 11:
                ch = 'b';
                break;
            case 12:
                ch = 'c';
                break;
            case 13:
                ch = 'd';
                break;
            case 14:
                ch = 'e';
                break;
            case 15:
                ch = 'f';
                break;
            default:
                ch = ' ';
        }
        return ch;
    }

    /**
     * 灰度值计算
     *
     * @param pixels 像素
     * @return int 灰度值
     */
    private static int rgbToGray(int pixels) {
        int _red = (pixels >> 16) & 0xFF;
        int _green = (pixels >> 8) & 0xFF;
        int _blue = (pixels) & 0xFF;
        return (int) (0.3 * _red + 0.59 * _green + 0.11 * _blue);
    }

    /**
     * 计算数组的平均值
     *
     * @param pixels 数组
     * @return int 平均值
     */
    private static int average(int[] pixels) {
        float m = 0;
        for (int pixel : pixels) {
            m += pixel;
        }
        m = m / pixels.length;
        return (int) m;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean multiLine) {
        if (!multiLine) {
            return this.hashCode;
        }
        StringBuilder buffer = new StringBuilder();
        for (int i = 1; i <= this.hashCode.length(); i++) {
            buffer.append(hashCode.charAt(i - 1));
            if (i % 8 == 0)
                buffer.append('\n');
        }
        return buffer.toString();
    }

    /**
     * 计算"汉明距离"（Hamming distance）。
     * 如果不相同的数据位不超过 5，就说明两张图片很相似；如果大于 10，就说明这是两张不同的图片。
     *
     * @param sourceHashCode sourceHashCode
     * @param targetHashCode targetHashCode
     */
    public static int hammingDistance(String sourceHashCode, String targetHashCode) {
        int difference = 0;
        int len = sourceHashCode.length();
        for (int i = 0; i < len; i++) {
            if (sourceHashCode.charAt(i) != targetHashCode.charAt(i)) {
                difference++;
            }
        }
        return difference;
    }

    /**
     * 判断两个数组相似度，数组长度必须一致否则抛出异常
     *
     * @param f1 数组_1
     * @param f2 数组_2
     * @return 返回相似度 ( 0.0 ~ 1.0 )
     */
    public static float compare(byte[] f1, byte[] f2) {
        if (f1.length != f2.length)
            throw new IllegalArgumentException("Mismatch FingerPrint length");
        int sameCount = 0;
        for (int i = 0; i < f1.length; ++i) {
            if (f1[i] == f2[i])
                ++sameCount;
        }
        return (float) sameCount / f1.length;
    }

    public static void main(String[] args) {
        try {
            FingerPrint fp0 = new FingerPrint(ImageIO.read(new URL("http://thirdwx.qlogo.cn/mmopen/bVy2VQVTWzZ8bV6zCuKUfrU5X74nEqV1t2U1xQg8bHCaqCUngXrYf7uDaLjlNyMqFSGeDib78v79gbw41p21GyOSvax38cx0L/132")));
            FingerPrint fp1 = new FingerPrint(ImageIO.read(new URL("http://wx.qlogo.cn/mmhead/ver_1/SkDOvWIeFFcnxkGwv5WIXO8Bsck8zB9Ekye4Zial5OOUo94micVsSulCoTht8spLiaAhI2Kq3qdYFQiaNgOfBHgkIqrxibOUDugZV2wt9f2m2Zsw/132")));
            System.out.println(fp0.toString(true));
            System.out.println(fp1.toString(true));

            int difference = hammingDistance(fp0.toString(), fp1.toString());
            System.out.println("汉明距离 = " + difference);

            float compare = compare(fp0.toString().getBytes(), fp1.toString().getBytes());
            System.out.println("compare = " + compare);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
