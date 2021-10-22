package com.kmschr.brs.enums;

public enum Version {
    Initial,
    MaterialsAsStoredAsNames,
    AddedOwnerData,
    AddedDateTime, // Alpha 4 Patch 1
    AddedComponentsData,
    AddedScreenshotsData,
    AddedGameVersionAndHostAndOwnerDataAndImprovedMaterials,
    RenamedComponentDescriptors,// Alpha 5 (QA)
    V8,
    V9,
    V10,
    V11;

    private static final Version[] versions = Version.values();

    public static Version getVersion(short v) {
        return versions[(v - 1) & 0xFF];
    }
}
