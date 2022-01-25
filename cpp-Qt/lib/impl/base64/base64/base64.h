#ifndef BASE64_H
#define BASE64_H

#include <string>
#include <QStdString.h>
using namespace std;

#define BASE64_ENCODE_OUT_SIZE(s) ((unsigned int)((((s) + 2) / 3) * 4 + 1))
#define BASE64_DECODE_OUT_SIZE(s) ((unsigned int)(((s) / 4) * 3))

/*
 * out is null-terminated encode string.
 * return values is out length, exclusive terminating `\0'
 */
unsigned int
base64_encode(const unsigned char *in, unsigned int inlen, char *out);

/*
 * return values is out length
 */
unsigned int
base64_decode(const char *in, unsigned int inlen, unsigned char *out);

namespace base64 {
    inline QStdString encode(const QStdString &src) {
        string encoded;
        auto *buffer = (char *) malloc(BASE64_ENCODE_OUT_SIZE(src.size()));
        if (buffer) {
            string s = src.toStdString();
#ifdef WIN32
            base64_encode((unsigned char *) s.c_str(), (unsigned int) s.length(), buffer);
#else
            base64_encode((unsigned char *) s.c_str(), s.length(), buffer);
#endif
            encoded = buffer;
            free(buffer);
        }
        return encoded;
    }

    inline QStdString decode(const char *src) {
        QStdString decoded;
#ifdef WIN32
        size_t length = strlen(src);
#else
        int length = strlen(src);
#endif
        auto *buffer = (unsigned char *) malloc(BASE64_DECODE_OUT_SIZE(length));
        if (buffer) {
#ifdef WIN32
            auto size = base64_decode(src, (int) length, buffer);
            for (size_t i = 0; i < size; i++) {
#else
            auto size = base64_decode(src, length, buffer);
            for (int i = 0; i < size; i++) {
#endif
                decoded += buffer[i];
            }
            free(buffer);
        }
        return decoded;
    }
}

#endif /* BASE64_H */
