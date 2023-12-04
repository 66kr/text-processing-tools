package sk.bednarik.nlp.sutime;

import com.google.common.collect.Lists;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sk.bednarik.nlp.utils.TimesService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TimesServiceConfig.class})
public class SutimeTest {

  @Autowired
  private TimesService timesService;
  //    private static String text = "Špecializovaný trestný súd na verejnom zasadnutí konanom dnes a 15. 01. 2019 v Pezinku pred samosudcom Mgr. Ivanom Matelom, po schválení dohody o vine a treste podľa § 334 ods. 4 Trestného poriadku";
  private static String text = "Od zajtra do pondelka 27. januára 2016. Dnes je 26. jan. 2016 a zajtra 27. marca 2016 bude nedeľa. Máj je lásky čas. Čo s tým? Neviem. Včera som sa sprchoval a potom 