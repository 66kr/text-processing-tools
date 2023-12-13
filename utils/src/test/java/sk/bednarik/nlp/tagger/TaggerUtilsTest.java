package sk.bednarik.nlp.tagger;

import com.google.common.collect.Lists;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sk.bednarik.nlp.utils.TaggerService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TaggerServiceConfig.class})
public class TaggerUtilsTest  {

  @Autowired
  private TaggerService taggerService;

  private String input = "Vláda Kanady a Európske spoločenstvo pre atómovú