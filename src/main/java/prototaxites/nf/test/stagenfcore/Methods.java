package prototaxites.nf.test.stagenfcore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

public class Methods {

    /**
     * Creates the modules directory and .nf-core.yml configuration file
     * @param libDir The directory path to initialise an nf-core library at
     */
    public static void nfcoreSetup(String libDir) {
        System.out.println("\n");
        System.out.println("Creating a temporary nf-core library at " + libDir);
        try {
            // Create modules directory
            File modulesDir = new File(libDir + "/modules");
            modulesDir.mkdirs();

            // Create .nf-core.yml file
            File nfcoreYml = new File(libDir + "/.nf-core.yml");
            try (FileWriter writer = new FileWriter(nfcoreYml)) {
                writer.write("repository_type: \"pipeline\"\n");
                writer.write("template:\n");
                writer.write("    name: test\n");
            }
        } catch (IOException e) {
            System.err.println("Error setting up nf-core: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Installs nf-core modules from a list
     * @param libDir An nf-core library initialised by nfcoreSetup()
     * @param modules List of module names to install (e.g., ["minimap2/index", "samtools/view"])
     */
    public static void nfcoreInstall(String libDir, List<String> modules) {
        System.out.println("Installing nf-core modules...");
        for (String module : modules) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(
                    "bash", "-c", "cd " + libDir + " && nf-core --verbose modules install " + module
                );
                Process process = processBuilder.start();

                // Capture stderr from nf-core tools
                BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                StringBuilder stderr = new StringBuilder();

                String line;
                while ((line = stderrReader.readLine()) != null) {
                    stderr.append(line).append("\n");
                }

                int exitCode = process.waitFor();

                // Spit out nf-core tools stderr if install fails
                if (exitCode != 0) {
                    System.err.println("Error installing module " + module + ": exit code " + exitCode + "\n");
                    System.err.println("nf-core tools output: \n");
                    System.err.println(stderr.toString());
                } else {
                    System.out.println("Successfully installed module: " + module);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Error installing module " + module + ": " + e.getMessage());
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Creates a symbolic link from the installed nf-core modules to the base directory
     * @param libDir An nf-core library initialised by nfcoreSetup()
     * @param destination Location to make the library available at
     */
    public static void nfcoreLink(String libDir, String destination) {

        try {
            File sourceDir = new File(libDir + "/modules/nf-core");
            File destination_path = new File(destination);

            Files.createSymbolicLink(
                destination_path.toPath(),
                sourceDir.toPath()
            );
            System.out.println("Linking temporary nf-core library: " + destination + " -> " + sourceDir);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Error: Symlink already exists: " + destination);
        } catch (IOException e) {
            System.err.println("Error creating symlink: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete the temorary nf-core library
     * @param libDir The launch directory path
     * @param modulesDir The base directory path
     */
    public static void nfcoreDeleteLibrary(String libDir) {
        System.out.println("Deleting temporary nf-core library: " + libDir);

        try {
            // Delete modules directory
            File modulesLibDir = new File(libDir);
            deleteDirectory(modulesLibDir);
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Cleanup function to remove created directories and files
     * @param destination Symlinked location
     */
    public static void nfcoreUnlink(String destination) {
        System.out.println("Unlinking temporary nf-core library: " + destination);

        try {
            File nfcoreLink = new File(destination);
            nfcoreLink.delete();
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Helper method to recursively delete a directory
     * @param directory The directory to delete
     */
    private static void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

}
