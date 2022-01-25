#if defined(_MSC_VER) && (_MSC_VER >= 1600)
#pragma execution_character_set("utf-8")
#endif
//
// Created by cnhuangr on 2020/9/27.
//

#ifndef QDispatch_H
#define QDispatch_H

#include <QObject>

class QDispatch : public QObject {

Q_OBJECT;

public:
    explicit QDispatch(QObject *parent = nullptr);

    ~QDispatch() override = default;

public:

    static void invoke(const std::function<void()> &);

    static void invoke(const std::function<void(QString)> &, const QString &);

signals:

    void signalNonParam(const std::function<void()> &);

    void signal(const std::function<void(QString)> &, const QString &);

public slots:

    void slotNonParam(const std::function<void()> &);

    void slot(const std::function<void(QString)> &, const QString &);

public :
    static const char *unused;
};

#endif //QDispatch_H
