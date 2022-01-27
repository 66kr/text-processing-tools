package sk.bednarik.nlp.sanitizer;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class SpaceHighlightingSanitizerTest {

  @Test
  public void testSanitization() {
    assertThat(SpaceHighlightingSanitizer.sanitize("P o u č e n i e")).isEqualTo("Poučenie");
    assertThat(SpaceHighlightingSanitizer.sanitize("3 a 4")).isEqualTo("3 a 4");
    assertThat(SpaceHighlightingSanitizer.sanitize("R O Z H O D N U T I E")).isEqualTo("ROZHODNUTIE");
    assertThat(SpaceHighlightingSanitizer.sanitize("O d ô v o d n e n i e")).isEqualTo("Odôvodnenie");
    assertThat(SpaceHighlightingSanitizer.sanitize("Išiel domov a v tom sa pokazila električka."))
        .isEqualTo("Išiel domov a v tom sa pokazila električka.")