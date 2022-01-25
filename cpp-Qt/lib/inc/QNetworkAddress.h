#if defined(_MSC_VER) && (_MSC_VER >= 1600)
#pragma execution_character_set("utf-8")
#endif
//
// Created by 黄元镭 on 2020/10/7.
//

#ifndef QNetworkAddress_H
#define QNetworkAddress_H

#include <QStdString.h>

class QNetworkAddress {
public:
    QNetworkAddress() = default;

    QStdString ip;
    QStdString netmask;
    QStdString gateway;
    QStdString networkNumber;
    QStdString broadcast;
    QStdString humanReadableName;
    QStdString hardwareAddress;
    std::vector<std::string> allAddresses;

    void calculate();
    static QList<QNetworkAddress> allInterfaces();
    static QList<QStdString> allMacAddress();

protected:

    void release();
};


#endif //QNetworkAddress_H
