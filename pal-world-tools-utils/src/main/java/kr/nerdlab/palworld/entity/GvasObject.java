package kr.nerdlab.palworld.entity;

public record GvasObject(byte[] uncompressedData, SaveType saveType, byte[] magicBytes) {}
