package life.unwitting;

import jp.co.fujixerox.xcp.plugin.repository.Plugin;
import jp.co.fujixerox.xcp.plugin.repository.PluginDescriptor;
import jp.co.fujixerox.xcp.plugin.repository.PluginException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantThrows"})
public class pluginEntry extends Plugin {

    private static final String internalVersion = "Plugin-Internal-Version";
    protected static PluginDescriptor pluginDescriptor = null;
    protected static pluginEntry theApp = null;
    public static boolean isStop = false;
    public static Class<?> mainClass = null;
    public static life.unwitting.httpd4j.httpd4j httpd4j = null;
    private static String MAIN = "main";

    // 找不到主类：
    //  Plugin这个类没有被打包到jar里面，maven编译的时候指定的scope是provider
    //  就是造成编译的时候忽略jar，然后执行要初始化Main类，但是找不到父类Plugin
    //  最终造成找不到主类
    // 资源文件查找：
    //  标记为SourceRoot的这个文件夹，并不是package root，
    //  SourceRoot文件夹下的文件夹才是第一级package root
//    @SuppressWarnings("SpellCheckingInspection")
//    public static void main(String[] args) {
//        try {
//            byte[] json = resource4j.GetBytes(Main.class, "product.json");
//            if (lib.notNullOrZeroLength(json)) {
//                for (Method m : Class.forName(new JSONObject(new String(json)).getString("classRoot")).getDeclaredMethods()) {
//                    // wwwroot对于product1.class反射后的位置就是/wwwroot
//                    if (m.getName().endsWith("launch")) {
//                        m.invoke(null, httpd4j
//                                .newInstance(Main.pluginDescriptor)
//                                .wwwroot("/products/pushtime/cardReader/wwwroot")
//                                .prototype(Main.class)
//                                .disableProxy()
//                                .launch());
//                        break;
//                    }
//                }
//                if (!runtime4j.isXCPJvm()) {
//                    System.out.println(System.in.read());
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public static void init() {
        try {
            try {
                pluginEntry.mainClass = Class.forName("Main");
            } catch (Exception ignored) {

            }
            pluginEntry.httpd4j = lib.init(pluginEntry.pluginDescriptor, pluginEntry.mainClass);
            if (pluginEntry.mainClass != null && !reflection4j.GetUpstairsMethodName().equals(pluginEntry.MAIN)) {
                for (Method m : pluginEntry.mainClass.getMethods()) {
                    if (Modifier.isStatic(m.getModifiers()) && m.getName().equalsIgnoreCase(pluginEntry.MAIN)) {
                        m.invoke(null);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public pluginEntry(final PluginDescriptor descriptor) {
        super(descriptor);
        pluginEntry.pluginDescriptor = descriptor;
        pluginEntry.theApp = this;
    }

    public static synchronized PluginDescriptor getInstance() {
        return pluginEntry.pluginDescriptor;
    }

    public static boolean isXCPPluginRuntime() {
        return pluginEntry.pluginDescriptor != null;
    }

    @Override
    public void onStart() throws PluginException {
        pluginEntry.isStop = false;
        pluginEntry.init();
    }

    @Override
    public void onStop() throws PluginException {
        pluginEntry.isStop = true;
    }
}
