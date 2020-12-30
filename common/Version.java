
package io.collapp.common;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {


    private static final long START_DATE = System.nanoTime();

    public static String version() {
        Properties buildProp = new Properties();
        try (InputStream is = new ClassPathResource("io/collapp/build.properties").getInputStream()) {
            buildProp.load(is);
            String build = buildProp.getProperty("build.version");
            return build.endsWith("SNAPSHOT") ? (build + "-" + START_DATE): build;
        } catch (IOException e) {
            return "dev";
        }
    }
}
