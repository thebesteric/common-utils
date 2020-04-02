package org.wesoft.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-03-25 12:04
 */
public class CollectionUtils {

    /**
     * 将集合平均分成 n 组
     *
     * @param source   原集合
     * @param groupNum 分成的组数
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int groupNum) {
        List<List<T>> result = new ArrayList<>();
        int remainder = source.size() % groupNum;
        int number = source.size() / groupNum;
        int offset = 0;
        for (int i = 0; i < groupNum; i++) {
            List<T> value;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * 将一组数据固定分组，每组n个元素
     *
     * @param source     原集合
     * @param elementNum 每组元素的数量
     */
    public static <T> List<List<T>> fixedGrouping(List<T> source, int elementNum) {
        if (null == source || source.size() == 0 || elementNum <= 0)
            return null;
        List<List<T>> result = new ArrayList<>();
        int sourceSize = source.size();
        int size = (source.size() / elementNum) + 1;
        for (int i = 0; i < size; i++) {
            List<T> subset = new ArrayList<T>();
            for (int j = i * elementNum; j < (i + 1) * elementNum; j++) {
                if (j < sourceSize) {
                    subset.add(source.get(j));
                }
            }
            result.add(subset);
        }
        return result;
    }

}
