package kr.nerdlab.palworld.entity;

import kr.nerdlab.palworld.sav.UnrealEngineSaveFileUtils;

public record GvasObject(byte[] uncompressedData, UnrealEngineSaveFileUtils.SaveType saveType, byte[] magicBytes) {}
