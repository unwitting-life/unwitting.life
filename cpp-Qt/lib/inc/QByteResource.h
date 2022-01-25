//
// Created by 黄元镭 on 2020/8/12.
//

#ifndef QByteResource_H
#define QByteResource_H

#include <QStdString.h>

class QStdString;

class QByteResource : public QByteArray {
public:
    QByteResource(const QStdString &);

    static void enableFuzzyMatching();

    static void disableFuzzyMatching();

    static bool isFuzzyMatching();

    bool initialized() const;

    static bool isExists(const QStdString &);

    static bool isExists(const QStdString &, QStdString &);

protected:
    static bool fuzzyMatching;

    bool m_initialized{};
};


#endif //QByteResource_H
