//
// Created by huang on 11/11/2020.
//

#ifndef QDebugger_H
#define QDebugger_H

#define DBG_BREAK __asm__("int $3\n" : : )
#define DBG_BREAK_ON_CTRL if (QApplication::queryKeyboardModifiers().testFlag(Qt::ControlModifier)) { DBG_BREAK; }

#endif //QDebugger_H
