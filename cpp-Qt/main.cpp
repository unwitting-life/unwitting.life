#include <main.h>
#include <QApplication>
#include <qrc.h>

int main(int argc, char *argv[]) {
    QApplication a(argc, argv);
    qrc::printQrcFileList();
    if (main_initialized(&a)) {
        return QApplication::exec();
    }
    return MAIN_INITIALIZE_FATAL;
}
