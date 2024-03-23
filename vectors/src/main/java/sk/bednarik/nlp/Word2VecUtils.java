package sk.bednarik.nlp;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.springframework.stereotype.Component;

@Component
public class Word2VecUtils {

  private static Word2Vec word2Vec;

  public static void init() throws IOException {
    initLemma();
  }

  public static void initFull() throws IOException {
    String filename = "sk.essentialdata.nlp/vectors/embeddings.txt.gz";
    word2Vec = Dl4jWord2VecUtils.readTextModel(
        Word2VecUtils.class.getClassLoader().getResourceAsStream(filename),
        filename);
  }

  public static void initLemma() throws IOException {
    String filename = "sk.essentialdata.nlp/vectors/vec-sk-skipgram-lemma.bin";
    word2Vec = Dl4jWord2VecUtils.readBinaryModel(
        Word2VecUtils.class.getClassLoader().getResourceAsStream(filename),
        filename, true, false);
  }

  public static Collection<String> wordsNearest(String word) {
    return 