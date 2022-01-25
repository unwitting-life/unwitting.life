//
// Created by 黄元镭 on 2020/9/9.
//

#include <QBuffer>
#include <QMap>
#include <QDir>
#include <QTemporaryFile>
#include <QDebug>
#include <QCoreApplication>
#include "QZip.h"
#include <QFileSystem.h>
#include <JlCompress.h>
#include <quazip.h>
#include <quazipfile.h>

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
QZip::QZip(const QStdString &archive) : QZip(QFileSystem::readAllBytes(archive)) {}

QZip::QZip(const QByteArray &archive) {
    if (archive.size() > 0) {
        QByteArray tmp = archive;
        QBuffer buffer(&tmp);
        if (buffer.open(QIODevice::ReadOnly)) {
            QuaZip zip(&buffer);
            if (zip.open(QuaZip::mdUnzip)) {
                for (bool eof = zip.goToFirstFile(); eof; eof = zip.goToNextFile()) {
                    QZip::File file = QZip::getfileInfo(zip.getCurrentFileName());
                    QuaZipFile zipFile(&zip);
                    zipFile.open(QIODevice::ReadOnly);
                    file.data = zipFile.readAll();
                    m_files.push_back(file);
                    zipFile.close();
                }
                zip.close();
            }
            buffer.close();
        }
    }
}

QVector<QZip::File> QZip::files() {
    return this->m_files;
}

QZip::File QZip::getfileInfo(const QStdString &filePath) {
    QZip::File file;
    QFileInfo fileInfo(filePath);
    file.name = fileInfo.fileName();
    file.extension = "." + fileInfo.suffix();
    file.relativePath = filePath;
    file.directory = (file.name == filePath ? QString(".%1").arg(QDir::separator()) : fileInfo.absoluteDir().absolutePath());
    file.directoryName = QFileInfo(file.directory).fileName();
    return file;
}

QByteArray QZip::compressDir(const QString &dir, bool recursive) {
    QBuffer buffer;
    QuaZip zip(&buffer);
    zip.setFileNameCodec("UTF-8");
    if (zip.open(QuaZip::mdCreate)) {
        JlCompress::compressSubDir(&zip, dir, dir, recursive, QDir::Filters());
        zip.close();
    }
    return buffer.data();
}

QByteArray QZip::compressDir7z(const QStdString &dir, bool recursive) {
    QByteArray buffer;
    QStdString sh;
    QTemporaryFile temp;
    if (temp.open()) {
        QStdString tempFileName = temp.fileName() + ".zip";
        sh = QString(R"(%1/7z/7z a -tZip -mx9 "%2" "%3)").arg(
                QCoreApplication::applicationDirPath(),
                tempFileName,
                dir).replace("/", QDir::separator());
        if (recursive) {
            sh += QDir::separator();
            sh += "*";
        }
        sh += "\"";
        system(sh.toStdString().c_str());
        QFile file(tempFileName);
        if (file.open(QFile::ReadOnly)) {
            buffer = file.readAll();
            file.remove();
            file.close();
        }
        temp.close();
    }
    return buffer;
}
#pragma clang diagnostic pop