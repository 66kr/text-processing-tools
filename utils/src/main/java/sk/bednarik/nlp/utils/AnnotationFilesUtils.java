package sk.bednarik.nlp.utils;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

import java.io.*;
import java.util.List;

public class AnnotationFilesUtils {

  public static void saveResults(String id, Annotation annotation, String folder)
      throws FileNotFoundException, UnsupportedEncodingException {
    List<CoreLabel> labels = annotation.get(CoreAnnotations.TokensAnnotation.class);
    String originalText = annotation.get(CoreAnnotations.OriginalTextAnnotation.class);
    try (PrintWriter writer = new PrintWriter(folder + id + ".ann", "UTF-8")) {
      int i = 1;
      CoreLabel lastLabel = nu