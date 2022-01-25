#if defined(_MSC_VER) && (_MSC_VER >= 1600)
#pragma execution_character_set("utf-8")
#endif
//
// Created by 黄元镭 on 2020/10/7.
//

#include "QNetworkAddress.h"

#ifdef WIN32
#include <winsock2.h>
#include <Ws2tcpip.h>
#else

#include <netinet/in.h>
#include <arpa/inet.h>

#endif

#include <QNetworkInterface>

void QNetworkAddress::calculate() {
    struct sockaddr_in _ip{};
    if (this->ip.initialized()) {
#ifdef WIN32
        inet_pton(AF_INET, this->ip.toStdString().c_str(), &_ip.sin_addr);
#else
        inet_aton(this->ip.std_string.c_str(), &_ip.sin_addr);
#endif
    } else {
#ifdef WIN32
        inet_pton(AF_INET, this->gateway.toStdString().c_str(), &_ip.sin_addr);
#else
        inet_aton(this->gateway.std_string.c_str(), &_ip.sin_addr);
#endif
    }

    struct sockaddr_in _netmask{};
#ifdef WIN32
    inet_pton(AF_INET, this->netmask.toStdString().c_str(), &_netmask.sin_addr);
#else
    inet_aton(this->netmask.std_string.c_str(), &_netmask.sin_addr);
#endif

    struct in_addr _networkNumber{};
    _networkNumber.s_addr = _ip.sin_addr.s_addr & _netmask.sin_addr.s_addr;
    this->networkNumber = inet_ntoa(_networkNumber);

    struct in_addr _broadcast{};
    _broadcast.s_addr = _networkNumber.s_addr | ~_netmask.sin_addr.s_addr;
    this->broadcast = inet_ntoa(_broadcast);

    QStdString addr = inet_ntoa(_networkNumber);
    int n1, n2, n3, n4;
    sscanf(addr.toStdString().c_str(), "%d.%d.%d.%d", &n1, &n2, &n3, &n4);
    addr = QString("%1.%2.%3.%4").arg(n1).arg(n2).arg(n3).arg(n4);
    this->allAddresses.clear();
    while (addr != this->broadcast) {
        std::string str = addr.toStdString();
        this->allAddresses.push_back(str);
        n4++;
        if (n4 > 255) {
            n4 = 0;
            n3++;
        }
        if (n3 > 255) {
            n3 = 0;
            n2++;
        }
        if (n2 > 255) {
            n2 = 0;
            n1++;
        }
        addr = QString("%1.%2.%3.%4").arg(n1).arg(n2).arg(n3).arg(n4);
    }
}

QList<QNetworkAddress> QNetworkAddress::allInterfaces() {
    QList<QNetworkAddress> addresses;
    QList<QNetworkInterface> interfaces = QNetworkInterface::allInterfaces();
    for (int i = 0; i < interfaces.count(); i++) {
        QNetworkAddress address;
        address.humanReadableName = interfaces.at(i).humanReadableName();
        QList<QNetworkAddressEntry> entryList = interfaces.at(i).addressEntries();
        for (int j = 0; j < entryList.count(); j++) {
            const QNetworkAddressEntry &entry = entryList.at(j);
            if (entry.ip().protocol() == QAbstractSocket::IPv4Protocol &&
                entry.ip() != QHostAddress(QHostAddress::LocalHost)) {
                address.ip = entry.ip().toString();
                address.netmask = entry.netmask().toString();
                address.broadcast = entry.broadcast().toString();
                address.hardwareAddress = interfaces.at(i).hardwareAddress();
                address.calculate();
                addresses.push_back(address);
                break;
            }
        }
    }
    return addresses;
}

QList<QStdString> QNetworkAddress::allMacAddress() {
    QList<QStdString> hardwareAddresses;
    for (const auto &e: QNetworkAddress::allInterfaces()) {
        bool exists = false;
        for (const auto &a: hardwareAddresses) {
            if (a == e.hardwareAddress) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            hardwareAddresses.push_back(e.hardwareAddress);
        }
    }
    return hardwareAddresses;
}
