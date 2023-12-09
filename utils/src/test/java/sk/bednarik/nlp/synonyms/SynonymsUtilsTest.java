package sk.bednarik.nlp.synonyms;

import static org.assertj.core.api.Assertions.assertThat;

import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sk.bednarik.nlp.commons.AsurAnnotations;
import sk.bednarik.nlp.utils.SynonymsService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes =