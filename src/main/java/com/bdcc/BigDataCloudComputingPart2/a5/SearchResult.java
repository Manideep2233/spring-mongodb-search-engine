package com.bdcc.BigDataCloudComputingPart2.a5;

public class SearchResult {
    private String fileName;
    private int lineNumber;
    private String line;

    public SearchResult(String fileName, int lineNumber, String line) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.line = line;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLine() {
        return line;
    }
}
