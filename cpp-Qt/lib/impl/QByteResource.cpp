//
// Created by 黄元镭 on 2020/8/12.
//
#include <QFile>
#include <QDirIterator>
#include <qrc.h>
#include <QLogger.h>
#include <QByteResource.h>
#include <QFileSystem.h>

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
bool QByteResource::fuzzyMatching = false;

QByteResource::QByteResource(const QStdString &qrcPath) {
    QStdString actualQrcPath;
    if (QByteResource::isExists(qrcPath, actualQrcPath)) {
        this->clear();
        this->append(QFileSystem::readAllBytes(actualQrcPath));
        this->m_initialized = true;
    }
}

void QByteResource::enableFuzzyMatching() {
    QByteResource::fuzzyMatching = true;
}

void QByteResource::disableFuzzyMatching() {
    QByteResource::fuzzyMatching = false;
}

bool QByteResource::isFuzzyMatching() {
    return QByteResource::fuzzyMatching;
}

bool QByteResource::initialized() const {
    return this->m_initialized;
}

bool QByteResource::isExists(const QStdString &qrcPath) {
    QStdString actualQrcPath;
    return QByteResource::isExists(qrcPath, actualQrcPath);
}

bool QByteResource::isExists(const QStdString &qrcPath, QStdString &actualQrcPath) {
    bool b = false;
    QStdString relativePath = qrcPath;
    QDirIterator it(":", QDirIterator::Subdirectories);
    while (it.hasNext()) {
        QStdString p = it.next();
        if (p.compare(relativePath, Qt::CaseInsensitive) == 0) {
            actualQrcPath = p;
            b = true;
            break;
        }
    }
    if (!b && QByteResource::fuzzyMatching) {
        relativePath = QStdString(qrcPath).replace(qrc.path(), "");
        if (relativePath.initialized() && relativePath.startsWith(':')) {
            relativePath = relativePath.mid(1);
        }
        QDirIterator _it(QRC_ROOT_PATH_SYMBOL, QDirIterator::Subdirectories);
        while (_it.hasNext()) {
            QStdString p = _it.next();
            if (p.endsWith(relativePath, Qt::CaseInsensitive)) {
                actualQrcPath = p;
                b = true;
                break;
            }
        }
    }
    return b;
}

#pragma clang diagnostic pop