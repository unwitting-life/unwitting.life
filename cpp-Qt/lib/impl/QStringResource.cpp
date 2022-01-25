//
// Created by 黄元镭 on 2020/8/12.
//

#include "QStringResource.h"
#include <QLogger.h>
#include <QByteResource.h>
#if QT_VERSION < QT_VERSION_CHECK(6, 0, 0)
#include <QtCore/QTextCodec>
#else
#include <QtCore5Compat/QTextCodec>
#endif

QStringResource::QStringResource(const QStdString &absolutePath)
        : QStringResource(absolutePath, nullptr) {}

QStringResource::QStringResource(const QStdString &absolutePath, const char *encoding) :
        m_initialized(false) {
    QByteResource byteResource(absolutePath);
    if (byteResource.initialized()) {
        if (encoding) {
            /* https://stackoverflow.com/questions/65379825/qtcore-qtextcodec-not-found-in-qt-6 */
            /* https://doc.qt.io/qt-6/qtextcodec.html */
            QTextCodec *codec = QTextCodec::codecForName(encoding);
            this->append(codec->toUnicode(byteResource));
        } else {
            this->append(byteResource);
        }
        this->m_initialized = true;
    }
}

QStringResource::QStringResource(const char *absolutePath) : QStringResource(QString(absolutePath)) {
}

void QStringResource::enableFuzzyMatching() {
    BGN("QStringResource::enableFuzzyMatching()");
    QByteResource::enableFuzzyMatching();
    END("QStringResource::enableFuzzyMatching()");
}

void QStringResource::disableFuzzyMatching() {
    QByteResource::disableFuzzyMatching();
}

bool QStringResource::isFuzzyMatching() {
    return QByteResource::isFuzzyMatching();
}

bool QStringResource::initialized() {
    return this->m_initialized;
}