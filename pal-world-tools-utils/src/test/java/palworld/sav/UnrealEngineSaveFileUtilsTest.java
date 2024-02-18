package palworld.sav;

import kr.nerdlab.palworld.entity.GvasObject;
import kr.nerdlab.palworld.sav.UnrealEngineSaveFileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class UnrealEngineSaveFileUtilsTest {

	@Test
	public void testDecompressSavToGvas() throws Exception {
		try (Stream<Path> pathStream = Files.walk(Paths.get(ClassLoader.getSystemResource("uesaveTest").toURI()))) {
			pathStream
					.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().endsWith(".sav"))
					.forEach(savFile -> {
						try {
							GvasObject gvasObject = UnrealEngineSaveFileUtils.decompressSavToGvas(savFile);
							Assertions.assertNotNull(gvasObject);
						} catch (Exception e) {
							Assertions.fail("Failed to decompress " + savFile, e);
						}
					});
		}
	}
}

