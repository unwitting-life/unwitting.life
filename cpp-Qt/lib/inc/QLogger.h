//
// Created by 黄元镭 on 2020/9/7.
//

#ifndef logger_H
#define logger_H

#include <nlohmann/json.hpp>
#include <QStdString.h>
#include <QApplication>
#include <QFile>
#include <QDebug>
#include <QMutex>
#include <QMap>
#include <QThread>
#include <QDateTime>
#include <iostream>

void loggerHandler(QtMsgType, const QMessageLogContext &, const QString &);

namespace logger {
    void setFileOutput(bool enabled);

    void enableFileOutput();

    void disableFileOutput();

    void setConsoleOutput(bool enabled);

    void enableConsoleOutput();

    void disableConsoleOutput();

    void setMaxWriteLogFileTextLength(int maxLength);

    const char *enter();

    const char *leave();

    void clear();

    QStdString GetLogFilePath();

    QStdString timestamp(bool, const QStdString &, int);
}

#define __NO_LINE__              (0)
#define CONSOLE_COLOR_NONE       "\033[0m"
#define CONSOLE_COLOR_DEFAULT    CONSOLE_COLOR_NONE
#define CONSOLE_COLOR_INFO       "\033[0;30;42m"
#define CONSOLE_COLOR_HIGH       "\033[0;30;47m"
#define CONSOLE_COLOR_WARN       "\033[0;30;43m"
#define CONSOLE_COLOR_ERROR      "\033[0;30;41m"
#define RIGHT_ARROW_SYMBOL       "\xE2\x86\x92\x20"
#define LOG_HEAD_SYMBOL          (CONSOLE_COLOR_DEFAULT RIGHT_ARROW_SYMBOL CONSOLE_COLOR_NONE)
#define LOG_HEAD_SYMBOL_ENTER    (CONSOLE_COLOR_DEFAULT "+ \xE5\x87\xBD\xE6\x95\xB0:\x20" CONSOLE_COLOR_NONE)
#define LOG_HEAD_SYMBOL_LEAVE    (CONSOLE_COLOR_DEFAULT "- \xE5\x87\xBD\xE6\x95\xB0:\x20" CONSOLE_COLOR_NONE)
#define LOG_HEAD_SYMBOL_EMPTY    (CONSOLE_COLOR_DEFAULT "  \xE5\x87\xBD\xE6\x95\xB0:\x20" CONSOLE_COLOR_NONE)
#define INFINITE_LOG_TEXT_LENGTH (-1)
#define DBG_ENCRYPT_KEY          (0xccedfdbaadcf06)
#define DBG_ENCRYPT(x)           TRACE(QString("DBG_ENCRYPT: %1").arg(SimpleCrypt(DBG_ENCRYPT_KEY).encryptToString(QString(x))))
#define DBG_DECRYPT(x)           TRACE(QString("DBG_DECRYPT: %1").arg(SimpleCrypt(DBG_ENCRYPT_KEY).decryptToString(QString(x))))
#define BGN(x)                   TRACE((x), true, LOG_HEAD_SYMBOL_ENTER, nullptr,            __FILE__, __LINE__   )
#define END(x)                   TRACE((x), true, LOG_HEAD_SYMBOL_LEAVE, nullptr,            nullptr,  __NO_LINE__)
#define DBG_INFO(x)              TRACE((x), true, nullptr,               CONSOLE_COLOR_INFO                       )
#define DBG_SELECT(x)            TRACE((x), true, nullptr,               CONSOLE_COLOR_HIGH                       )
#define DBG_WARN(x)              TRACE((x), true, nullptr,               CONSOLE_COLOR_WARN, __FILE__, __LINE__   )
#define DBG_ERR(x)               TRACE((x), true, nullptr,               CONSOLE_COLOR_ERROR                      )
#define DBG_PRINT(x)             TRACE((x), false                                                                 )
#define DBG(x)                   TRACE((x)                                                                        )

void TRACE(const QString &log,
         bool writeFile = true,
         const char *symbol = nullptr,
         const char *logTextColor = nullptr,
         const char *fileName = nullptr,
         int lineNumber = 0);

#endif //logger_H
