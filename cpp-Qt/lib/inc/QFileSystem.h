//
// Created by 黄元镭 on 2020/9/9.
//

#ifndef QFileSystem_H
#define QFileSystem_H

#include <QByteArray>
#include <QStdString.h>

#ifdef WIN32
#include <windows.h>
#define CLUSTER_UNKNOWN (-1)
#endif

class QFileSystem {
public:
    static QByteArray readAllBytes(const QStdString &);

    static bool writeAllBytes(const QStdString &, const QByteArray &);

#ifdef WIN32
    static std::vector<int64_t> readClusters(const char *);
#endif
};

#endif //QFileSystem_H
