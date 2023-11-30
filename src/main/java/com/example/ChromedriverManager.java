package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChromedriverManager {
//    public static void main(String[] args) {
//        try {
//            int maxInstances = 5; // Maximum number of allowed chromedriver instances
//            int currentInstances = countChromedriverInstances();
//
//            if (currentInstances > maxInstances) {
//                killExcessChromedriverProcesses(currentInstances - maxInstances);
//            }
//        } catch (IOException e) {
//            // Handle exception
//            e.printStackTrace();
//        }
//    }

    public static int countChromedriverInstances() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("tasklist", "/FI", "IMAGENAME eq chromedriver.exe");
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        int count = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("chromedriver.exe")) {
                count++;
            }
        }

        return count;
    }

    public static void killExcessChromedriverProcesses(int numToKill) throws IOException {
        ProcessBuilder processBuilder;
        for (int i = 0; i < numToKill; i++) {
            processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", "chromedriver.exe");
            processBuilder.start();
        }
    }
}
