package com.bdcc.BigDataCloudComputingPart2.a5;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/a5")
public class SearchController {

    @Autowired
    private MongoTemplate mongoTemplate;

//    @Value("${search.folder}")
//    private String searchFolder;

//    @Value("${search.folder1}")
//    private String searchFolder1;




    String searchFolder = System.getProperty("user.dir") + "/data";

    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "search-form";
    }

    @GetMapping("/teja/home")
    public String searchForm1(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "home1";
    }


    @PostMapping("/search")
    public String searchWord(@ModelAttribute SearchForm searchForm, Model model) throws IOException {
        String word = searchForm.getWord().trim().toLowerCase();
        List<SearchResult> searchResults = new ArrayList<>();
        String collectionName = "files";

        mongoTemplate.dropCollection(collectionName);

        List<String> filenames = new ArrayList<>();
        filenames.add("data/pg18929.txt");
        filenames.add("data/pg70430.txt");
        filenames.add("data/pg70431.txt");
        filenames.add("data/pg70432.txt");
        filenames.add("data/pg70433.txt");
        filenames.add("data/pg70434.txt");
        filenames.add("data/pg70435.txt");
        filenames.add("data/pg70436.txt");
        filenames.add("data/pg70438.txt");
        filenames.add("data/pg70439.txt");
        for(String file : filenames) {
            String fileName = file.substring(file.lastIndexOf("/") + 1);
            InputStream is = getFileAsIOStream(file);
            String data = new String(IOUtils.toByteArray(is), StandardCharsets.UTF_8);
            Document doc = new Document();
            doc.put("filename", fileName);
            doc.put("text", data);
            mongoTemplate.insert(doc, collectionName);
        }

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("text").regex("(?i)" + word));
        List<Document> documents1 = mongoTemplate.find(query1, Document.class, collectionName);
        for (Document document : documents1) {
            String fileName = document.getString("filename");
            String text = document.getString("text");
            int lineNumber = 1;
            try (Scanner scanner = new Scanner(text)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.toLowerCase().contains(word)) {
                        searchResults.add(new SearchResult(fileName, lineNumber, line));
                    }
                    lineNumber++;
                }
            }
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("text").regex("(?i)" + word));
        List<Document> documents = mongoTemplate.find(query, Document.class, collectionName);
        Map<String, Integer> wordCountMap = new HashMap<>();
        for (Document document : documents) {
            String text = document.getString("text");
            try (Scanner scanner = new Scanner(text)) {
                while (scanner.hasNext()) {
                    String token = scanner.next().toLowerCase();
                    if (token.matches("[a-z]+")) {
                        int count = wordCountMap.getOrDefault(token, 0);
                        wordCountMap.put(token, count + 1);
                    }
                }
            }
        }

        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        if (maxEntry != null) {
            model.addAttribute("maxWord", maxEntry.getKey());
            model.addAttribute("maxCount", maxEntry.getValue());
        }


        Map.Entry<String, Integer> minEntry = null;
        for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
            if (minEntry == null || entry.getValue() < minEntry.getValue()) {
                minEntry = entry;
            }
        }
        if (minEntry != null) {
            model.addAttribute("minWord", minEntry.getKey());
            model.addAttribute("minCount", minEntry.getValue());
        }

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("searchWord", word);
        model.addAttribute("info1",  word + ":  is found : " + searchResults.size() + " times");
        return "search-results";
    }


    @PostMapping("/searchData")
    public String searchData(@ModelAttribute SearchForm searchForm, Model model) {

        String data = searchForm.getFileData();
        int wordCount = getWordCount(data.toLowerCase());
        int charCount = getCharCount(data.toLowerCase());
        int spaceCount = getSpaceCount(data.toLowerCase());
        model.addAttribute("wc",  wordCount);
        model.addAttribute("wordCount", "Word Count: " + wordCount);
        model.addAttribute("charCount", "Character Count: " + charCount);
        model.addAttribute("spaceCount", "Space Count: " + spaceCount);
        if(searchForm.getFileSearchChar() != null) {
            int wordOccurrences = getWordOccurrences(data.toLowerCase(), searchForm.getFileSearchChar().toLowerCase());
            model.addAttribute("wordOccurrenceCount", "Word Occurrence Count: " + wordOccurrences + " times. " + " Fraction: " + wordOccurrences + "/" + wordCount );
        }
        if(searchForm.getEachCharOccurrence() != null) {
            Map<Character, Integer> charCountMap = getEachCharCount(data.toLowerCase(), searchForm.getEachCharOccurrence().toLowerCase());
            model.addAttribute("charCountMap", charCountMap);
        }

        if(searchForm.getTopNWords()!=null) {
            Map<String, Integer> topNOccurrenceMap = getTopNWords(data.toLowerCase(), searchForm.getTopNWords());
            model.addAttribute("topNOccurrenceMap", topNOccurrenceMap);
        }

        if(searchForm.getLeastNWords()!=null) {
            Map<String, Integer> bottomNOccurrenceMap = getBottomNWords(data, searchForm.getLeastNWords());
            model.addAttribute("bottomNOccurrenceMap", bottomNOccurrenceMap);
        }

        if(searchForm.getFirstNChars()!=null) {
           int N = searchForm.getFirstNChars();

            String cleanData = data.replaceAll("\\p{Punct}|[^\t\n\r -~]", "");
            String lowerData = cleanData.toLowerCase();

            model.addAttribute("firstNChar", lowerData.substring(0, N));
            // Show the first N characters
//            System.out.println(lowerData.substring(0, N));
        }

       if(searchForm.getMultipleWords()!=null){
           Map<String, Integer> multipleWordsCount = getEachWordCount( data,  searchForm.getMultipleWords());
           model.addAttribute("multipleWordsCount", multipleWordsCount);
       }
       if(searchForm.getMin()!=null && searchForm.getMax()!=null){
          List<String> minMax = getWordsByLength(data, searchForm.getMin(), searchForm.getMax());
          model.addAttribute("minMax", minMax);

       }
       if(searchForm.getWord1()!=null && searchForm.getWord2()!=null){
           List<String> word1Word2 = countAdjacentWords(data, searchForm.getWord1(), searchForm.getWord2());
           model.addAttribute("word1Word2", word1Word2);
       }

        return "search-data-result";

    }


    public static List<String> countAdjacentWords(String data, String word1, String word2) {
        List<String> result = new ArrayList<>();
        if (data == null || data.isEmpty() || word1 == null || word1.isEmpty() || word2 == null || word2.isEmpty()) {
            return result;
        }
        String[] words = data.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase().split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].equals(word1) && words[i + 1].equals(word2)) {
                int start = Math.max(i - 5, 0);
                int end = Math.min(i + 6, words.length+1);
                StringBuilder sb = new StringBuilder();
                for (int j = start; j <= end; j++) {
                    sb.append(words[j]).append(" ");
                }
                result.add(sb.toString().trim());
            }
        }
        return result;
    }



    public static List<String> getWordsByLength(String data, int min, int max) {
        List<String> words = Arrays.asList(data.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().split("\\s+"));
        List<String> wordsByLength = new ArrayList<>(words);
        wordsByLength.removeIf(word -> word.length() < min || word.length() > max);
        List<String> output = new ArrayList<>();
        for (int i = max; i >= min; i--) {
            for (String word : wordsByLength) {
                if (word.length() == i) {
                    output.add(word);

                }
            }
        }

        return output;
    }


    public static int getWordCount(String data) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        String[] words = data.split("\\s+");
        return words.length;
    }

   /* public static int getCharCount(String data) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) != ' ') {
                count++;
            }
        }
        return count;
    }*/
   public static int getCharCount(String data) {
       if (data == null || data.isEmpty()) {
           return 0;
       }
       return data.length();
   }
    public static int getSpaceCount(String data) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == ' ') {
                count++;
            }
        }
        return count;
    }
    public static int getWordOccurrences(String data, String word) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        String[] words = data.split("\\s+");
        int count = 0;
        for (String w : words) {
            if (w.equals(word)) {
                count++;
            }
        }
        return count;
    }

    public static int getCharOccurrences(String data, char c) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
    public static Map<String, Integer> getEachWordCount(String data, String searchword) {
        Map<String, Integer> wordCountMap = new HashMap<>();
        if (data == null || data.isEmpty() || searchword == null || searchword.isEmpty()) {
            return wordCountMap;
        }
        String[] searchWords = searchword.split("\\s+");
        for (String word : searchWords) {
            wordCountMap.put(word, 0);
        }
        String[] words = data.split("\\s+");
        for (String word : words) {
            if (wordCountMap.containsKey(word)) {
                int count = wordCountMap.get(word);
                wordCountMap.put(word, count + 1);
            }
        }
        return wordCountMap;
    }


    public static Map<Character, Integer> getEachCharCount(String data, String searchword) {
        Map<Character, Integer> charCountMap = new HashMap<>();
        if (data == null || data.isEmpty() || searchword == null || searchword.isEmpty()) {
            return charCountMap;
        }
        for (int i = 0; i < searchword.length(); i++) {
            char c = searchword.charAt(i);
            charCountMap.put(c, 0);
        }
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            if (charCountMap.containsKey(c)) {
                int count = charCountMap.get(c);
                charCountMap.put(c, count + 1);
            }
        }
        return charCountMap;
    }

    public static Map<String, Integer> getTopNWords(String data, int n) {
        if (data == null || data.isEmpty() || n <= 0) {
            return Collections.emptyMap();
        }

        String[] words = data.toLowerCase().split("\\W+");

        Map<String, Integer> wordCountMap = new HashMap<>();

        for (String word : words) {
            if (!word.isEmpty()) {
                wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> wordCountList = new ArrayList<>(wordCountMap.entrySet());
        wordCountList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());

        Map<String, Integer> topNWords = new LinkedHashMap<>();

        for (int i = 0; i < n && i < wordCountList.size(); i++) {
            Map.Entry<String, Integer> entry = wordCountList.get(i);
            topNWords.put(entry.getKey(), entry.getValue());
        }

        return topNWords;
    }

    public static Map<String, Integer> getBottomNWords(String data, int n) {
        if (data == null || data.isEmpty() || n <= 0) {
            return Collections.emptyMap();
        }

        String[] words = data.toLowerCase().split("\\W+");

        Map<String, Integer> wordCountMap = new HashMap<>();

        for (String word : words) {
            if (!word.isEmpty()) {
                wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> wordCountList = new ArrayList<>(wordCountMap.entrySet());
        wordCountList.sort(Map.Entry.<String, Integer>comparingByValue());

        Map<String, Integer> bottomNWords = new LinkedHashMap<>();

        for (int i = 0; i < n && i < wordCountList.size(); i++) {
            Map.Entry<String, Integer> entry = wordCountList.get(i);
            bottomNWords.put(entry.getKey(), entry.getValue());
        }

        return bottomNWords;
    }
    @Autowired
    private ResourceLoader resourceLoader;



    @PostMapping("/search1")
    public String searchWord1(@ModelAttribute SearchForm searchForm,
                              Model model) throws IOException {
        String word = searchForm.getWord().trim().toLowerCase();
        List<SearchResult> searchResults = new ArrayList<>();
        String collectionName = "files1";

        mongoTemplate.dropCollection(collectionName);
        List<String> filenames = new ArrayList<>();
        filenames.add("cloudFiles/58068-0.txt");
        filenames.add("cloudFiles/61163-0.txt");
        filenames.add("cloudFiles/65255-0.txt");
        filenames.add("cloudFiles/pg9844.txt");
        filenames.add("cloudFiles/pg23728.txt");
        filenames.add("cloudFiles/pg31563.txt");
        filenames.add("cloudFiles/sampletext1.txt");
        filenames.add("cloudFiles/sampletext2.txt");
        for(String file : filenames) {
            String fileName = file.substring(file.lastIndexOf("/") + 1);
            InputStream is = getFileAsIOStream(file);
            String data = new String(IOUtils.toByteArray(is), StandardCharsets.UTF_8);
            Document doc = new Document();
            doc.put("filename", fileName);
            doc.put("text", data);
            mongoTemplate.insert(doc, collectionName);
        }

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("text").regex("(?i)" + word));
        List<Document> documents1 = mongoTemplate.find(query1, Document.class, collectionName);
        for (Document document : documents1) {
            String fileName = document.getString("filename");
            String text = document.getString("text");
            int lineNumber = 1;
            try (Scanner scanner = new Scanner(text)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.toLowerCase().contains(word)) {
                        searchResults.add(new SearchResult(fileName, lineNumber, line));
                    }
                    lineNumber++;
                }
            }
        }

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("searchWord", word);
        model.addAttribute("info1",  word + ":  is found : " + searchResults.size() + " times");
        return "result1";
    }


    private InputStream getFileAsIOStream(final String fileName)
    {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }
}
