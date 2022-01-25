//
// Created by cnhuangr on 6/9/2020.
//

#include <QJson.h>
#include <iostream>
#include <utility>

QJson::QJson(const char *jsonString) : QJson(QStdString(jsonString)) {}

QJson::QJson(const std::string &jsonString) : QJson(QStdString(jsonString)) {}

QJson::QJson(const QStdString &jsonString) : m_initialized(false) {
    this->parseJson(jsonString);
}

QJson::QJson(const nlohmann::ordered_json &json) : m_initialized(true) {
    this->m_json = json;
    this->m_jsonString = this->m_json.dump();
}

void QJson::parseJson(const QStdString &jsonString) {
    if (jsonString.isEmpty()) {
        this->m_json = nlohmann::ordered_json::parse("{}");
    } else {
        this->m_jsonString = jsonString;
        try {
            this->m_json = nlohmann::ordered_json::parse(jsonString.toStdString());
            this->m_initialized = true;
        } catch (const nlohmann::detail::parse_error &e) {
            DBG(jsonString);
            DBG_ERR(e.what());
            this->m_json = nlohmann::ordered_json::parse("{}");
        } catch (...) {
            this->m_json = nlohmann::ordered_json::parse("{}");
        }
    }
}

QJson::~QJson() = default;

bool QJson::find(nlohmann::ordered_json *json, const char *findName, const QStdString &parent, std::function<void(nlohmann::ordered_json::iterator &)> fn) {
    return QJson::find(std::move(json), findName, Qt::CaseSensitive, parent, std::move(fn));
}

bool QJson::find(nlohmann::ordered_json *json, const char *findName, Qt::CaseSensitivity caseSensitivity, const QStdString &parent, std::function<void(nlohmann::ordered_json::iterator &)> fn) {
    bool b = false;
    QStdString trimmedName = findName;
    trimmedName = trimmedName.trimBeginEnd();
    for (auto it = json->begin(); it != json->end() && !b; it++) {
        try {
            QStdString key = it.key();
            QStdString name = key;
            if (findName == nullptr) {
                fn(it);
                if ((*it).type() == nlohmann::detail::value_t::object) {
                    b |= QJson::find(&it.value(), findName, caseSensitivity, name, fn);
                }
            } else {
                if (!parent.isEmpty()) {
                    name = QString("%1.%2").arg(parent, name);
                }
                if (name.compare(trimmedName, caseSensitivity) == 0) {
                    b = true;
                    fn(it);
                } else if ((*it).type() == nlohmann::detail::value_t::object) {
                    b |= QJson::find(&it.value(), trimmedName.toStdString().c_str(), caseSensitivity, name, fn);
                }
            }
        } catch (...) {

        }
    }
    return b;
}

QStdString QJson::value(const QStdString &name) const {
    return this->value(name, "");
}

QStdString QJson::value(const QStdString &name, const QStdString &defaultValue) const {
    bool success;
    return this->value(name, defaultValue, success);
}

QStdString QJson::value(const QStdString &name, bool &success) const {
    return this->value(name, "", success);
}

QStdString QJson::value(const QStdString &name, const QStdString &defaultValue, bool &success) const {
    success = false;
    QStdString result = defaultValue;
    QStdString parent;
    QJson::find(const_cast<nlohmann::ordered_json *>(&this->m_json), name.toStdString().c_str(), this->caseSensitivity, parent,
               [this, &result, &success](nlohmann::ordered_json::iterator &it) {
                   result = this->valueToString(*it, success);
               });
    return result;
}

QStdString QJson::valueToString(const nlohmann::ordered_json &e) const {
    bool success;
    return this->valueToString(e, success);
}

QStdString QJson::valueToString(const nlohmann::ordered_json &e, bool &success) const {
    QStdString str;
    success = false;
    if (!e.is_object() && !e.is_null()) {
        if (e.is_string()) {
            str = e.get<string>();
            success = true;
        } else if (e.is_number_integer()) {
            str = QString::number(e.get<int>());
            success = true;
        } else if (e.is_number_float()) {
            str = QString::number(e.get<float>());
            success = true;
        } else if (e.is_number_unsigned()) {
            str = QString::number(e.get<unsigned>());
            success = true;
        } else if (e.is_boolean()) {
            str = e.get<bool>() ? "true" : "false";
            success = true;
        } else {
            DBG_WARN(QString("无法转换类型: %1").arg(e.type_name()));
        }
    }
    return str;
}

void QJson::each(const std::function<void(const nlohmann::ordered_json &)> &fn) const {
    this->each(nullptr, fn);
}

void QJson::each(const std::function<void(const QStdString &, const nlohmann::ordered_json &)> &fn) const {
    this->each(nullptr, fn);
}

void QJson::each(const char *name, const std::function<void(const nlohmann::ordered_json &)> &fn) const {
    this->each(name, [&fn](const QStdString &, const nlohmann::ordered_json &json) {
        fn(json);
    });
}

void QJson::each(const char *name, const std::function<void(const QStdString &, const nlohmann::ordered_json &)> &fn) const {
    QStdString parent;
    QJson::find(const_cast<nlohmann::ordered_json *>(&this->m_json), name, this->caseSensitivity, parent,
               [&fn](nlohmann::ordered_json::iterator &it) {
                   if ((*it).is_array()) {
                       for (auto &element: *it) {
                           fn(it.key(), element);
                       }
                   } else {
                       fn(it.key(), *it);
                   }
               });
}

bool QJson::initialized() const {
    return this->m_initialized;
}

bool QJson::contains(const QStdString &name) const {
    return this->m_json.find(name.toLocal8Bit().data()) != this->m_json.end();
}

QStdString QJson::toJson() const {
    return this->m_jsonString;
}

nlohmann::ordered_json &QJson::nlohmann_json() const {
    return *((nlohmann::ordered_json *) &this->m_json);
}

QStdString QJson::operator[](const QStdString &key) const {
    return this->value(key);
}

std::vector<std::string> QJson::array(const char *name) const {
    std::vector<std::string> list;
    this->each(name, [this, &list](const nlohmann::ordered_json &e) {
        bool success;
        auto str = this->valueToString(e, success);
        if (success) {
            list.push_back(str.toStdString());
        }
    });
    return list;
}

int QJson::size() const {
    int i = 0;
    this->each(nullptr, [&i](const nlohmann::ordered_json &e) {
        i++;
    });
    return i;
}

void QJson::setValue(const char *name, const char *value) {
    auto list = QString(name).split(".");
    QStdString parent = "";
    for (int i = 0; i < list.count() - 1; i++) {
        if (parent.initialized()) {
            parent += ".";
        }
        parent += list[i];
    }
    if (parent.initialized()) {
        this->each(parent.toStdString().c_str(), [&value](const nlohmann::ordered_json &json) {
            *const_cast<nlohmann::ordered_json *>(&json) = value;
        });
    } else {
        this->m_json[name] = value;
    }
    this->m_jsonString = this->m_json.dump();
}

void QJson::setValue(const QStdString &name, const QStdString &value) {
    this->setValue(name.toStdString().c_str(), value.toStdString().c_str());
}