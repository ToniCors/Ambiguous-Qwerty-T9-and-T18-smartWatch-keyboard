package it.unisa.di.clueleab.isd;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictMap {
    private DictEntry root;
    private String[] index2chars;
    private Map<Character, Integer> char2index;

    public DictMap(Iterable<String> words, List<String> index2chars, int longerWordsTop, boolean firstNoLonger) {
        this.index2chars = index2chars.toArray(new String[index2chars.size()]);
        int nIndexes = index2chars.size();
        char2index = new HashMap<>();
        for (Integer i = 0; i < nIndexes; i++) {
            String k = index2chars.get(i);
            for (int j = 0; j < k.length(); j++) {
                char2index.put(k.charAt(j), i);
            }
        }
        ArrayList<SimpleEntry<DictEntry, Integer>> dicEnts = new ArrayList<>();
        dicEnts.add(new SimpleEntry<>(root = new DictEntry(nIndexes), 0));
        for (String word : words) {
            List<Integer> wordInds = toIndexes(word);
            DictEntry cur = root;
            for (int i = 0, n = word.length() - 1; i <= n; i++) {
                int index = wordInds.get(i);
                if (cur.entries[index] == null) {
                    dicEnts.add(new SimpleEntry<>(cur.entries[index] = new DictEntry(nIndexes), i + 1));
                }
                cur = cur.entries[index];
                if (i == n || cur.words.size() < longerWordsTop) {
                    cur.words.add(word);
                }
            }
        }
        if (firstNoLonger) {
            for (SimpleEntry<DictEntry, Integer> e : dicEnts) {
                List<String> deWords = e.getKey().words;
                int len = e.getValue();
                if (!deWords.isEmpty() && deWords.get(0).length() != len) {
                    boolean notFound = true;
                    for (int i = 1, n = deWords.size(); i < n; i++) {
                        String w = deWords.get(i);
                        if (w.length() == len) {
                            for (int j = i; j > 0; j--) {
                                deWords.set(j, deWords.get(j - 1));
                            }
                            deWords.set(0, w);
                            notFound = false;
                            break;
                        }
                    }
                    if (notFound) {
                        deWords.add(0, deWords.get(0).substring(0, len));
                    }
                }
                // e.getKey().words = Collections.unmodifiableList(deWords);
            }
        } // else {
        // for (SimpleEntry<DictEntry, Integer> e : dicEnts) {
        //   e.getKey().words = Collections.unmodifiableList(e.getKey().words);
        // }
        // }
    }

    public List<String> getWords(List<Integer> indexes, boolean addOutOfDict) {
        DictEntry cur = root;
        for (int index : indexes) {
            if (cur.entries[index] == null) {
                if (addOutOfDict) {
                    String first = cur.words.isEmpty() ? "" : cur.words.get(0);
                    int n = indexes.size();
                    if (first.length() >= n) {
                        return Arrays.asList(first.substring(0, n));
                    }
                    List<String> out = Arrays.asList(first);
                    for (int i = first.length(); i < n; i++) {
                        String iChars = index2chars[indexes.get(i)];
                        List<String> old = out;
                        out = new ArrayList<String>();
                        for (String w : old) {
                            for (int j = 0; j < iChars.length(); j++) {
                                out.add(w + iChars.charAt(j));
                            }
                        }
                    }
                    return out;
                } else {
                    return Collections.emptyList();
                }
            }
            cur = cur.entries[index];
        }
        return cur.words;
    }

    public List<Integer> toIndexes(String w) {
        Integer[] res = new Integer[w.length()];
        for (int i = 0; i < res.length; i++) {
            res[i] = char2index.get(Character.toLowerCase(w.charAt(i)));
        }
        return Arrays.asList(res);
    }

    private static class DictEntry {
        DictEntry[] entries;
        List<String> words;

        DictEntry(int nIndexes) {
            entries = new DictEntry[nIndexes];
            words = new ArrayList<String>();
        }
    }
}
