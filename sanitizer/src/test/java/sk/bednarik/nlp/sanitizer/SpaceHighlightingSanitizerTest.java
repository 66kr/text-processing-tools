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
        .isEqualTo("Išiel domov a v tom sa pokazila električka.");
    assertThat(SpaceHighlightingSanitizer.sanitize("s a     n e b u d e    p o s u d z o v a ť"))
        .isEqualTo("sa nebude posudzovať");
    System.out.println(SpaceHighlightingSanitizer.sanitize("\n" +
        "                                            R O Z H O D N U T I E\n" +
        "\n" +
        "\n" +
        "Okresný  úrad Žiar nad Hronom, odbor starostlivosti o životné prostredie, ako príslušný orgán  štátnej správy na úseku posudzovania vplyvov na životné prostredie podľa § 56 písm. b) zákona č. 24/2006 Z. z.  o posudzovaní vplyvov na životné prostredie a o zmene a doplnení niektorých zákonov v znení neskorších predpisov,  v súlade s   § 2 ods. 3, ods. 6 a § 3 ods. 1 písm. e) zákona č. 180/2013 o organizovaní miestnej štátnej správy a o zmene a doplnení  niektorých zákonov a  podľa  § 5 ods. 1 zákona NR SR č. 525/2003 Z. z. o štátnej správe starostlivosti o životné prostredie v znení neskorších predpisov, ako príslušný orgán štátnej správy  na základe predloženého zámeru navrhovanej činnosti „ Výkup železných a neželezných kovov, papiera “,  ktorý  predložila navrhovateľka  Anita Hrmová – AEM zberné suroviny, Veternícka 169/66, 967 01 Kremnica,  IČO 00321125,   vykonal  zisťovacie konanie podľa § 29 ods. 1 zákona NR SR č. 24/2006 Z. z. o posudzovaní vplyvov na životné prostredie v znení neskorších predpisov  ( ďalej len „ zákon“ ) vydáva podľa § 29 zákona  toto rozhodnutie:\n"
        +
        "\n" +
        "             Navrhovaná činnosť „Výkup železných a neželezných kovov, papiera “, ktorej účelom je výkup železných a neželezných kovov, papiera, ich dočasné zhromaždenie a uskladnenie, navrhovaná  v zastavanom území Mesta Kremnica,   na pozemkoch C KN par. č. 1878/1, 1878/2 a 1878/4\n"
        +
        "                                       s 