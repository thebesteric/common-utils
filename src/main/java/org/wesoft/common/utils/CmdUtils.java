package org.wesoft.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * CmdUtils
 *
 * @author Eric Joe
 * @version Ver 1.0
 * @build 2020-04-05 13:51
 */
public class CmdUtils {

    public static String exec(String cmd) {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }
            in.close();
            proc.waitFor();
            return result.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
