//
// Created by 黄元镭 on 2020/8/12.
//

#pragma clang diagnostic push
#pragma ide diagnostic ignored "cert-err58-cpp"
#pragma ide diagnostic ignored "modernize-return-braced-init-list"

#ifndef qrc_H
#define qrc_H

#include <QStringResource.h>
#include <QByteResource.h>
#include <QDir>
#include <QDirIterator>
#include <QDebug>
#include <utility>
#include <QLogger.h>

#define QRC_ROOT_PATH_SYMBOL (":")

static class qrc {
public:
    explicit qrc(const char *absolutePath) {
        this->m_absolutePath = absolutePath;
    }

    explicit qrc(const QStdString &absolutePath) {
        this->m_absolutePath = absolutePath;
    }

    qrc(qrc *parent, const char *name) {
        if (parent && name) {
            this->m_absolutePath = parent->combine(name);
        }
    }

    QStdString path() { return this->m_absolutePath; }

    QStringResource stringResource(const char *name) {
        return QStringResource(this->combine(name));
    }

    QByteResource byteResource(const char *name) {
        return QByteResource(this->combine(name));
    }

    QStdString combine(const char *name) {
        return QString(name && name[0] == '/' ? "%1%2" : "%1/%2").arg(this->m_absolutePath, name);
    }

    static void printQrcFileList() {
        QDirIterator it(":", QDirIterator::Subdirectories);
        while (it.hasNext()) {
            DBG(it.next());
        }
    }

protected:
    QStdString m_absolutePath;
} qrc(QRC_ROOT_PATH_SYMBOL);

#endif //qrc_H

#pragma clang diagnostic pop