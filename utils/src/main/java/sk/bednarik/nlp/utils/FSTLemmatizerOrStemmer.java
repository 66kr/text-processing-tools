package sk.bednarik.nlp.utils;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.fst.CharSequenceOutputs;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.Util;
import sk.bednarik.nlp.stemmer.SlovakStemmer;
import sk.bednarik.nlp.tokenizer.model.Token;
import sk.bednarik.nlp.utils.model.Lemma;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class FSTLemmatizerOrStemmer {

  private FST<CharsRef> fst;
  private SlovakStemmer slovakStemmer;

  public FSTLemmatizerOrStemmer(File file) throws IOException {
    slovakStemmer = new SlovakStemmer();
    fst = FST.read(file.toPath(), CharSequenceOutputs.getSingleton());
  }

  public List<Lemma> lemmatizeOrStemString(List<Token> input, boolean keepNotLemmatized, boolean allowStemming)
      throws IOException {
    return input.stream()
        .map(token -> {
          String[] text = lemmatize(token)
              .orElse(
                  lemmatizeLowerCase(token)
                      .orElse(
                          lemmatizeProperCase(token)
                              .orElse(
                                  stem(token, allowStemming)
                                      .orElse(
                                          originalWord(token, keepNotLemmatized)
                                              .orElse(new String[0])))));
          return new Lemma(token, text);
        })
        .collect(toList());

  }

  private Optional<String[]> originalWord(Token token, boolean keepNotLemmatized) {
    return keepNo