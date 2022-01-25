//
// Created by 黄元镭 on 2022/1/17.
//

#ifndef CPP_QT_MAIN_H
#define CPP_QT_MAIN_H

#include <QApplication>
#include <QUtility.h>

#define UNREFERENCED(x) (x = x)
#define MAIN_INITIALIZE_FATAL (-1)

bool main_initialized(QApplication *);

#endif //CPP_QT_MAIN_H
