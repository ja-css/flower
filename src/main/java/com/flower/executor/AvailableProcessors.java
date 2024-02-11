package com.flower.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class AvailableProcessors {
    public static int availableProcessors() {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<Integer>) () -> (determineProcessors()));
        }

        return determineProcessors();
    }

    public static boolean isLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nux");
    }

    private static final String CPUS_ALLOWED = "Cpus_allowed:";
    private static final byte[] BITS = new byte[]{0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4};

    protected static int readCPUMask(File file) throws IOException {
        if (file == null  || ! file.exists()) {
            return -1;
        }

        try (final FileInputStream stream = new FileInputStream(file);
             final InputStreamReader inputReader = new InputStreamReader(stream, StandardCharsets.US_ASCII);
             final BufferedReader reader = new BufferedReader(inputReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(CPUS_ALLOWED)) {
                    int count = 0;
                    int start = CPUS_ALLOWED.length();
                    for (int i = start; i < line.length(); i++) {
                        char ch = line.charAt(i);
                        if (ch >= '0' && ch <= '9') {
                            count += BITS[ch - '0'];
                        } else if (ch >= 'a' && ch <= 'f') {
                            count += BITS[ch - 'a' + 10];
                        } else if (ch >= 'A' && ch <= 'F') {
                            count += BITS[ch - 'A' + 10];
                        }
                    }
                    return count;
                }
            }
        }

        return -1;
    }

    private static int determineProcessors() {
        int fromJava = Runtime.getRuntime().availableProcessors();
        int fromProcFile = 0;

        if (!isLinux()) {
            return fromJava;
        }

        try {
            fromProcFile = readCPUMask(new File("/proc/self/status"));
        } catch (Exception e) {
            // We can't do much at this point, we are on linux but using a different /proc format.
        }

        return fromProcFile > 0 ? Math.min(fromJava, fromProcFile) : fromJava;
    }
}
