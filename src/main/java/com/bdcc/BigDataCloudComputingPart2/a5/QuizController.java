package com.bdcc.BigDataCloudComputingPart2.a5;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/q5")
public class QuizController {

    @GetMapping("/mani")
    public String searchForm(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "home2";
    }
    @GetMapping("/teja")
    public String searchForm1(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "home3";
    }

    @PostMapping("/searchOne")
    public String searchWord(@ModelAttribute SearchForm searchForm, Model model) throws IOException {
//        String data = searchForm.getTextInput().trim().toLowerCase();
        if(searchForm.getSearchInput()!=null && searchForm.getSearchInput().trim().length()>0) {
            String text = searchForm.getTextInput().replaceAll("[^a-zA-Z\\s]", "").toUpperCase();
            String[] words = text.split("\\s+");
            String[] searchWords = searchForm.getSearchInput().split("\\s+");

            Map<String, Integer> wordCount = new HashMap<>();
            for (String searchWord : searchWords) {
                wordCount.put(searchWord.toLowerCase(), 0);
            }
            for (String word : words) {
                String lowercaseWord = word.toLowerCase();
                if (wordCount.containsKey(lowercaseWord)) {
                    wordCount.put(lowercaseWord, wordCount.get(lowercaseWord) + 1);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            String mapAsString = sb.toString();
            model.addAttribute("textInput", searchForm.getTextInput());
            model.addAttribute("searchInput", searchForm.getSearchInput());
            model.addAttribute("result", mapAsString);

            return "home2";
        }

        else{
            String data = searchForm.getTextInput();
            int N = data.length();
            int U = 0, P = 0, C = 0, V = 0;
            for (int i = 0; i < N; i++) {
                char c = data.charAt(i);
                if (Character.isUpperCase(c)) {
                    U++;
                } else if (Character.isWhitespace(c)) {
                    P++;
                } else if (c == '.' || c == ',' || c == ':' || c == '?' || c == '$' || c == '(' || c == ')' || c == '&') {
                    C++;
                } else if (Character.isDigit(c)) {
                    V++;
                }
            }

            model.addAttribute("U", "U : "+  U);
            model.addAttribute("P", "P : "+  P);
            model.addAttribute("C", "C : "+  C);
            model.addAttribute("V", "V : "+  V);
            model.addAttribute("N", "N : "+  N);
            SearchForm searchForm1 = new SearchForm();
            searchForm1.setTextInput(data);
            searchForm1.setResult("U : "+  U+"\nP : "+  P+"\nC : "+  C+"\nV : "+  V+"\nN : "+  N);

            model.addAttribute("searchForm",searchForm1);
            model.addAttribute("textInput", searchForm.getTextInput());
            String cleanedData = data.replaceAll("[^a-zA-Z ]", "").replaceAll("\\d+", "").toUpperCase();

            int dataLen = cleanedData.length();
            model.addAttribute("result", "U : "+  U+"\nP : "+  P+"\nC : "+  C+"\nV : "+  V+"\nN : "+  N +"\n"+ "After len:" +dataLen +"\n" +cleanedData);
//            model.addAttribute("searchInput", searchForm.getSearchInput());
            return "home2";
        }
    }
    @PostMapping("/searchTwo")
    public String searchWord2(@ModelAttribute SearchForm searchForm, Model model) throws IOException {
//        String data = searchForm.getTextInput().trim().toLowerCase();
        if(searchForm.getSearchInput()!=null && searchForm.getSearchInput().trim().length()>0) {
            String text = searchForm.getTextInput().replaceAll("[^a-zA-Z\\s]", "").toUpperCase();
            String[] words = text.split("\\s+");
            String[] searchWords = searchForm.getSearchInput().split("\\s+");

            Map<String, Integer> wordCount = new HashMap<>();
            for (String searchWord : searchWords) {
                wordCount.put(searchWord.toLowerCase(), 0);
            }
            for (String word : words) {
                String lowercaseWord = word.toLowerCase();
                if (wordCount.containsKey(lowercaseWord)) {
                    wordCount.put(lowercaseWord, wordCount.get(lowercaseWord) + 1);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            String mapAsString = sb.toString();
            model.addAttribute("textInput", searchForm.getTextInput());
            model.addAttribute("searchInput", searchForm.getSearchInput());
            model.addAttribute("result", mapAsString);

            return "home3";
        }

        else{
            String data = searchForm.getTextInput();
            int N = data.length();
            int U = 0, P = 0, C = 0, V = 0;
            for (int i = 0; i < N; i++) {
                char c = data.charAt(i);
                if (Character.isUpperCase(c)) {
                    U++;
                } else if (Character.isWhitespace(c)) {
                    P++;
                } else if (c == '.' || c == ',' || c == ':' || c == '?' || c == '$' || c == '(' || c == ')' || c == '&') {
                    C++;
                } else if (Character.isDigit(c)) {
                    V++;
                }
            }

            model.addAttribute("U", "U : "+  U);
            model.addAttribute("P", "P : "+  P);
            model.addAttribute("C", "C : "+  C);
            model.addAttribute("V", "V : "+  V);
            model.addAttribute("N", "N : "+  N);
            SearchForm searchForm1 = new SearchForm();
            searchForm1.setTextInput(data);
            searchForm1.setResult("U : "+  U+"\nP : "+  P+"\nC : "+  C+"\nV : "+  V+"\nN : "+  N);

            model.addAttribute("searchForm",searchForm1);
            model.addAttribute("textInput", searchForm.getTextInput());
            String cleanedData = data.replaceAll("[^a-zA-Z ]", "").replaceAll("\\d+", "").toUpperCase();

            int dataLen = cleanedData.length();
            model.addAttribute("result", "U : "+  U+"\nP : "+  P+"\nC : "+  C+"\nV : "+  V+"\nN : "+  N +"\n"+ "After len:" +dataLen +"\n" +cleanedData);
//            model.addAttribute("searchInput", searchForm.getSearchInput());
            return "home3";
        }
    }
}
