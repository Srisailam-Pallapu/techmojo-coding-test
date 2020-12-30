package com.twitter.hashtags.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwitterHashTagTrendingController {

  @Autowired
  private ResourceLoader resourceLoader;
  private int trendingTop10Tweets = 10;

  @SuppressWarnings("rawtypes")
  @RequestMapping("/")
  public String printTrendingHashTags() {
    BufferedReader br = null;
    String response = "";
    try {
      final Resource fileResource = resourceLoader
          .getResource("classpath:twitter_input_file.txt");
      br = new BufferedReader(
          new InputStreamReader(fileResource.getInputStream()));
      Pattern pattern = Pattern.compile("(#\\w+)");
      Matcher m;

      LinkedHashMap<String, Integer> tredingTagcounter = new LinkedHashMap<String, Integer>();
      for (int i = 0; i < trendingTop10Tweets; i++) {
        String line = br.readLine();
        m = pattern.matcher(line);
        while (m.find()) {

          if (null != tredingTagcounter.get(m.group())) {
            tredingTagcounter.put(m.group(), (tredingTagcounter.get(m.group()) + 1));
          }
          else {
            tredingTagcounter.put(m.group(), new Integer(1));
          }

        }

      }
      List<Map.Entry<String, Integer>> listOfEntries = new ArrayList<Map.Entry<String, Integer>>(
          tredingTagcounter.entrySet());
      Collections.sort(listOfEntries, new Comparator<Map.Entry<String, Integer>>() {
        public int compare(Map.Entry<String, Integer> a,
            Map.Entry<String, Integer> b) {
          if (b.getValue().equals(a.getValue()))
            return a.getKey().compareTo(b.getKey());
          else
            return b.getValue().compareTo(a.getValue());
        }
      });
      Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
      for (Map.Entry<String, Integer> entry : listOfEntries) {
        sortedMap.put(entry.getKey(), entry.getValue());
      }
      Iterator it = sortedMap.entrySet().iterator();
      int counterTemp = 1;
      while (it.hasNext()) {
        Map.Entry pair = (Map.Entry) it.next();
        System.out.println(pair.getKey());
        response += (String) pair.getKey();
        response += "<br>";
        it.remove();
        if (counterTemp == trendingTop10Tweets)
          break;
        counterTemp++;
      }
    }
    catch (Exception e) {
      System.out.println("Exception : " + e);
    }

    return response;
  }

}
