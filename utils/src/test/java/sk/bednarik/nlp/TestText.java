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
      "Okresný  úrad Žiar nad Hronom, odbor starostlivosti o životné prostredie, ako príslušný orgán  štátnej správy na úseku posudzovania vplyvov na životné prostredie podľa § 56 písm. b) zákona č. 24/2006 Z. z.  o posudzovaní vplyvov na životné prostredie a o zmene a doplnení niektorých zákonov v znení neskorších predpisov,  v súlade s   § 2 ods. 3, ods. 6 a § 3 ods. 1 písm. e) zákona č. 180/2013 o organizovaní miestnej štátnej správy a o zmene a doplnení  niektorých zákonov a  podľa  § 5 ods. 1 zákona NR SR č. 525/2003 Z. z. o štátnej správe starostlivosti o životné prostredie v znení neskorších predpisov, ako príslušný orgán štátnej správy  na základe predloženého zámeru navrhovanej činnosti „ Výkup železných a neželezných kovov, papiera “,  ktorý  predložila navrhovateľka  Anita Hrmová – AEM zberné suroviny, Veternícka 169/66, 967 01 Kremnica,  IČO 00321125,   vykonal  zisťovacie konanie podľa § 29 ods. 1 zákona NR SR č. 24/2006 Z. z. o posudzovaní vplyvov na životné prostredie v znení neskorších predpisov  ( ďalej len „ zákon“ ) vydáva podľa § 29 zákona  toto rozhodnutie:\n"
      +
      "\n" +
      "             Navrhovaná činnosť „Výkup železných a neželezných kovov, papiera “, ktorej účelom je výkup železných a neželezných kovov, papiera, ich dočasné zhromaždenie a uskladnenie, navrhovaná  v zastavanom území Mesta Kremnica,   na pozemkoch C KN par. č. 1878/1, 1878/2 a 1878/4\n"
      +
      "                                       s a     n e b u d e    p o s u d z o v a ť  \n" +
      "\n" +
      "podľa  zákona.  Pre uvedenú činnosť  je možné požiadať o povolenie podľa osobitných predpisov. Pre realizáciu sa navrhuje variant č. A pre ktorý sa stanovujú tieto  podmienky.  \n"
      +
      "\n" +
      "        Pri príprave dokumentácie stavby k stavebnému konaniu a v procese konania o povolení  činnosti podľa osobitných predpisov je potrebné zohľadniť v plnej miere  rešpektovať pripomienky, ktoré vyplynuli 