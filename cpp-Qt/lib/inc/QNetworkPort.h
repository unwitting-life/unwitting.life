//
// Created by cnhuangr on 6/9/2020.
//

#ifndef QNetworkPort_H
#define QNetworkPort_H


class port {
public:
    static int max();

    static int min();

    static bool isValid(int port);
};


#endif //QNetworkPort_H
