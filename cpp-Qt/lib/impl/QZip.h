//
// Created by 黄元镭 on 2020/9/9.
//

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#ifndef QZip_H
#define QZip_H

#include <QStdString.h>
#include <QObject>
#include <QByteArray>
#include <QDir>

class QZip : public QObject {

Q_OBJECT;

public:
    class File {
    public:
        QStdString name;
        QStdString extension;
        QStdString relativePath;
        QStdString directory;
        QStdString directoryName;
        QByteArray data;
    };

public:
    explicit QZip(const QStdString &);

    QZip(const QByteArray &);

    QVector<File> files();

    static QZip::File getfileInfo(const QStdString &);

    static QByteArray compressDir(const QString &dir, bool recursive = true);

    static QByteArray compressDir7z(const QStdString &dir, bool recursive = true);

protected:
    QVector<File> m_files;
};


#endif //QZip_H

#pragma clang diagnostic pop