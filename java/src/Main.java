import com.fujifilm.eXtensible.lib;
import jp.co.fujixerox.xcp.plugin.repository.Plugin;
import jp.co.fujixerox.xcp.plugin.repository.PluginDescriptor;
import jp.co.fujixerox.xcp.plugin.repository.PluginException;

@SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantThrows"})
public class Main extends Plugin {
    private static final String internalVersion = "Plugin-Internal-Version";
    protected static PluginDescriptor pluginDescriptor = null;
    protected static Main plugin = null;
    public static boolean isStop = false;

    public static void main(String[] args) {
        lib.init(null);
    }

    public Main(final PluginDescriptor descriptor) {
        super(descriptor);
        Main.pluginDescriptor = descriptor;
        Main.plugin = this;
        lib.init(descriptor);
    }

    public static synchronized PluginDescriptor getInstance() {
        return Main.pluginDescriptor;
    }

    public static boolean isPluginRuntime() {
        return Main.pluginDescriptor != null;
    }

    @Override
    public void onStart() throws PluginException {
        Main.isStop = false;
    }

    @Override
    public void onStop() throws PluginException {
        Main.isStop = true;
    }
}
