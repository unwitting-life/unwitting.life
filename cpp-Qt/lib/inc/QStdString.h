//
// Created by 黄元镭 on 2020/9/5.
//

#ifndef QDebugString_H
#define QDebugString_H

#include <string>
#include <QString>
#include <QSharedPointer>
#include <QDateTime>
#include <QByteResource.h>

using namespace std;

class QByteResource;

class QStdString : public QString {

public:
    QStdString();

    QStdString(const QString &qString);

    QStdString(const char *);

    QStdString(const std::basic_string<char> &);

    QStdString(const QByteArray &);

    static QStdString random();

    static QStdString random(int);

    static QStdString space(int);

    static QStdString format_thousands_separator(long);

    static QStdString formatXml(const QStdString &);

    static QStdString millisecondsToHours(int);

    static QStdString empty();

    static QDateTime compiledDateTime();

    static const char *duplicate(const char *, int);

    static const char *duplicate(const char *);

    static const char *duplicate(const QString &);

    static const char *duplicate(const QString &, int &);

    static const char *percent();

    static QByteArray toAES(const QByteArray &);

    static QStdString toAES(const QStdString &);

    static QByteArray fromAES(const QByteArray &);

    static QByteArray fromAES(const QStdString &);

    static QStdString encrypt(const QStdString &);

    static QStdString decrypt(const QStdString &text, const char *defaultText = nullptr);

    static QStdString machineCodeForEncryptString(int pickLength = -1);

    static QStdString hash(const QStdString &, const QStdString &, int);

    struct KeyValuePair {
    public:
        const char *name = nullptr;
        const char *value = nullptr;

        KeyValuePair(const char *name, const char *value) {
            this->release();
            this->name = QStdString::duplicate(name);
            this->value = QStdString::duplicate(value);
        }

        KeyValuePair(const QStdString &name, const char *value) {
            this->release();
            this->name = QStdString::duplicate(name.toStdString().c_str());
            this->value = QStdString::duplicate(value);
        }

        KeyValuePair(const char *name, const QStdString &value) {
            this->release();
            this->name = QStdString::duplicate(name);
            this->value = QStdString::duplicate(value.toStdString().c_str());
        }

        KeyValuePair(const QStdString &name, const QStdString &value) {
            this->release();
            this->name = QStdString::duplicate(name.toStdString().c_str());
            this->value = QStdString::duplicate(value.toStdString().c_str());
        }

        KeyValuePair(const KeyValuePair &ptr) : KeyValuePair(ptr.name, ptr.value) {};

        ~KeyValuePair() {
            this->release();
        }

        KeyValuePair &operator=(const KeyValuePair &pair) {
            this->release();
            this->name = QStdString::duplicate(name);
            this->value = QStdString::duplicate(value);
            return *this;
        };

        void release() {
            delete[] this->name;
            delete[] this->value;
            this->name = nullptr;
            this->value = nullptr;
        }
    };


public:
    bool initialized() const;

    bool notNullOrEmpty() { return this->initialized(); }

    void clear();

    QStdString &operator+=(int);

    QStdString &operator+=(const QString &);

    QStdString &operator+=(const QByteArray &);

    QStdString &operator+=(const char);

    QStdString &operator+=(const char *);

    QStdString trimBeginEnd() const;

    QString toQStr() const;

    QStdString toNumberStr() const;

    QByteResource toByteResource();

    bool toBool() const;

    std::string std_string;

protected:
    void synchronize();

private:
};

Q_DECLARE_METATYPE(QStdString);

inline QStdString nil(const QString &expr) {
    if (expr.isEmpty()) {
        return "null";
    }
    return expr;
}

#endif //QDebugString_H
