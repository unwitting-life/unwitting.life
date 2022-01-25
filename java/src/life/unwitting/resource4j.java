package life.unwitting;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("unused")
public class resource4j {
    public static byte[] GetBytes(String uri) {
        return resource4j.GetBytes(resource4j.class, uri);
    }

    public static byte[] GetBytes(Class<?> rootClass, String relativeUri) {
        byte[] data = null;
        try {
            ArrayList<Byte> raw = new ArrayList<Byte>();
            InputStream input = rootClass.getResourceAsStream(relativeUri);
            if (input != null) {
                data = lib.of(input).allBytes();
                input.close();
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return data;
    }

    public static void checkPath(Class<?> type, String absolutePath) {
        try {
            final String path = "sample/folder";
            Class<?> t = type == null ? resource4j.class : type;
            final File jarFile = new File(t.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (jarFile.isFile()) {  // Run with JAR file
                final JarFile jar = new JarFile(jarFile);
                final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.startsWith(path + "/")) { //filter according to the path
                        System.out.println(name);
                    }
                }
                jar.close();
            } else { // Run with IDE
//                final URL url = Launcher.class.getResource("/" + path);
//                if (url != null) {
//                    try {
//                        final life.unwitting.lib.File apps = new life.unwitting.lib.File(url.toURI());
//                        for (life.unwitting.lib.File app : apps.listFiles()) {
//                            System.out.println(app);
//                        }
//                    } catch (Exception ignored) {
//                    }
//                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
    }
}
