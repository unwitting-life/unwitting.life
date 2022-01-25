//
// Created by cnhuangr on 6/9/2020.
//

#include <QNetworkPort.h>

int port::max() {
    return 65535;
}

int port::min() {
    return 0;
}

bool port::isValid(int port) {
    return port >= port::min() && port <= port::max();
}