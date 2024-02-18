package kr.nerdlab.palworld.sav;

import jakarta.annotation.Nonnull;
import kr.nerdlab.palworld.entity.GvasObject;
import kr.nerdlab.palworld.entity.SaveType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;


/**
 * Utility class for handling Unreal Engine save files.
 * <p>
 * This class offers methods for compressing and decompressing Unreal Engine save files,
 * facilitating the management of game save data. The structure of a *.sav file is outlined below:
 * <pre>
 *         [0-3]    : int - Length of the uncompressed data
 *         [4-7]    : int - Length of the compressed data
 *         [8-12]   : int - Magic number for file validation
 *         ㄴ [8-11] : byte[] - Magic bytes for identifying file format
 *         ㄴ [12]   : byte  - Save type indicator (0x31 for single, 0x32 for double zlib compression)
 *         [13-*]   : byte* - Compressed data block
 * </pre>
 */

public class UnrealEngineSaveFileUtils {
	/**
	 * Decompresses a *.sav file to a GvasObject.
	 *
	 * @param savFilePath the path to the *.sav file
	 * @return a GvasObject containing the uncompressed data, save type, and magic bytes
	 * @throws IllegalStateException if the file cannot be decompressed
	 * @throws IOException           if an I/O error occurs
	 */
	public static GvasObject decompressSavToGvas(@Nonnull Path savFilePath) throws IllegalStateException, IOException {
		byte[] data = Files.readAllBytes(savFilePath);
		try {
			return decompressSavToGvas(data);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to decompress " + savFilePath, e);
		}
	}

	/**
	 * Save type 0x31 denotes a file with single zlib compression,
	 * while save type 0x32 indicates double zlib compression for added security and reduced file size.
	 * <p>
	 * The decompress method converts compressed data back into its original uncompressed form.
	 * The length of the decompressed data should match the uncompressed length specified in the file header.
	 * For save type 0x32, the length after the first decompression will match the compressed length,
	 * requiring a second decompression step to retrieve the original data.
	 *
	 * @param data the byte array of the *.sav file
	 * @return a GvasObject containing the uncompressed data, save type, and magic bytes
	 * @throws IllegalArgumentException if the file is not a valid *.sav file
	 * @throws IllegalStateException    if the file cannot be decompressed
	 */
	private static GvasObject decompressSavToGvas(byte[] data) throws IllegalArgumentException, IllegalStateException, DataFormatException {
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		int uncompressedLen = buffer.getInt();
		int compressedLen = buffer.getInt();
		int magicInt = buffer.getInt();

		SaveType saveType = Optional.ofNullable(SaveType.fromValue((byte) (magicInt >> 24))).orElseThrow(() -> new IllegalStateException("Unknown save type byte: " + (byte) (magicInt >> 24)));

		byte[] compressedData = Arrays.copyOfRange(data, 12, data.length);
		byte[] uncompressedData = decompress(compressedData);

		if (SaveType.DOUBLE_ZLIB == saveType) {
			if (compressedLen != uncompressedData.length) {
				throw new IllegalArgumentException("Incorrect uncompressed length: " + compressedLen + " != " + uncompressedData.length);
			}
			uncompressedData = decompress(uncompressedData);
		}

		if (uncompressedLen != uncompressedData.length) {
			throw new IllegalStateException("Incorrect uncompressed length: " + uncompressedLen + " != " + uncompressedData.length);
		}

		return new GvasObject(uncompressedData, saveType, magicBytes(magicInt));
	}

	private static byte[] magicBytes(int magic) {
		return new byte[]{(byte) (magic >> 16), (byte) (magic >> 8), (byte) magic};
	}

	private static byte[] decompress(byte[] data) throws DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);

		byte[] buf = new byte[1024];
		int bytesRead;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		while (!inflater.finished()) {
			bytesRead = inflater.inflate(buf);
			outputStream.write(buf, 0, bytesRead);
		}
		inflater.end();

		return outputStream.toByteArray();
	}
}

