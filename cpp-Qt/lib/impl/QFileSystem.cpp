//
// Created by 黄元镭 on 2020/9/9.
//

#include <QFileSystem.h>
#include <QFile>
#include <QLogger.h>

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"
#ifdef WINDOWS
#include <winioctl.h>
#endif

QByteArray QFileSystem::readAllBytes(const QStdString &filePath) {
    QByteArray data;
    QFile file(filePath);
    if (file.exists()) {
        if (file.open(QIODevice::ReadOnly)) {
            data = file.readAll();
            file.close();
        }
    }
    return data;
}

bool QFileSystem::writeAllBytes(const QStdString &filePath, const QByteArray &data) {
    bool b = false;
    QFile file(filePath);
    if (file.open(file.exists() ? QIODevice::Truncate : QIODevice::NewOnly)) {
        b = file.write(data) == data.size();
        file.close();
    }
    return b;
}

#ifdef WINDOWS
std::vector<int64_t> QFileSystem::readClusters(const char *fileName) {
    std::vector<int64_t> clusters;
    if (fileName && QFile::exists(fileName)) {
        std::string driver = fileName;
        driver.resize(2);
        DWORD sectorsPerCluster = 0;
        DWORD bytesPerSector = 0;
        if (GetDiskFreeSpace(driver.c_str(), &sectorsPerCluster, &bytesPerSector, nullptr, nullptr)) {
            HANDLE hFile = CreateFile(fileName,
                                      FILE_READ_ATTRIBUTES,
                                      FILE_SHARE_READ | FILE_SHARE_WRITE | FILE_SHARE_DELETE,
                                      nullptr,
                                      OPEN_EXISTING,
                                      0,
                                      nullptr);
            if (hFile != INVALID_HANDLE_VALUE) {
                auto fileSize = GetFileSize(hFile, nullptr);
                auto clusterSize = sectorsPerCluster * bytesPerSector;
                auto clusterCount = DWORD(ceil(fileSize * 1.0 / bytesPerSector / sectorsPerCluster));
                STARTING_VCN_INPUT_BUFFER in = {0};
                in.StartingVcn.QuadPart = 0;
                PRETRIEVAL_POINTERS_BUFFER out = nullptr;
                DWORD outSize = sizeof(RETRIEVAL_POINTERS_BUFFER) + (fileSize / clusterSize) * sizeof(out->Extents);
                out = (PRETRIEVAL_POINTERS_BUFFER) malloc(outSize);
                DWORD bytesReturned = 0;
                auto result = DeviceIoControl(hFile,
                                              FSCTL_GET_RETRIEVAL_POINTERS,
                                              &in,
                                              sizeof(in),
                                              out,
                                              outSize,
                                              &bytesReturned,
                                              nullptr);
                if (result) {
                    LARGE_INTEGER vcn = out->StartingVcn;
                    LARGE_INTEGER lcn;
                    for (auto i = (DWORD) 0; i < out->ExtentCount; i++) {
                        lcn = out->Extents[i].Lcn;
                        for (auto j = (DWORD) (out->Extents[i].NextVcn.QuadPart - vcn.QuadPart); j; j--, lcn.QuadPart++) {
                            clusters.push_back(lcn.QuadPart);
                        }
                        vcn = out->Extents[i].NextVcn;
                    }
                    CloseHandle(hFile);
                    hFile = INVALID_HANDLE_VALUE;
                } else {
                    DBG(QString("File::readClusters Error = %1").arg(GetLastError()));
                }
            }
        }
    }
    if (clusters.empty()) {
        clusters.push_back(CLUSTER_UNKNOWN);
    }
    return clusters;
}
#endif
#pragma clang diagnostic pop