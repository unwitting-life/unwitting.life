//
// Created by cnhuangr on 2020/9/27.
//
// clazy:excludeall=connect-not-normalized
#pragma clang diagnostic push
#pragma ide diagnostic ignored "cert-err58-cpp"
#pragma ide diagnostic ignored "readability-convert-member-functions-to-static"
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"

#include "QDispatch.h"
#include <QApplication>
#include <QThread>

static QDispatch dispatch;

void QDispatch::invoke(const std::function<void()> &f) {
    dispatch.slotNonParam(f);
}

void QDispatch::invoke(const std::function<void(QString)> &f, const QString &param) {
    dispatch.slot(f, param);
}

QDispatch::QDispatch(QObject *parent) : QObject(parent) {
    connect(this,
            SIGNAL(signalNonParam(std::function<void()>)),
            this,
            SLOT(slotNonParam(std::function<void()>)),
            Qt::BlockingQueuedConnection);

    connect(this,
            SIGNAL(signal(std::function<void(QString)>, QString)),
            this,
            SLOT(slot(std::function<void(QString)>, QString)),
            Qt::BlockingQueuedConnection);
}

void QDispatch::slotNonParam(const std::function<void()> &f) {
#ifdef WINDOWS
#ifdef QT_DEBUG
    if (QApplication::instance()->thread() != QThread::currentThread()) {
        emit this->signalNonParam(f);
    } else {
        f();
    }
#else
    f();
#endif
#else
    f();
#endif
}

void QDispatch::slot(const std::function<void(QString)> &f, const QString &param) {
#ifdef WINDOWS
#ifdef QT_DEBUG
    if (QApplication::instance()->thread() != QThread::currentThread()) {
        emit this->signal(f, param);
    } else {
        f(param);
    }
#else
    f(param);
#endif
#else
    f(param);
#endif
}

#pragma clang diagnostic pop