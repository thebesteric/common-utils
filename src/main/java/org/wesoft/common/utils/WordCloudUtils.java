package org.wesoft.common.utils;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.Background;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.FontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 词云工具类
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @link https://github.com/kennycason/kumo
 * @build 2020-04-05 17:55
 */
public class WordCloudUtils {

    public static BufferedImage build(Params params) {

        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();

        // 设置词频
        frequencyAnalyzer.setWordFrequenciesToReturn(params.wordFrequenciesToReturn);
        // 最小词组数量
        frequencyAnalyzer.setMinWordLength(params.minWordLength);
        // 屏蔽词
        frequencyAnalyzer.setStopWords(params.stopWords);

        // 引入中文解析器
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        // 设置图片分辨率
        Dimension dimension = new Dimension(params.dimensionWidth, params.dimensionHeight);
        WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

        // 设置边界及字体
        wordCloud.setPadding(params.padding);
        Font font = new Font(params.fontName, params.fontStyle, params.fontSize);
        wordCloud.setColorPalette(new LinearGradientColorPalette(params.displayFirstColor, params.displaySecondColor, params.displayThirdColor, 30, 30));
        wordCloud.setKumoFont(new KumoFont(font));

        // 设置背景色
        wordCloud.setBackgroundColor(new Color(params.backgroundColorRed, params.backgroundColorGreen, params.backgroundColorBlue));

        wordCloud.setBackground(params.background);
        wordCloud.setFontScalar(params.fontScalar);

        // 生成词云
        final List<WordFrequency> wordFrequencyList = frequencyAnalyzer.load(params.words);
        wordCloud.build(wordFrequencyList);

        return wordCloud.getBufferedImage();
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Params {

        private Params() {
            super();
        }

        public Params(List<String> words) {
            this.words = words;
        }

        private List<String> words = new ArrayList<>();
        private List<String> stopWords = new ArrayList<>();

        private int wordFrequenciesToReturn = 50;
        private int minWordLength = 2;

        private int dimensionWidth = 1024;
        private int dimensionHeight = 768;

        private int padding = 2;

        private String fontName = "STSong-Light";
        private int fontStyle = Font.PLAIN;
        private int fontSize = 20;

        private Color displayFirstColor = Color.RED;
        private Color displaySecondColor = Color.GREEN;
        private Color displayThirdColor = Color.BLUE;

        private int backgroundColorRed = 255;
        private int backgroundColorGreen = 255;
        private int backgroundColorBlue = 255;

        private Background background = new CircleBackground(300);
        private FontScalar fontScalar = new SqrtFontScalar(12, 45);

    }

    public static void main(String[] args) throws IOException {
        List<String> words = new ArrayList<>();
        words.add("中华人民共和国国歌");
        words.add("中华人民共和国国旗");
        words.add("今天天气真好");

        WordCloudUtils.Params params = new WordCloudUtils.Params(words).setDimensionWidth(800).setDimensionHeight(600);
        BufferedImage bufferedImage = WordCloudUtils.build(params);
        ImageIO.write(bufferedImage, "png", new File("d:\\test2.png"));
    }


}
