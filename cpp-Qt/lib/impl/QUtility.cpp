#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#pragma ide diagnostic ignored "misc-no-recursion"
//
// Created by huang on 11/17/2020.
//

/* https://forum.qt.io/topic/33223/qfiledialog-file-not-found */
#include <QFileDialog>
#include <QUtility.h>
#include <QCryptographicHash>
#include <QLogger.h>
#include <QDispatch.h>

QStdString QUtility::openFileDialog() {
    return QUtility::openFileDialog(QStdString::empty(), QStdString::empty());
}

QStdString QUtility::openFileDialog(const QStdString &filter) {
    return QUtility::openFileDialog(QStdString::empty(), filter);
}

QStdString QUtility::openFileDialog(const QStdString &title, const QStdString &filter) {
    BGN("QStdString QUtility::openFileDialog(const QStdString &title, const QStdString &filter, const QStdString &defaultFileName)");
    QString selectedFile;
    QFileDialog fileDialog(nullptr);
    if (title.initialized()) {
        fileDialog.setWindowTitle(title);
    }
    fileDialog.setDirectory(".");
    fileDialog.setNameFilter(filter);
    fileDialog.setAcceptMode(QFileDialog::AcceptOpen);
    fileDialog.setFileMode(QFileDialog::ExistingFiles);
    fileDialog.setViewMode(QFileDialog::Detail);
    if (fileDialog.exec()) {
        selectedFile = fileDialog.selectedFiles().at(0);
    }
    END("QStdString QUtility::openFileDialog");
    return selectedFile.toUtf8().data();
}

QStdString QUtility::saveFileDialog(const QStdString &filter) {
    return QUtility::saveFileDialog("", filter, "");
}

QStdString QUtility::saveFileDialog(const QStdString &title, const QStdString &filter) {
    return QUtility::saveFileDialog(title, filter, "");
}

QStdString QUtility::saveFileDialog(const QStdString &title, const QStdString &filter, const QStdString &defaultFileName) {
    BGN("QStdString QUtility::saveFileDialog(const QStdString &title, const QStdString &filter, const QStdString &defaultFileName)");
    QStdString selectedFile;
    QDispatch::invoke([&] {
        QFileDialog fileDialog(nullptr);
        fileDialog.setWindowTitle(title);
        fileDialog.setDirectory(".");
        fileDialog.setNameFilter(filter);
        fileDialog.selectFile(defaultFileName);
        fileDialog.setAcceptMode(QFileDialog::AcceptSave);
        fileDialog.setFileMode(QFileDialog::ExistingFiles);
        fileDialog.setViewMode(QFileDialog::Detail);
        if (fileDialog.exec()) {
            selectedFile = fileDialog.selectedFiles().at(0);
        }
    });
    END("QStdString QUtility::saveFileDialog");
    return selectedFile;
}

QStringList QUtility::findFiles(const QString &directoryPath, const QStringList &filters) {
    QStringList names;
    QDir dir(directoryPath);
    const auto files = dir.entryList(filters, QDir::Files);
    for (const QString &file: files) {
        QStdString relative = directoryPath + QDir::separator() + file;
        QFileInfo fileInfo(relative);
        if (fileInfo.exists()) {
            names += fileInfo.absoluteFilePath();
        }
    }
    const auto subdirs = dir.entryList(QDir::AllDirs | QDir::NoDotAndDotDot);
    for (const QString &subdir: subdirs) {
        names += findFiles(directoryPath + QDir::separator() + subdir, filters);
    }
    return names;
}

#pragma clang diagnostic pop