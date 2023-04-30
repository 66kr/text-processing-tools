package sk.bednarik.nlp;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public class TestText {

  private final String input;
  private final List<String> sentences;
  private final List<String> tokens;

  public TestText(String input, List<String> sentences, List<String> tokens) {
    this.input = input;
    this.sentences = sentences;
    this.tokens = tokens;
  }

  private static String in1 = "\n" +
      "                                            R O Z H O D N U T I E\n" +
      "\n" +
      "\n" +
      "Okresný  úrad Žiar nad Hronom, odbor starostlivosti o životné prostredie, ako príslušný orgán  štátnej správy na úseku posudzovania vplyvov na životné prostredie podľa § 56 písm. b) zákona č. 24/2006 Z. z.