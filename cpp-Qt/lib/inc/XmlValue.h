//
// Created by 黄元镭 on 2020/8/13.
//

#ifndef XmlValue_H
#define XmlValue_H

#include <QStdString.h>
#include <utility>

struct XmlValue {
public:
    QStdString value() const { return this->hasValue() ? this->m_value : ""; }

    virtual void clear() { this->m_value = XmlValue::nullValue(); }

    virtual bool hasValue() const { return this->m_value != XmlValue::nullValue(); };

    explicit XmlValue() {
        this->init(XmlValue::nullValue());
    };

    // TODO: Do not add prefix explicit
    XmlValue(const basic_string<char> value) {
        this->init(value);
    }

    // TODO: Do not add prefix explicit
    XmlValue(const char *value) {
        this->init(value);
    }

    XmlValue(const QString value) {
        this->init(value);
    }

    XmlValue(const QStdString value) {
        this->init(value);
    }

    XmlValue(const int value) {
        this->init(QString::number(value));
    }

    XmlValue(const bool value) {
        this->init(value ? "true" : "false");
    }

    static const char *nullValue() {
        return "\"79c4c4c7-6019-4679-9f9e-c87ccec415c9\"";
    }

protected:

    void init(const QStdString value) {
        this->m_value = value;
    }

    QStdString m_value;
};

#endif //XmlValue_H
