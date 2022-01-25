//
// Created by 黄元镭 on 2020/9/8.
//

#include "QLogger.h"
#include <QDebugger.h>
#include <QDir>
#include <QStringConverter>

#define MIN_CONSOLE_TEXT_SIZE (300)
#define UNICODE_BEGIN (0x7F)

void remove_expired_logs();

namespace global_QLogger {
    class QLoggerStaticInit {
    public:
        QLoggerStaticInit() {
            remove_expired_logs();
        }
    } init;
}

// \033[0;30m 之超级终端的字体背景和颜色显示等]
// 字色              背景              颜色
// ---------------------------------------
// 30                40              黑色
// 31                41              紅色
// 32                42              綠色
// 33                43              黃色
// 34                44              藍色
// 35                45              紫紅色
// 36                46              青藍色
// 37                47              白色
#define CONSOLE_COLOR_BLACK        ("\033[0;30m")
#define CONSOLE_COLOR_DARK_GRAY    ("\033[1;30m")
#define CONSOLE_COLOR_BLUE         ("\033[0;34m")
#define CONSOLE_COLOR_LIGHT_BLUE   ("\033[1;34m")
#define CONSOLE_COLOR_GREEN        ("\033[0;32m")
#define CONSOLE_COLOR_LIGHT_GREEN  ("\033[1;32m")
#define CONSOLE_COLOR_CYAN         ("\033[0;36m")
#define CONSOLE_COLOR_LIGHT_CYAN   ("\033[1;36m")
#define CONSOLE_COLOR_RED          ("\033[0;31m")
#define CONSOLE_COLOR_LIGHT_RED    ("\033[1;31m")
#define CONSOLE_COLOR_PURPLE       ("\033[0;35m")
#define CONSOLE_COLOR_LIGHT_PURPLE ("\033[1;35m")
#define CONSOLE_COLOR_BROWN        ("\033[0;33m")
#define CONSOLE_COLOR_YELLOW       ("\033[1;33m")
#define CONSOLE_COLOR_LIGHT_GRAY   ("\033[0;37m")
#define CONSOLE_COLOR_WHITE        ("\033[1;37m")

const char *consoleColors[] = {
        CONSOLE_COLOR_NONE,
        CONSOLE_COLOR_BLACK,
        CONSOLE_COLOR_DARK_GRAY,
        CONSOLE_COLOR_BLUE,
        CONSOLE_COLOR_LIGHT_BLUE,
        CONSOLE_COLOR_GREEN,
        CONSOLE_COLOR_LIGHT_GREEN,
        CONSOLE_COLOR_CYAN,
        CONSOLE_COLOR_LIGHT_CYAN,
        CONSOLE_COLOR_RED,
        CONSOLE_COLOR_LIGHT_RED,
        CONSOLE_COLOR_PURPLE,
        CONSOLE_COLOR_LIGHT_PURPLE,
        CONSOLE_COLOR_BROWN,
        CONSOLE_COLOR_YELLOW,
        CONSOLE_COLOR_LIGHT_GRAY,
        CONSOLE_COLOR_WHITE,
        CONSOLE_COLOR_DEFAULT,
        CONSOLE_COLOR_INFO,
        CONSOLE_COLOR_HIGH,
        CONSOLE_COLOR_WARN,
        CONSOLE_COLOR_ERROR,
};
const char *threadColors[] = {
        CONSOLE_COLOR_BLUE,
        CONSOLE_COLOR_LIGHT_BLUE,
        CONSOLE_COLOR_GREEN,
        CONSOLE_COLOR_LIGHT_GREEN,
        CONSOLE_COLOR_CYAN,
        CONSOLE_COLOR_LIGHT_CYAN,
        CONSOLE_COLOR_RED,
        CONSOLE_COLOR_LIGHT_RED,
        CONSOLE_COLOR_PURPLE,
        CONSOLE_COLOR_LIGHT_PURPLE,
        CONSOLE_COLOR_BROWN,
        CONSOLE_COLOR_YELLOW,
};

#define SPACE (" ")

inline void writeLog(const QString &);

typedef struct tagTHREAD_INFO {
    int space = 0;
    QFile *file = nullptr;
    int64_t BGN{};
    int64_t END{};
} THREAD_INFO;

namespace logger {
    QMutex thread_info_map_mutex;
    QMap<Qt::HANDLE, THREAD_INFO> threadsInfo;
    bool fileOutput = true;
    bool consoleOutput = true;
    int maxWriteLogFileTextLength = INFINITE_LOG_TEXT_LENGTH;
    int maxKeepDays = 31;
    int maxLogFileSize = 1024 * 1024 * 100;
    auto main_thread_id = QThread::currentThreadId();
    const char *log_file_name_extension = ".log";

    void setFileOutput(bool enabled) {
        logger::fileOutput = enabled;
    }

    void enableFileOutput() {
        logger::setFileOutput(true);
    }

    void disableFileOutput() {
        logger::setFileOutput(false);
    }

    void setConsoleOutput(bool enabled) {
        logger::consoleOutput = enabled;
    }

    void enableConsoleOutput() {
        logger::setConsoleOutput(true);
    }

    void disableConsoleOutput() {
        logger::setConsoleOutput(false);
    }

    void setMaxWriteLogFileTextLength(int maxLength) {
        logger::maxWriteLogFileTextLength = maxLength;
    }

    void clear() {
        QFile file(GetLogFilePath());
        if (file.exists()) {
            file.remove();
        }
    }

    QStdString GetLogFileDirectory() {
        QStdString index;
        QStdString directoryPath;
        do {
            //@formatter:off
            directoryPath = QString("%1/qtcore/1.%2.%3%4").arg(QDir::currentPath())
                                                          .arg(QDateTime::currentDateTime().toString("yyyy").mid(2))
                                                          .arg(QDateTime::currentDateTime().toString("MMdd"))
                                                          .arg(index)
                                                          .replace("/", QDir::separator());
            index = QString(".%1").arg(index.toNumberStr().toInt());
            //@formatter:on
        } while (QFileInfo(directoryPath).isFile());
        return directoryPath;
    }

    QStdString GetLogFilePath() {
        QStdString threadId = QString(".%1").arg(QString::number((qulonglong) QThread::currentThreadId(), 16));
        if (QThread::currentThreadId() == logger::main_thread_id) {
            threadId.clear();
        }

        //@formatter:off
        return QString("%1/qtcore%2%3").arg(GetLogFileDirectory(),
                                            threadId,
                                            logger::log_file_name_extension)
                                        .replace("/", QDir::separator());
        //@formatter:on
    }
}

void remove_expired_logs() {
    auto expired = QDateTime::currentDateTime().addDays(-1 * logger::maxKeepDays).toString("yyyyMMdd") +
                   logger::log_file_name_extension;
    QDir dir(logger::GetLogFileDirectory());
    QStringList filters;
    filters << QString("*%1").arg(logger::log_file_name_extension);
    for (const auto &e: dir.entryList(filters, QDir::Files | QDir::Readable, QDir::Name)) {
        auto lower = e.toLower();
        if (lower < expired) {
            QFile::remove(lower);
        }
    }
}

const char *GetConsoleColorFromCurrentThreadId() {
    const char *threadColor = CONSOLE_COLOR_DEFAULT;
    if (QThread::currentThreadId() != logger::main_thread_id) {
        threadColor = threadColors[(int64_t) QThread::currentThreadId() % (sizeof(threadColors) / sizeof(threadColors[0]))];
    }
    return threadColor;
}

QStdString remove_console_escape_ascii(const QStdString &text) {
    auto trimmed = text;
    for (const auto &e : consoleColors) {
        trimmed = trimmed.replace(e, "");
    }
    return trimmed;
}

void loggerHandler(QtMsgType type, const QMessageLogContext &context, const QString &text) {
    QStdString qtLogText = text;
    //@formatter:off
    switch (type) {
        case QtDebugMsg:    { DBG_SELECT(QString("QtDebugMsg: %1").arg(qtLogText));            } break;
        case QtCriticalMsg: { DBG_ERR(QString("QtCriticalMsg: %1").arg(qtLogText)); DBG_BREAK; } break;
        case QtFatalMsg:    { DBG_ERR(QString("QtFatalMsg: %1").arg(qtLogText));    DBG_BREAK; } break;
        case QtInfoMsg:     { DBG_INFO(QString("QtInfoMsg: %1").arg(qtLogText));               } break;
        case QtWarningMsg:  {
            DBG_SELECT(QString("QtWarningMsg: %1").arg(text));
            if (!text.contains("Remote debugging server started successfully") &&
                !text.contains("Synchronous XMLHttpRequest on the main thread is deprecated")) {
                DBG_BREAK;
            }
        }
        break;
    }
    //@formatter:on
}

void TRACE(const QString &log, bool writeFile, const char *headSymbol, const char *logTextColor, const char *fileName, int lineNumber) {
    logger::thread_info_map_mutex.lock();
    if (!logger::threadsInfo.contains(QThread::currentThreadId())) {
        logger::threadsInfo[QThread::currentThreadId()] = THREAD_INFO();
    }
    if (headSymbol && strcmp(headSymbol, LOG_HEAD_SYMBOL_LEAVE) == 0) {
        logger::threadsInfo[QThread::currentThreadId()].END = QDateTime::currentMSecsSinceEpoch();
        if (logger::threadsInfo[QThread::currentThreadId()].space > 0) {
            logger::threadsInfo[QThread::currentThreadId()].space--;
        }
    }
    int number_of_space = logger::threadsInfo[QThread::currentThreadId()].space;
    if (headSymbol && strcmp(headSymbol, LOG_HEAD_SYMBOL_ENTER) == 0) {
        logger::threadsInfo[QThread::currentThreadId()].BGN = QDateTime::currentMSecsSinceEpoch();
        logger::threadsInfo[QThread::currentThreadId()].space++;
    }

    auto beginMSecsSinceEpoch = logger::threadsInfo[QThread::currentThreadId()].BGN;
    auto endMSecsSinceEpoch = logger::threadsInfo[QThread::currentThreadId()].END;
    logger::thread_info_map_mutex.unlock();

    QStdString spaces;
    for (int i = 0; i < number_of_space; i++) {
        spaces += SPACE;
    }

    QString datetime = QDateTime::currentDateTime().toString("HH:mm:ss.zzz");
    QString threadId = QString("x%1").arg(QString::number((qulonglong) QThread::currentThreadId(), 16));
    threadId = QString("%1%2").arg(QStdString::space(14 - threadId.size())).arg(threadId).replace(SPACE, "x");

    QStdString logText = log;
    QStringList lines;

    QStdString symbol = headSymbol ? headSymbol : LOG_HEAD_SYMBOL;
    if (symbol == LOG_HEAD_SYMBOL_LEAVE) {
        symbol = LOG_HEAD_SYMBOL_EMPTY;
    }
    
    //@formatter:off
    QStdString head = QString("%1[%2%3%4][%5%6%7][%8%9%10] %11%12%13").arg(CONSOLE_COLOR_DEFAULT)
                                                                        .arg(CONSOLE_COLOR_DEFAULT)
                                                                        .arg(datetime)
                                                                        .arg(CONSOLE_COLOR_DEFAULT)
                                                                        .arg(GetConsoleColorFromCurrentThreadId())
                                                                        .arg(QThread::currentThreadId() == logger::main_thread_id ? "MAIN" : "WORK")
                                                                        .arg(CONSOLE_COLOR_DEFAULT)
                                                                        .arg(GetConsoleColorFromCurrentThreadId())
                                                                        .arg(threadId)
                                                                        .arg(CONSOLE_COLOR_DEFAULT)
                                                                        .arg(spaces)
                                                                        .arg(symbol)
                                                                        .arg(logTextColor ? logTextColor : CONSOLE_COLOR_DEFAULT);


    lines = logText.replace("\r", "")
                   .split("\n");
    //@formatter:on

    logText.clear();

    auto trim = head;
    for (const auto &e : consoleColors) {
        trim = trim.replace(e, "");
    }
    int size = trim.size();
    for (int i = 0; i < lines.size(); i++) {
        if (!lines.at(i).isEmpty()) {
            if (logText.initialized()) {
                logText += "\n";
                for (int j = 0; j < size; j++) {
                    logText += SPACE;
                }
            }
            logText += lines.at(i);
        }
    }

    //@formatter:off
    auto source = fileName ?
                    QString(", [\xE6\xBA\x90\xE6\x96\x87\xE4\xBB\xB6: %1:%2]").arg(fileName)
                                                                              .arg(lineNumber) :
                    QString("");
    logText = QString("%1%2%3%4%5").arg(head)
                                   .arg(logText)
                                   .arg(CONSOLE_COLOR_DEFAULT)
                                   .arg(source)
                                   .arg(CONSOLE_COLOR_NONE);
    //@formatter:on

    if (headSymbol && strcmp(headSymbol, LOG_HEAD_SYMBOL_LEAVE) == 0) {
        logText += QString(", [\xE6\xB8\xB2\xE6\x9F\x93\xE6\x97\xB6\xE9\x97\xB4: %1 secs]")
                .arg(QString::number(((double)endMSecsSinceEpoch - (double)beginMSecsSinceEpoch) / 1000.0, 'f', 2));
    }

    if (logger::consoleOutput) {
        printf("%s\n", logText.toStdString().c_str());
        fflush(stdout);
    }

    if (writeFile) {
        writeLog(remove_console_escape_ascii(logText));
    }
}

void writeLog(const QString &log) {
    QStdString logText = log;
    if (logger::maxWriteLogFileTextLength != INFINITE_LOG_TEXT_LENGTH &&
        logText.length() > logger::maxWriteLogFileTextLength) {
        logText = logText.mid(0, logger::maxWriteLogFileTextLength) +
                  QString("\n... (log truncated due to limit length: %1)").arg(logger::maxWriteLogFileTextLength);
    }
    if (logger::fileOutput) {
        QFile *file = nullptr;
        logger::thread_info_map_mutex.lock();
        if (logger::threadsInfo.contains(QThread::currentThreadId())) {
            file = logger::threadsInfo[QThread::currentThreadId()].file;
            if (!file) {
                QFileInfo log_file_info(logger::GetLogFilePath());
                QDir().mkpath(log_file_info.absoluteDir().absolutePath());
                file = new QFile(log_file_info.absoluteFilePath());
                file->open(QIODevice::ReadWrite | QIODevice::Append);
                logger::threadsInfo[QThread::currentThreadId()].file = file;
            }
        }
        logger::thread_info_map_mutex.unlock();

        if (file) {
            auto size = file->size();
            if (size > logger::maxLogFileSize) {
                file->seek(0);
            }

            QTextStream stream(file);

            /* https://embeddeduse.com/2021/01/17/migrating-a-harvester-hmi-from-qt-5-12-to-qt-6-0/ */
            stream.setEncoding(QStringConverter::Utf8);
            if (size == 0) {
                stream << QString(QChar::ByteOrderMark);
            }
            stream << logText;
            stream << "\n";
            file->flush();
        }
    }
}