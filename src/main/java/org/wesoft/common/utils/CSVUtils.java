package org.wesoft.common.utils;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * CSV 解析工具类
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-03-31 13:56
 */
public class CSVUtils {

    /**
     * CSV 文件转实体类
     *
     * @param in          输入流
     * @param clazz       实体类 Class
     * @param charsetName 字符集
     */
    public static <T> List<T> csvToBean(InputStream in, Class<T> clazz, String charsetName) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(in, charsetName);
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(clazz);
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withSeparator(',')
                    .withQuoteChar('\'')
                    .withMappingStrategy(strategy).build();
            return csvToBean.parse();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * CSV 文件转实体类
     *
     * @param file        文件
     * @param clazz       实体类 Class
     * @param charsetName 字符集
     */
    public static <T> List<T> csvToBean(File file, Class<T> clazz, String charsetName) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return csvToBean(in, clazz, charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * CSV 转集合
     *
     * @param in          输入流
     * @param charsetName 字符集
     */
    public static List<String[]> csvToList(InputStream in, String charsetName) {
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new InputStreamReader(in, charsetName));
            return csvReader.readAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * CSV 转集合
     *
     * @param file        文件
     * @param charsetName 字符集
     */
    public static List<String[]> csvToList(File file, String charsetName) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            return csvToList(in, charsetName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        File file = new File("D:\\Work\\追星站\\方案1_追星站主控室_群成员积分汇总_20200329_20200330.csv");
        List<String[]> list = csvToList(file, "gbk");
        for (String[] arr : list) {
            System.out.println(Arrays.toString(arr));
        }
    }

}
