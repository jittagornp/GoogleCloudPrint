/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.geniustree.google.cloudprint.api.util;

import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author jittagorn pitakmetagoon
 */
public class PropertiesFileUtils {

    private static Properties properties;

    public static synchronized Properties load(String path) throws IOException {
        if (properties == null) {
            properties = new Properties();
        }
        properties.load(PropertiesFileUtils.class.getResourceAsStream(path));
        return properties;
    }
}
