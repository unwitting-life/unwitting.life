//
// Created by huang on 11/17/2020.
//

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#ifndef QUtility_H
#define QUtility_H

#include <QCoreApplication>
#include <QProcess>
#include <QStdString.h>

class QUtility {
public:
    static QStdString openFileDialog();

    static QStdString openFileDialog(const QStdString &filter);

    static QStdString openFileDialog(const QStdString &title, const QStdString &filter);

    static QStdString saveFileDialog(const QStdString &);

    static QStdString saveFileDialog(const QStdString &, const QStdString &);

    static QStdString saveFileDialog(const QStdString &, const QStdString &, const QStdString &);

    static QStringList findFiles(const QString &, const QStringList &);
};

#endif //QUtility_H

#pragma clang diagnostic pop