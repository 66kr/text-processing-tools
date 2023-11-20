package sk.bednarik.nlp.lemmatizer;

import com.google.common.collect.Lists;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;
import sk.bednarik.nlp.statistics.MapCounter;
import sk.bednarik.nlp.statistics.TextStatistics;
import sk.bednarik.nlp.tika.FileExtractor;
import sk.bednarik.nlp.utils.LemmaService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {LemmaServiceConfig.class})
public class LemmatizerUtilsTest {

  @Autowired
  private LemmaService lemmaService;

  private static final Pattern pattern = Pattern.compile("\\d+|[.,:\"“„;§'()-]|[A-Za-z]\\)");
  private static final String filename_300_2005 = "2005_300.txt";
  private static final String filename_301_2005 = "2005_301.txt";
  private static final String goldOutput_not_lemma_300_2005 = "goldOutput_not_lemma_300_2005.txt";
  private static final String goldOutput_not_lemma_301_2005 = "goldOutput_not_lemma_301_2005.txt";

  private String input = "Testovacia veta je smiešna lebo pri stole boli nohy. V uvedenej stratégii boli z hľadiska rozhlasového vysielania popísané predovšetkým hlavné ciele a zásady prechodu a bol navrhnutý technický a časový plán prechodu z pozemského analógového na pozemské digitálne rozhlasové vysielanie.";

  @Test
  public void test1() {
    List<String> goldOutput = Lists
        .newArrayList("Testovacia", "veta", "byť", "smiešny", "lebo", "pri", "stôl", "byť", "noha", ".", "V", "uvedený",
            "stratégia", "byť", "z", "hľadisko", "rozhlasový", "vysielanie", "popísaný", "predovšetkým", "hlavná",
            "cieľ", "a", "zásada", "prechod", "a", "bola", "navrhnutý", "technický", "a", "časový", "plán", "prechod",
            "z", "pozemský", "analógový", "na", "pozemský", "digitálne", "rozhlasový", "vysielanie", ".");
    List<CoreMap> labels = lemmaService.lemmatize(input);
    List<String> output = labels
        .stream()
        .map(sentences -> sentences.get(CoreAnnotations.TokensAnnotation.class))
        .flatMap(Collection::stream)
        .map(CoreLabel::lemma)
        .collect(Collectors.toList());
    Assert.assertEquals(goldOutput, output);
  }

  /**
   * keepOriginal must be set false in {@link sk.bednarik.nlp.spring.FSTLemmaComponent}
   */
  @Test
  public void findNotLematizedTokens() {
    List<CoreMap> labels = lemmaService.lemmatizeKeepNotLemmatized(input);
    List<String> output = labels
        .stream()
        .map(sentences -> sentences.get(CoreAnnotations.TokensAnnotation.class))
        