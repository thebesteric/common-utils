package org.wesoft.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * 二维码生成工具类
 */
public class QRCodeUtils {

    /**
     * 生成本地 QRCode
     *
     * @param text                 二维码内容
     * @param width                宽度
     * @param height               高度
     * @param filePath             保存的文件路径
     * @param margin               边距
     * @param errorCorrectionLevel 纠错等级
     */
    public static void generateQRCodeImage(String text, int width, int height, String filePath, int margin, ErrorCorrectionLevel errorCorrectionLevel) throws IOException {
        BitMatrix bitMatrix = getBitMatrix(text, width, height, margin, errorCorrectionLevel);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "png", path);
    }

    /**
     * 生成 QRCode 数据
     *
     * @param text                 二维码内容
     * @param width                宽度
     * @param height               高度
     * @param margin               边距
     * @param errorCorrectionLevel 纠错等级
     * @return base64
     */
    public static String generateQRCodeImage(String text, int width, int height, int margin, ErrorCorrectionLevel errorCorrectionLevel) throws IOException {
        BitMatrix bitMatrix = getBitMatrix(text, width, height, margin, errorCorrectionLevel);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        return new String("data:image/png;base64," + new Base64().encodeToString(os.toByteArray()));
    }

    private static BitMatrix getBitMatrix(String text, int width, int height, int margin, ErrorCorrectionLevel ErrorCorrectionLevel) {
        BitMatrix bitMatrix = null;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            HashMap<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 字符编码
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel); // 纠错等级为中级
            hints.put(EncodeHintType.MARGIN, margin); // 边距
            bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitMatrix;
    }

}
