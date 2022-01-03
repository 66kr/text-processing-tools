package sk.bednarik.nlp.ner;

import com.google.common.collect.Sets;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.util.ArraySet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleNERAnnotator implements Annotator {

  private int maxTokens = 0;
  private HashMap<String, String> annotations = new HashMap<>();
  private HashSet<String> addable;

  public SimpleNERAnnotator(File sourceFile, String... addable) throws IOException {
    this(new FileReader(sourceFile), Sets.newHashSet(addable));
  }

  public SimpleNERAnnotator(InputStream sourceFile, String... addable) throws IOException {
    this(sourceFile, Sets.newHashSet(addable));
  }

  public SimpleNERAnnotator(InputStream inputStream, HashSet<String> addable) throws IOException {
    this(new InputStreamReader(inputStream), addable);
  }

  public SimpleNERAnnotator(Reader sourceFileReader, HashSet<String> addable) throws IOException {
    this.addable = addable;
    try (BufferedReader br = new BufferedReader(sourceFileReader)) {
      br.lines()
          .map(line -> line.split("\t"))
          .forEach(stringAndClass -> {
            Annotation annotation = new Annotation(stringAndClass[0]);
            new TokenizerAnnotator()
                .annotate(annotation); //TODO: must use the same tokenizer as pipeline!!!
            List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotat