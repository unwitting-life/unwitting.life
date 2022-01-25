//
// Created by 黄元镭 on 2020/9/5.
//

#include <QStdString.h>
#include <QXmlStreamReader>
#include <QCryptographicHash>
#include <utility>
#include <QNetworkAddress.h>
#include <qaesencryption.h>
#include <SimpleCrypt.h>
#include <QLogger.h>
#include <random>

namespace global_QDebugString {
    QStdString machineCode(int pickLength = -1) {
        QStdString hardwareAddress;
        for (const auto &a: QNetworkAddress::allMacAddress()) {
            if (hardwareAddress.initialized()) {
                hardwareAddress += ":";
            }
            hardwareAddress += a;
        }
        return QStdString::hash(hardwareAddress, "salt", pickLength);
    }

    qint64 simpleCryptKey() {
        static auto fixed = machineCode(3).toInt(nullptr, 16);
        return 0x1a81be7b16fc8345 - fixed;
    }

    QAESEncryption aes(QAESEncryption::AES_256, QAESEncryption::CBC);

    QStdString key() { return machineCode(); }

    QStdString iv() { return global_QDebugString::key() + "_iv"; }

    QByteArray hashKey() {
        return QCryptographicHash::hash(global_QDebugString::key().toLocal8Bit(), QCryptographicHash::Sha256);
    }

    QByteArray hashIV() {
        return QCryptographicHash::hash(global_QDebugString::iv().toLocal8Bit(), QCryptographicHash::Md5);
    }
}

QStdString::QStdString() {
    this->synchronize();
}

QStdString::QStdString(const QString &value) : QString(value) {
    this->synchronize();
}

QStdString::QStdString(const char *value) : QString(value) {
    this->synchronize();
}

QStdString::QStdString(const std::basic_string<char> &value) : QString(value.c_str()) {
    this->synchronize();
}

QStdString::QStdString(const QByteArray &value) : QString(value) {
    this->synchronize();
}

QStdString QStdString::random() {
    return QStdString::random(8);
}

QStdString QStdString::random(int size) {
    QStdString str;

    /* https://blog.csdn.net/weixin_43778179/article/details/105271279 */
    std::random_device random;
    auto engine = std::default_random_engine(random());
    std::uniform_int_distribution<int> distribution(1, 1000);
    int i;
    for (i = 0; i < size; ++i) {
        switch ((distribution(engine) % 3)) {
            case 1:
                str += 'A' + distribution(engine) % 26;
                break;
            case 2:
                str += 'a' + distribution(engine) % 26;
                break;
            default:
                str += '0' + distribution(engine) % 10;
                break;
        }
    }
    return str;
}

QStdString QStdString::space(int size) {
    QStdString s;
    for (int i = 0; i < size; i++) {
        s += " ";
    }
    return s;
}

void QStdString::synchronize() {
    this->std_string = this->toStdString();
}

bool QStdString::initialized() const {
    return !this->isEmpty();
}

void QStdString::clear() {
    QString::clear();
    this->synchronize();
}

QStdString &QStdString::operator+=(const QString &value) {
    this->append(value);
    this->synchronize();
    return *this;
}

QStdString &QStdString::operator+=(int value) {
    this->append(QString::number(value));
    this->synchronize();
    return *this;
}

QStdString &QStdString::operator+=(const QByteArray &value) {
    this->append(value);
    this->synchronize();
    return *this;
}

QStdString &QStdString::operator+=(const char value) {
    this->append(value);
    this->synchronize();
    return *this;
}

QStdString &QStdString::operator+=(const char *value) {
    this->append(value);
    this->synchronize();
    return *this;
}

QStdString QStdString::format_thousands_separator(long value) {
    long m, n = 0;
    QStdString num = QString::number(value);
    int size = (num.size() * 2) + 1;
    char *buf = new char[size];
    char *p = &buf[size - 1];
    *p = '\0';
    do {
        m = value % 10;
        value = value / 10;
        *--p = '0' + (m < 0 ? -m : m);

        if (!value && m < 0)
            *--p = '-';

        if (value && !(++n % 3))
            *--p = ',';

    } while (value);

    //拷贝内存数据
    char *firstdig = buf;
    do {
        *firstdig++ = *p++;
    } while (*p);
    *firstdig = '\0';

    QStdString dec = buf;
    delete[] buf;
    return dec;
}

QStdString QStdString::trimBeginEnd() const {
    int begin = 0;
    while (begin < this->size() && this->at(begin) == ' ') {
        begin++;
    }
    int end = this->size() - 1;
    while (end >= 0 && this->at(end) == ' ') {
        end--;
    }
    return this->mid(begin, end - begin + 1);
}

QStdString QStdString::formatXml(const QStdString &xml) {
    QStdString formattedXml = xml;
    if (xml.initialized() &&
        !xml.startsWith("<html>") &&
        !xml.contains("404 Not found") &&
        !xml.contains("!DOCTYPE")) {
        QString formatted;
        QXmlStreamReader reader(xml);
        QXmlStreamWriter writer(&formatted);
        writer.setAutoFormatting(true);
        while (!reader.atEnd()) {
            reader.readNext();
            if (!reader.isWhitespace()) {
                writer.writeCurrentToken(reader);
            }
        }
        if (reader.hasError()) {
            formattedXml = R"(<?xml version="1.0" encoding="UTF-8"?>)";
        } else {
            formattedXml = formatted;
        }
    }
    return formattedXml;
}

const char *QStdString::duplicate(const char *p, int size) {
    char *ptr = nullptr;
    if (p) {
        if (size > 0) {
            ptr = new char[size + 1];
            memset((char *) ptr, 0, size + 1);
            memcpy((char *) ptr, p, size);
        }
    }
    if (ptr == nullptr) {
        ptr = new char[1];
        ptr[0] = '\0';
    }
    return ptr;
}

const char *QStdString::duplicate(const char *p) {
    return QStdString::duplicate(p, (int) strlen(p));
}

const char *QStdString::duplicate(const QString &p) {
    int size = 0;
    return QStdString::duplicate(p, size);
}

const char *QStdString::duplicate(const QString &p, int &size) {
    QByteArray raw = p.toLocal8Bit();
    size = raw.size();
    return QStdString::duplicate(raw.constData(), raw.size());
}

QStdString QStdString::empty() {
    return "";
}

QDateTime QStdString::compiledDateTime() {
    QDateTime datetime;
    QStdString date = __DATE__;
    QStdString time = __TIME__;
    QStringList list = date.split(" ");
    if (list.size() == 3 || list.size() == 4) {
        if (list.size() == 4) {
            list[1] = "0" + list[2];
            list[2] = list[3];
        }
        QStdString month = list[0].toLower();
        if (month == "jan") {
            month = "01";
        } else if (month == "feb") {
            month = "02";
        } else if (month == "mar") {
            month = "03";
        } else if (month == "apr") {
            month = "04";
        } else if (month == "may") {
            month = "05";
        } else if (month == "jun") {
            month = "06";
        } else if (month == "jul") {
            month = "07";
        } else if (month == "aug") {
            month = "08";
        } else if (month == "sep") {
            month = "09";
        } else if (month == "oct") {
            month = "10";
        } else if (month == "nov") {
            month = "11";
        } else if (month == "dec") {
            month = "12";
        }
        datetime = QDateTime::fromString(
                list[2] + month + list[1] + time.replace(":", ""),
                "yyyyMMddHHmmss");
    }
    return datetime;
}

QString QStdString::toQStr() const {
    return this->toLocal8Bit();
}

QStdString QStdString::toNumberStr() const {
    QStdString num;
    auto length = this->length();
    for (int i = 0; i < length; i++) {
        if (this->at(i) >= '0' && this->at(i) <= '9') {
            num += this->at(i);
        }
    }
    if (num.isEmpty()) {
        num = 0;
    }
    return num;
}

bool QStdString::toBool() const {
    return this->compare("true", Qt::CaseInsensitive) == 0;
}

const char *QStdString::percent() {
    return "\xFE";
}

QStdString QStdString::millisecondsToHours(int millisecond) {
    auto ss = 1000;
    auto mi = ss * 60;
    auto hh = mi * 60;
    auto dd = hh * 24;

    auto day = millisecond / dd;
    auto hour = (millisecond - day * dd) / hh;
    auto minute = (millisecond - day * dd - hour * hh) / mi;
    auto second = (millisecond - day * dd - hour * hh - minute * mi) / ss;
    auto milliSecond = millisecond - day * dd - hour * hh - minute * mi - second * ss;

    auto hours = QString("%1").arg(hour, 1, 10, QLatin1Char('0'));
    auto minutes = QString("%1").arg(minute, 2, 10, QLatin1Char('0'));;
    auto seconds = QString("%1").arg(second, 2, 10, QLatin1Char('0'));;
    auto milliseconds = QString("%1").arg(milliSecond, 0, 10, QLatin1Char('0'));;

    return hours + ":" + minutes + ":" + seconds + "." + milliseconds;
}

QByteArray QStdString::toAES(const QByteArray &raw) {
    return global_QDebugString::aes.encode(
            raw,
            global_QDebugString::hashKey(),
            global_QDebugString::hashIV());
}

QStdString QStdString::toAES(const QStdString &text) {
    return global_QDebugString::aes.encode(
            text.toLocal8Bit(),
            global_QDebugString::hashKey(),
            global_QDebugString::hashIV()).toBase64();
}

QByteArray QStdString::fromAES(const QByteArray &data) {
    auto decrypt = global_QDebugString::aes.decode(
            data,
            global_QDebugString::hashKey(),
            global_QDebugString::hashIV());
    decrypt = global_QDebugString::aes.removePadding(decrypt);
    return decrypt;
}

QByteArray QStdString::fromAES(const QStdString &base64) {
    QByteArray decoded;
    if (base64.initialized()) {
        QStdString encrypted = base64.mid(1);
        decoded = global_QDebugString::aes.decode(
                QByteArray::fromBase64(encrypted.toLatin1()),
                global_QDebugString::hashKey(),
                global_QDebugString::hashIV());
    }
    return decoded;
}

QStdString QStdString::encrypt(const QStdString &text) {
    SimpleCrypt simpleCrypt(global_QDebugString::simpleCryptKey());
    simpleCrypt.setCompressionMode(SimpleCrypt::CompressionAlways);
    simpleCrypt.setIntegrityProtectionMode(SimpleCrypt::IntegrityProtectionMode::ProtectionHash);
    auto encrypted = simpleCrypt.encryptToString(text);
    DBG(QString("encrypt from: %1, to: %2, with secureKey: 0x%3")
                .arg(text)
                .arg(encrypted)
                .arg(QString::number(global_QDebugString::simpleCryptKey(), 16)));
    return encrypted;
}

QStdString QStdString::decrypt(const QStdString &text, const char *defaultText) {
    QStdString decrypted = defaultText ? defaultText : "";
    if (text.initialized()) {
        SimpleCrypt simpleCrypt(global_QDebugString::simpleCryptKey());
        decrypted = simpleCrypt.decryptToString(text);
        DBG(QString("decrypt from: %1, to: %2, with secureKey: 0x%3")
                    .arg(text.isEmpty() ? "\"\"" : text)
                    .arg(decrypted.isEmpty() ? "\"\"" : decrypted)
                    .arg(QString::number(global_QDebugString::simpleCryptKey(), 16)));
        if (simpleCrypt.lastError() != SimpleCrypt::Error::ErrorNoError) {
            decrypted = defaultText ? defaultText : "";
        }
    }
    return decrypted;
}

QStdString QStdString::machineCodeForEncryptString(int pickLength) {
    return global_QDebugString::machineCode(pickLength);
}

QByteResource QStdString::toByteResource() {
    return QByteResource(*this);
}

QStdString QStdString::hash(const QStdString &code, const QStdString &salt, int pickLength) {
    QStdString md5 = QCryptographicHash::hash((code.toUpper() + salt).toLocal8Bit(), QCryptographicHash::Md5).toHex();
    if (pickLength >= 0) {
        md5 = md5.mid(0, pickLength) + md5.mid(md5.length() - pickLength, pickLength);
    }
    md5 = md5.toUpper();
    return md5;
}