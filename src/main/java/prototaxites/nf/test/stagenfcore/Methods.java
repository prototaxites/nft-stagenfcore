package prototaxites.nf.test.stagenfcore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

/*
 * Add your custom methods to nf-test
 *
 * @author: prototaxites
 */

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
                    "bash", "-c", "cd " + libDir + " && nf-core modules install " + module
                );
                Process process = processBuilder.start();
                int exitCode = process.waitFor();

                if (exitCode != 0) {
                    System.err.println("Error installing module " + module + ": exit code " + exitCode);
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
        System.out.println("\n\n");
    }

    /**
     * Creates a symbolic link from the installed nf-core modules to the base directory
     * @param libDir An nf-core library initialised by nfcoreSetup()
     * @param modulesDir The root directory of an nf-core style modules repository
     */
    public static void nfcoreLink(String libDir, String modulesDir) {

        try {
            File sourceDir = new File(libDir + "/modules/nf-core");
            File targetDir = new File(modulesDir + "/nf-core");

            Files.createSymbolicLink(
                targetDir.toPath(),
                sourceDir.toPath()
            );
            System.out.println("Linking temporary nf-core library: " + targetDir + " -> " + sourceDir);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Error: Symlink already exists: " + modulesDir + "/nf-core");
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
            File modulesLibDir = new File(libDir + "/modules");
            deleteDirectory(modulesLibDir);

            // Delete .nf-core.yml file
            File nfcoreYml = new File(libDir + "/.nf-core.yml");
            nfcoreYml.delete();

            // Delete modules.json file
            File modulesJson = new File(libDir + "/modules.json");
            modulesJson.delete();
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Cleanup function to remove created directories and files
     * @param modulesDir The base directory path
     */
    public static void nfcoreUnlink(String modulesDir) {
        System.out.println("Unlinking temporary nf-core library: " + modulesDir + "/nf-core");

        try {
            File nfcoreLink = new File(modulesDir + "/nf-core");
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
