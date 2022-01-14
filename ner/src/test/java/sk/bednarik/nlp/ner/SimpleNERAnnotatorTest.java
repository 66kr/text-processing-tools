package sk.bednarik.nlp.ner;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class SimpleNERAnnotatorTest {

  @Test
  public void testSimpleNERAnnotation() throws IOException {
    String[] inputWords = new String[]{"Ahoj", "volám", "sa", "Filip"};
    String[] expectedResult = new String[]{"O", "O", "O", "MENO"};

    //Create OriginalText annotation
    Annotation annotation = new Annotation(String.join(" ", inputWords));

    //Fake tokenizer START
    int position = -1;
    List<CoreLabel> tokens = new ArrayList<>();
    //TODO: Stream
    for (String word : inputWords) {
      CoreLabel token = CoreLabel.wordFromString(word);
      token.setBeginPosition(position + 1);
      position = position + 1 + word.length();
      token.setEndPosition(position);
      tokens.add(token);
    }
    annotation.set(CoreAnnota