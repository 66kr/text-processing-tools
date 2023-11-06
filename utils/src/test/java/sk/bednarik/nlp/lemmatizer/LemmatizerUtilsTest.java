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
  private stati