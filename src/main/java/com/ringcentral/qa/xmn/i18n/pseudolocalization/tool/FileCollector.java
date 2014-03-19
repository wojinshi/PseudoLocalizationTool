/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ringcentral.qa.xmn.i18n.pseudolocalization.tool;

import com.ringcentral.qa.xmn.i18n.pseudolocalization.exception.CollectFileException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author julia.lin
 */
public class FileCollector {

    private String rootPath;
    private String outputFile;
    private Pattern filePattern;

    public FileCollector(String path, String fileRegex, String outputFile) {
        this.rootPath = path;
        this.outputFile = outputFile;
        this.filePattern = Pattern.compile(fileRegex);
    }

    public void collect() {
        List<String> fileNames = new ArrayList<String>();
        collect(rootPath, fileNames);
        try {
            writeToOutputFile(fileNames);
        } catch (IOException ex) {
            throw new CollectFileException("Fail to collect files under path: " + rootPath, ex);
        }
    }

    private void collect(String rootPath, List<String> fileNames) {
        File dir = new File(rootPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                if (!file.isHidden()) {
                    collect(file.getAbsolutePath(), fileNames);
                }
            } else {
                if (filePattern.matcher(file.getAbsolutePath()).find()) {
                    String strFileName = file.getAbsolutePath();
                    fileNames.add(strFileName);
                }
            }
        }
    }

    public void writeToOutputFile(List<String> fileNames) throws IOException {
        if (fileNames != null) {
            BufferedWriter out = null;
            try {
                File output = new File(outputFile);
                if (!output.exists()) {
                    output.createNewFile();
                }
                out = new BufferedWriter(new FileWriter(outputFile));
                for (String path : fileNames) {
                    out.write(path);
                    out.newLine();
                }
                out.flush();                
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }
    }
}
