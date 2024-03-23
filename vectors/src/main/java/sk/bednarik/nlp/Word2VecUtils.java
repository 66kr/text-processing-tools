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

  public static void initFul