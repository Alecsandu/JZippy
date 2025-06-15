import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class App {

    static void run(String[] args) {
        new App().runApp(args);
    }

    private void writeToFile(Path filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {
            writer.append("\nWe are appending... What is up");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void readFileContent(Path filePath) {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String content = reader.readLine();
            while (content != null) {
                System.out.println(content);
                content = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void runApp(String[] args) {
        String fileName = "test.txt";
        String zipFileName = "test.zip";
        boolean doAdditionalProcessing = false;

        if (args.length == 2) {
            fileName = args[0];
            zipFileName = args[1];
        } else if (args.length == 3) {
            fileName = args[0];
            zipFileName = args[1];
            doAdditionalProcessing = Boolean.getBoolean(args[2]);
        }

        Path filePath = Paths.get(fileName);
        if (doAdditionalProcessing) {
            writeToFile(filePath);
            readFileContent(filePath);
        }

        File zipFile = new File(zipFileName);
        if (zipFile.exists()) {
            boolean isDeleted = zipFile.delete();
            if (isDeleted) {
                System.out.println("File was deleted");
            } else {
                System.out.println("Couldn't delete the file");
            }
        } else {
            try {
                boolean isCreated = zipFile.createNewFile();
                if (isCreated) {
                    System.out.println("Created new file");
                } else {
                    System.out.println("File already exists");
                }
            } catch (IOException e) {
                System.out.println("Couldn't create new file because : [" + e.getMessage() + "]");
            }
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);

            List<String> fileContent = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                fileContent.add(line);
                line = reader.readLine();
            }

            fileContent.forEach(lineData -> {
                try {
                    lineData = lineData + '\n';
                    zipOutputStream.write(lineData.getBytes(), 0, lineData.getBytes().length);
                } catch (IOException e) {
                    System.out.println("Failed to get bytes because : " + e.getMessage());
                }
            });

            zipOutputStream.closeEntry();
            zipOutputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            zipFile.deleteOnExit();
        } catch (IOException e) {
            System.out.println("Failed due to : " + e.getMessage());
            zipFile.deleteOnExit();
        }
    }
}
