package life.unwitting;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings({"unused", "DuplicatedCode", "ResultOfMethodCallIgnored", "StringConcatenationInsideStringBufferAppend"})
public class log4j {
    private static final Lock lock = new ReentrantLock();
    public static final String datetimeMillisecondFormat = "yyyy/MM/dd HH:mm:ss.SSS";
    public static final String DirectoryName = "logs";
    public static final String logFileExtension = ".log";
    public static String logFileDirectory = System.getProperty("user.dir") + File.separator + log4j.DirectoryName;
    public static int maxLogFiles = 10;
    public static int maxLogFileSize = 1024 * 1024;
    public static int maxJvmLineSize = 50;
    public static boolean consoleLogger = true;
    public static boolean traceLogger = false;

    public static void err(final Exception e) {
        try {
            if (e != null) {
                log4j.info(e.getClass().getName() + string4j.Colon + string4j.Space + e.getMessage());
                StackTraceElement[] stacks = e.getStackTrace();
                if (stacks != null) {
                    StringBuilder lines = new StringBuilder();
                    for (StackTraceElement elem : stacks) {
                        if (elem != null) {
                            lines.append(elem + string4j.Lf);
                        }
                    }
                    log4j.info(lines.toString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void clear() {
        log4j.lock.lock();
        try {
            File logDirectory = new File(log4j.logFileDirectory);
            if (logDirectory.exists()) {
                logDirectory.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log4j.lock.unlock();
    }

    public static void info(final Object message) {
        log4j.lock.lock();
        String msg = message == null ? string4j.nil : message.toString();
        FileWriter fileWriter = null;
        try {
            String logFilePath = log4j.logFilePath();
            if (lib.notNullOrEmpty(logFilePath)) {
                File logFile = new File(logFilePath);
                File dir = new File(logFile.getAbsoluteFile().getParent());
                dir.mkdirs();
                if (dir.isDirectory() && dir.exists()) {
                    fileWriter = new FileWriter(logFile, true);
                    PrintWriter writer = new PrintWriter(fileWriter);
                    boolean writeTimestamp = true;
                    String now = log4j.now();
                    for (String m : msg.replaceAll(string4j.Cr, string4j.empty).split(string4j.Lf)) {
                        String log = String.format("[%s] %s", now, m);
                        if (!writeTimestamp) {
                            StringBuilder spaces = new StringBuilder(string4j.empty);
                            for (int i = 0; i < now.length(); i++) {
                                spaces.append(string4j.Space);
                            }
                            log = String.format(" %s  %s", spaces, m);
                        }
                        if (runtime4j.isXCPJvm()) {
                            String tmp = m;
                            while (tmp.length() > log4j.maxJvmLineSize) {
                                String limited = tmp.substring(0, log4j.maxJvmLineSize);
                                java.util.logging.Logger.getAnonymousLogger().info(limited);
                                tmp = tmp.substring(log4j.maxJvmLineSize);
                            }
                            if (lib.notNullOrEmpty(tmp)) {
                                java.util.logging.Logger.getAnonymousLogger().info(tmp);
                            }
                        } else {
                            System.out.println(log);
                        }
                        try {
                            writer.println(new String(log.getBytes(string4j.encoding.utf8)));
                        } catch (Exception e) {
                            java.util.logging.Logger.getAnonymousLogger().info(e.getMessage());
                            e.printStackTrace();
                        }
                        writeTimestamp = false;
                    }
                    try {
                        writer.flush();
                    } catch (Exception e) {
                        java.util.logging.Logger.getAnonymousLogger().info(e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    throw new Exception("mkdirs failed.");
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getAnonymousLogger().info(e.getMessage());
            e.printStackTrace();
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
        log4j.lock.unlock();
    }

    public static void trace(Object... args) {
        if (log4j.traceLogger) {
            StringBuilder sb = new StringBuilder();
            sb.append(reflection4j.GetUpstairsFullMethodName());
            sb.append("(");
            if (args != null) {
                if (args.length > 1) {
                    sb.append(string4j.Lf);
                }
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) {
                        sb.append(string4j.Comma + string4j.Lf);
                    }
                    String msg = string4j.empty;
                    try {
                        if (args[i] == null) {
                            msg = string4j.nil;
                        } else {
                            if (args[i] instanceof String) {
                                String arg = (String) args[i];
                                if (arg.length() > 1024) {
                                    msg += arg.substring(0, 1024);
                                } else {
                                    msg += (String) args[i];
                                }
                            } else {
                                msg = args[i].toString();
                            }
                        }
                    } catch (Exception ignored) {

                    }
                    if (args.length > 1) {
                        sb.append("\t");
                    }
                    sb.append("\"" + msg + "\"");
                }
            }
            sb.append(")");
            if (runtime4j.isXCPJvm()) {
                String m = sb.toString()
                        .replaceAll(string4j.Cr, string4j.empty)
                        .replaceAll(string4j.Lf, string4j.empty);
                while (m.length() > log4j.maxJvmLineSize) {
                    String m1 = m.substring(0, log4j.maxJvmLineSize);
                    java.util.logging.Logger.getAnonymousLogger().info(m1);
                    m = m.substring(log4j.maxJvmLineSize);
                }
                if (m.length() > 0) {
                    java.util.logging.Logger.getAnonymousLogger().info(m);
                }
            } else {
                System.out.println(sb);
            }
        }
    }

    @SuppressWarnings("StringConcatenationInLoop")
    public static String logs() {
        String logs = string4j.empty;
        log4j.lock.lock();
        try {
            String[] file = log4j.logFiles();
            ArrayList<String> files = lib.of(file).ToArray().toArrayList(String.class);
            while (files.size() > 0) {
                try {
                    String oldest = null;
                    for (String e : files) {
                        if (lib.isNullOrEmpty(oldest)) {
                            oldest = e;
                        }
                        if (new File(oldest).lastModified() > new File(e).lastModified()) {
                            oldest = e;
                        }
                    }
                    byte[] raw = lib.of(oldest).ToFile().allBytes();
                    if (lib.notNullOrZeroLength(raw)) {
                        logs += new String(raw);
                    }
                    files.remove(oldest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log4j.lock.unlock();
        return logs;
    }

    protected static String[] logFiles() {
        ArrayList<String> files = new ArrayList<String>();
        try {
            if (lib.notNullOrEmpty(log4j.logFileDirectory)) {
                File dir = new File(log4j.logFileDirectory);
                if (dir.isDirectory() && dir.exists()) {
                    File[] listFiles = dir.listFiles();
                    if (lib.notNullOrZeroLength(listFiles)) {
                        for (File e : listFiles) {
                            if (e.isFile() && e.getAbsolutePath().endsWith(log4j.logFileExtension)) {
                                files.add(e.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lib.nm(lib.of(files).toArray(String.class), new String[]{});
    }

    protected static String logFilePath() {
        String logFilePath = null;
        try {
            if (log4j.maxLogFiles > 0 && log4j.maxLogFileSize > 0) {
                String[] logFiles = log4j.logFiles();
                String oldest = null;
                String latest = log4j.newLogFilePath();
                for (int i = 0; i < logFiles.length; i++) {
                    if (i == 0) {
                        oldest = logFiles[i];
                        latest = logFiles[i];
                    } else {
                        if (new File(oldest).lastModified() > new File(logFiles[i]).lastModified()) {
                            oldest = logFiles[i];
                        }
                        if (new File(latest).lastModified() < new File(logFiles[i]).lastModified()) {
                            latest = logFiles[i];
                        }
                    }
                }
                logFilePath = latest;
                File logFile = new File(logFilePath);
                if (logFile.exists() && logFile.length() >= log4j.maxLogFileSize) {
                    if (logFiles.length >= log4j.maxLogFiles) {
                        logFilePath = oldest;
                        new File(logFilePath).delete();
                    } else {
                        logFilePath = log4j.newLogFilePath();
                    }
                }
            }
        } catch (Exception e) {
            logFilePath = log4j.newLogFilePath();
            e.printStackTrace();
        }
        return logFilePath;
    }

    protected static String newLogFilePath() {
        return log4j.logFileDirectory + File.separator + log4j.now("yyyyMMddHHmmss") + log4j.logFileExtension;
    }

    protected static String now() {
        return log4j.now(log4j.datetimeMillisecondFormat);
    }

    protected static String now(String format) {
        String now = string4j.empty;
        try {
            now = new SimpleDateFormat(format).format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }
}