package life.unwitting;

import jp.co.fujixerox.xcp.plugin.repository.PluginDescriptor;

import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class file4j extends lib<File> {
    public static PluginDescriptor pluginDescriptor = null;

    public file4j(File obj) {
        super(obj);
    }

    @Override
    public boolean isJsonString() {
        boolean available = super.isJsonString();
        if (available) {
            try {
                available = this.m_instance.exists();
            } catch (Exception e) {
                available = false;
                log4j.err(e);
            }
        }
        return available;
    }

    public String[] allLines() {
        ArrayList<String> lines = new ArrayList<String>();
        if (this.isJsonString()) {
            try {
                BufferedReader input = new BufferedReader(new FileReader(this.m_instance.getAbsoluteFile()));
                long length = 0;
                String line;
                while ((line = input.readLine()) != null) {
                    lines.add(line);
                    length += line.length();
                }
            } catch (Exception e) {
                log4j.err(e);
            }
        }
        return of(lines).toArray(String.class);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public byte[] allBytes() {
        byte[] bytes = null;
        if (this.isJsonString()) {
            FileInputStream input = null;
            long length = this.m_instance.length();
            if (length == 0) {
                bytes = new byte[]{};
            } else {
                bytes = new byte[(int) length];
                try {
                    input = new FileInputStream(this.m_instance);
                    input.read(bytes);
                    input.close();
                } catch (Exception e) {
                    log4j.err(e);
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            log4j.err(e);
                        }
                    }
                }
            }
        }
        return bytes;
    }

    public String allText() {
        String allText = null;
        if (this.isJsonString()) {
            if (this.m_instance.length() == 0) {
                allText = "";
            } else {
                byte[] bytes = this.allBytes();
                if (bytes != null) {
                    if (bytes.length == 0) {
                        allText = string4j.empty;
                    } else {
                        allText = new String(bytes);
                    }
                }
            }
        }
        return allText;
    }

    public String extensionName() {
        String extension = string4j.empty;
        if (super.isJsonString()) {
            String name = this.m_instance.getName();
            int lastIndexOf = name.lastIndexOf(".");
            if (lastIndexOf > 0) {
                extension = name.substring(lastIndexOf);
            }
        }
        return extension;
    }

    public boolean write(String raw) {
        boolean b = false;
        BufferedWriter writer = null;
        try {
            if (super.isJsonString()) {
                FileOutputStream output = new FileOutputStream(this.m_instance);
                writer = new BufferedWriter(new OutputStreamWriter(output));
                writer.write(raw);
                b = true;
            }
        } catch (Exception e) {
            log4j.err(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    log4j.err(e);
                }
            }
        }
        return b;
    }

    public String ToAnsiString() {
        String ansi = null;
        if (this.isJsonString()) {
            if (this.m_instance.length() > 0) {
                ansi = new String(of(this.allBytes()).toCharArray());
            } else {
                ansi = string4j.empty;
            }
        }
        return ansi;
    }

    public file4j combinePath(String relativePath) {
        file4j file = this;
        if (this.isJsonString()) {
            file = new file4j(new File(this.m_instance.getAbsolutePath(), relativePath));
        }
        return file;
    }

    public String[] GetChildDirectories() {
        return this.GetChildDirectories(false);
    }

    @SuppressWarnings("ConstantConditions")
    public String[] GetChildDirectories(boolean recursion) {
        ArrayList<String> directories = new ArrayList<String>();
        try {
            if (this.isJsonString() && this.m_instance.isDirectory()) {
                directories.add(this.m_instance.getAbsolutePath());
                if (recursion) {
                    File[] everything = this.m_instance.listFiles();
                    if (notNullOrZeroLength(everything)) {
                        for (File e : everything) {
                            if (e.isDirectory()) {
                                String[] dirs = of(e.getAbsolutePath()).ToFile().GetChildDirectories(recursion);
                                if (notNullOrZeroLength(dirs)) {
                                    ArrayList<String> list = of(dirs).ToArray().toArrayList(String.class);
                                    if (list != null) {
                                        directories.addAll(list);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return nm(of(directories).toArray(String.class), new String[]{});
    }

    public String[] GetFiles() {
        return this.GetFiles(false);
    }

    public String[] GetFiles(boolean all) {
        ArrayList<String> files = new ArrayList<String>();
        try {
            if (this.isJsonString() && this.m_instance.isDirectory()) {
                for (String dir : of(this.m_instance.getAbsolutePath()).ToFile().GetChildDirectories(all)) {
                    File[] listFiles = new File(dir).listFiles();
                    if (notNullOrZeroLength(listFiles)) {
                        for (File e : listFiles) {
                            if (e.isFile()) {
                                files.add(e.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log4j.err(e);
        }
        return nm(of(files).toArray(String.class), new String[]{});
    }

    /*
        public static String readAllText(String fileName) {
            String allText = null;
            File file = file4j.getFile(fileName);
            try {
                if (file.exists()) {
                    allText = f.of(file).allText();
                }
            } catch (Exception e) {
                log4j.info(String.format("File: %s", file.getAbsolutePath()));
                log4j.err(e);
            }
            return allText;
        }

        public static byte[] readAllBytes(String fileName) {
            byte[] allBytes = null;
            File file = file4j.getFile(fileName);
            try {
                if (file.exists()) {
                    allBytes = f.of(file).allBytes();
                }
            } catch (Exception e) {
                log4j.info(String.format("File: %s", file.getAbsolutePath()));
                log4j.err(e);
            }
            return allBytes;
        }
    */
    public void writeString(String s, boolean isAppend) {
        FileWriter fileWriter = null;
        if (this.m_instance != null) {
            try {
                boolean writeable = true;
                if (isAppend && !this.m_instance.exists()) {
                    writeable = this.m_instance.createNewFile();
                } else if (!isAppend && this.m_instance.exists()) {
                    writeable = this.m_instance.delete();
                }
                if (writeable && of(s).isJsonString()) {
                    fileWriter = new FileWriter(this.m_instance, isAppend);
                    PrintWriter writer = new PrintWriter(fileWriter);
                    writer.println(s);
                    writer.flush();
                }
            } catch (Exception e) {
                log4j.info(String.format("File: %s", this.m_instance.getAbsolutePath()));
                log4j.err(e);
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (Exception e) {
                        java.util.logging.Logger.getAnonymousLogger().info(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void writeString(String s) {
        this.writeString(s, false);
    }

    public void appendString(String s) {
        this.writeString(s, true);
    }

//    public static File getFile(String fileName) {
//        return file4j.pluginDescriptor == null ? new File(fileName) : file4j.pluginDescriptor.getWorkFile(fileName);
//    }
}
