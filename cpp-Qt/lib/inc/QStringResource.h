//
// Created by 黄元镭 on 2020/8/12.
//

#ifndef QStringResource_H
#define QStringResource_H

#include <QStdString.h>

class QStringResource : public QStdString {
public:
    QStringResource() = default;

    explicit QStringResource(const QStdString &);

    explicit QStringResource(const QStdString &, const char *);

    explicit QStringResource(const char *);

    static void enableFuzzyMatching();

    static void disableFuzzyMatching();

    static bool isFuzzyMatching();

    bool initialized();

protected:
    bool m_initialized;
};


#endif //QStringResource_H
