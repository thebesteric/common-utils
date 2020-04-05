package org.wesoft.common.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Position;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * <b>图片工具类</b>
 *
 * @author Eric Joe
 * @version V1.0
 * @info
 * @build 2018年8月27日
 */
public class ImageUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    private static final String PICTURE_FORMAT_JPG = "jpg";

    /**
     * 获取图片尺寸
     *
     * @param path 图片路径
     * @return [0]：width，[1]：height
     */
    public static Integer[] getImgSize(String path) throws Exception {
        File img = new File(path);
        return getImgSize(img);
    }

    /**
     * 获取图片尺寸
     *
     * @param img 图片文件
     * @return [0]：width，[1]：height
     */
    public static Integer[] getImgSize(File img) throws Exception {
        BufferedImage sourceImg = ImageIO.read(new FileInputStream(img));
        Integer[] imgSize = new Integer[2];
        imgSize[0] = sourceImg.getWidth();
        imgSize[1] = sourceImg.getHeight();
        return imgSize;
    }

    /**
     * 添加图片水印
     *
     * @param sourceImgPath 源图片路径
     * @param markImgPath   水印图片路径
     * @param targetImgPath 目标图片路径
     * @param x             水印图片距离目标图片左侧的偏移量，如果 x < 0, 则在正中间
     * @param y             水印图片距离目标图片上侧的偏移量，如果 y < 0, 则在正中间
     * @param alpha         透明度（0.0 -- 1.0）
     */
    public static void watermark(String sourceImgPath, String markImgPath, String targetImgPath, int x, int y, float alpha) {
        try {
            File markImgFile = new File(markImgPath);
            if (!markImgFile.exists()) {
                return;
            }
            File targetImgFile = new File(sourceImgPath);
            Image image = ImageIO.read(targetImgFile);

            int width = image.getWidth(null);
            int height = image.getHeight(null);
            logger.debug("[watermark] width = {}, height = {}", width, height);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);

            Image waterImage = ImageIO.read(markImgFile);
            int _width = waterImage.getWidth(null);
            int _height = waterImage.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            int widthDiff = width - _width;
            int heightDiff = height - _height;

            if (x < 0) {
                x = widthDiff / 2;
            } else if (x > widthDiff) {
                x = widthDiff;
            }
            if (y < 0) {
                y = heightDiff / 2;
            } else if (y > heightDiff) {
                y = heightDiff;
            }

            g.drawImage(waterImage, x, y, _width, _height, null);
            g.dispose();

            File watermarkImgFile = new File(targetImgPath);
            ImageIO.write(bufferedImage, PICTURE_FORMAT_JPG, watermarkImgFile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 添加图片水印
     *
     * @param sourceImgPath 目标图片路径
     * @param markText      水印文字
     * @param targetImgPath 目标图片路径
     * @param fontName      字体名称，如：宋体
     * @param fontStyle     字体样式，如：粗体和斜体（Font.BOLD | Font.ITALIC）
     * @param fontSize      字体大小，单位为像素
     * @param color         字体颜色, 如：Color.RED
     * @param x             水印文字距离目标图片左侧的偏移量，如果x<0, 则在正中间
     * @param y             水印文字距离目标图片上侧的偏移量，如果y<0, 则在正中间
     * @param alpha         透明度（0.0 -- 1.0）
     */
    public static void watermark(String sourceImgPath, String markText, String targetImgPath, String fontName, int fontStyle, int fontSize,
                                 Color color, int x, int y, float alpha) {
        try {
            File file = new File(sourceImgPath);
            Image image = ImageIO.read(file);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, width, height, null);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setColor(color);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));

            int _width = fontSize * getLength(markText);
            int widthDiff = width - _width;
            int heightDiff = height - fontSize;

            if (x < 0) {
                x = widthDiff / 2;
            } else if (x > widthDiff) {
                x = widthDiff;
            }
            if (y < 0) {
                y = heightDiff / 2;
            } else if (y > heightDiff) {
                y = heightDiff;
            }

            g.drawString(markText, x, y + fontSize);
            g.dispose();

            File watermarkImgFile = new File(targetImgPath);
            ImageIO.write(bufferedImage, PICTURE_FORMAT_JPG, watermarkImgFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取字符长度，一个汉字作为 1 个字符, 一个英文字母作为 0.5 个字符
     *
     * @param text text
     * @return 字符长度，如：text="中国",返回 2；text="test",返回 2；text="中国ABC",返回 4.
     */
    private static int getLength(String text) {
        int textLength = text.length();
        int length = textLength;
        for (int i = 0; i < textLength; i++) {
            if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
                length++;
            }
        }
        return (length % 2 == 0) ? length / 2 : length / 2 + 1;
    }

    /**
     * 图片转 base64 字符串
     *
     * @param inputStream 输入流
     */
    public static String imageToBase64(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return "data:image/png;base64," + Base64.encodeBase64String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片转 base64 字符串
     *
     * @param bufferedImage bufferedImage
     */
    public static String imageToBase64(BufferedImage bufferedImage) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", os);
            InputStream in = new ByteArrayInputStream(os.toByteArray());
            return imageToBase64(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片转 base64 字符串
     *
     * @param imgPath 图片地址
     */
    public static String imageToBase64(String imgPath) {
        InputStream in;
        try {
            in = new FileInputStream(imgPath);
            return imageToBase64(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * base64 字符串转图片
     *
     * @param base64Str base64Code
     * @param savePath  保存路径
     */
    public static boolean base64ToImage(String base64Str, String savePath) {
        if (StringUtils.isNotEmpty(base64Str)) {
            try {
                if (base64Str.contains(",")) {
                    base64Str = base64Str.substring(base64Str.indexOf(",") + 1);
                }

                byte[] bytes = Base64.decodeBase64(base64Str);
                for (int i = 0; i < bytes.length; i++) {
                    if (bytes[i] < 0) {
                        bytes[i] += 256;
                    }
                }
                File file = new File(savePath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                OutputStream out = new FileOutputStream(file);
                out.write(bytes);
                out.flush();
                out.close();

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 创建缩略图（截取缩放）
     *
     * @param originImgPath 原始图片路径
     * @param destImgPath   缩略图路径
     * @param scale         缩放比例
     * @param position      截取位置
     * @param suffix        后缀
     */
    public static void thumbnails(String originImgPath, String destImgPath, double scale, Position position, String... suffix) throws Exception {
        File originImg = new File(originImgPath);
        File destImg = new File(destImgPath);
        thumbnails(originImg, destImg, scale, position, suffix);
    }

    /**
     * 创建缩略图（截取缩放）
     *
     * @param originImg 原始图片
     * @param destImg   缩略图
     * @param scale     缩放比例
     * @param position  截取位置
     * @param suffix    后缀
     */
    public static void thumbnails(File originImg, File destImg, double scale, Position position, String... suffix)
            throws Exception {
        Integer[] imgSize = ImageUtils.getImgSize(originImg);
        int width = imgSize[0];
        int height = imgSize[1];
        if (width > height) {
            width = height;
        } else {
            height = width;
        }
        Builder<File> builder = Thumbnails.of(originImg).scale(scale).sourceRegion(position, width, height);
        if (destImg.getPath().lastIndexOf(".") == -1) {
            builder.outputFormat((suffix != null && suffix.length > 0) ? suffix[0] : "jpg");
        }
        builder.toFile(destImg);
    }

    /**
     * 创建缩略图（同比缩放）
     *
     * @param originImgPath 原始图片路径
     * @param destImgPath   缩略图路径
     * @param scale         缩放比例
     * @param quality       输出质量
     * @param suffix        后缀
     */
    public static void thumbnails(String originImgPath, String destImgPath, double scale, double quality, String... suffix) throws Exception {
        File originImg = new File(originImgPath);
        File destImg = new File(destImgPath);
        thumbnails(originImg, destImg, scale, quality, suffix);
    }

    /**
     * 创建缩略图（同比缩放）
     *
     * @param originImg 原始图片
     * @param destImg   缩略图
     * @param scale     缩放比例
     * @param quality   输出质量
     * @param suffix    后缀
     */
    public static void thumbnails(File originImg, File destImg, double scale, double quality, String... suffix)
            throws Exception {
        Thumbnails.of(originImg).scale(scale).outputQuality(quality)
                .outputFormat((suffix != null && suffix.length > 0) ? suffix[0] : "jpg").toFile(destImg);
    }


    public static void main(String[] args) throws Exception {
        watermark("d://test.jpg", "d://1.jpg", "d://test123.jpg", 180, 300, 1f);
        // watermark("d://test.jpg", "Made by Taurus", "d://test.jpg", "宋体", Font.BOLD | Font.ITALIC, 20, Color.BLACK, 0, 0, 0.5f);
        // thumbnails("D:\\upload\\_avatar\\2018-05-15\\fd3596ed-acdc-46f5-a47d-8bbcc21e6048.jpg",
        // "D:\\\\upload\\\\_avatar\\\\2018-05-15\\thumbnails.jpg", 0.25, Positions.CENTER);
        // thumbnails("D:\\upload\\_avatar\\2018-05-15\\fd3596ed-acdc-46f5-a47d-8bbcc21e6048.jpg",
        // "D:\\\\upload\\\\_avatar\\\\2018-05-15\\output1.jpg", 0.25, 1);
    }

}
