//
// Created by cnhuangr on 6/9/2020.
//

#ifndef QJSON_H
#define QJSON_H

#include <QLogger.h>
#include <nlohmann/json.hpp>
#include <QStdString.h>

class QJson : public QObject {
Q_OBJECT;

public:
    explicit QJson(const char *);

    explicit QJson(const std::string &);

    explicit QJson(const QStdString &);

    explicit QJson(const nlohmann::ordered_json &);

    ~QJson() override;

    QStdString value(const QStdString &) const;

    QStdString value(const QStdString &, const QStdString &) const;

    QStdString value(const QStdString &, bool &) const;

    QStdString value(const QStdString &, const QStdString &, bool &) const;

    QStdString valueToString(const nlohmann::ordered_json &) const;

    QStdString valueToString(const nlohmann::ordered_json &, bool &) const;

    std::vector<std::string> array(const char *) const;

    void each(const std::function<void(const nlohmann::ordered_json &)> &) const;

    void each(const std::function<void(const QStdString &, const nlohmann::ordered_json &)> &) const;

    void each(const char *name, const std::function<void(const nlohmann::ordered_json &)> &) const;

    void each(const char *name, const std::function<void(const QStdString &, const nlohmann::ordered_json &)> &) const;

    void setValue(const char *, const char *);

    void setValue(const QStdString &, const QStdString &);

    void parseJson(const QStdString &);

    bool initialized() const;

    bool contains(const QStdString &name) const;

    int size() const;

    QStdString toJson() const;

    nlohmann::ordered_json &nlohmann_json() const;

    QStdString operator[](const QStdString &) const;

    Qt::CaseSensitivity caseSensitivity = Qt::CaseSensitive;
protected:
    nlohmann::ordered_json m_json;

protected:

    static bool find(nlohmann::ordered_json *, const char *, const QStdString &, std::function<void(nlohmann::ordered_json::iterator &)>);

    static bool find(nlohmann::ordered_json *, const char *, Qt::CaseSensitivity, const QStdString &, std::function<void(nlohmann::ordered_json::iterator &)>);

    bool m_initialized;
    QStdString m_jsonString;
};

#endif //QJSON_H
