package com.bdcc.BigDataCloudComputingPart2.a5;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SearchForm {
    private String word;

   private String fileData;
   private String fileSearchChar;
   private Character specialChar;
   private String eachCharOccurrence;
   private Integer topNWords;
   private Integer leastNWords;
   private MultipartFile[] files;

   private Integer firstNChars;

   private String multipleWords;
   private Integer min;
   private Integer max;
   private String word1;
   private String word2;
   private String textInput;
   private String searchInput;
   private String result;
}
