
package sk.bednarik.nlp.tokenizer;

// Stanford English Tokenizer -- a deterministic, fast high-quality tokenizer
// Copyright (c) 2002-2016 The Board of Trustees of
// The Leland Stanford Junior University. All Rights Reserved.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
// For more information, bug reports, fixes, contact:
//    Christopher Manning
//    Dept of Computer Science, Gates 1A
//    Stanford CA 94305-9010
//    USA
//    java-nlp-support@lists.stanford.edu
//    http://nlp.stanford.edu/software/


import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.AbstractTokenizer;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.LexedTokenFactory;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.WordTokenFactory;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.util.StringUtils;
import edu.stanford.nlp.util.logging.Redwood;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * A fast, rule-based tokenizer implementation, which produces Penn Treebank style tokenization of English text. It was
 * initially written to conform to Penn Treebank tokenization conventions over ASCII text, but now provides a range of
 * tokenization options over a broader space of Unicode text. It reads raw text and outputs tokens of classes that
 * implement edu.stanford.nlp.trees.HasWord (typically a Word or a CoreLabel). It can optionally return end-of-line as a
 * token.
 * <p>
 * New code is encouraged to use the {@link #SVKPTBTokenizer(Reader, LexedTokenFactory, String)} constructor. The other
 * constructors are historical. You specify the type of result tokens with a LexedTokenFactory, and can specify the
 * treatment of tokens by mainly boolean options given in a comma separated String options (e.g.,
 * "invertible,normalizeParentheses=true"). If the String is {@code null} or empty, you get the traditional PTB3
 * normalization behaviour (i.e., you get ptb3Escaping=true).  If you want no normalization, then you should pass in the
 * String "ptb3Escaping=false".  The known option names are:
 * <ol>
 * <li>invertible: Store enough information about the original form of the
 * token and the whitespace around it that a list of tokens can be faithfully converted back to the original String.
 * Valid only if the LexedTokenFactory is an instance of CoreLabelTokenFactory.  The keys used in it are: TextAnnotation
 * for the tokenized form, OriginalTextAnnotation for the original string, BeforeAnnotation and AfterAnnotation for the
 * whitespace before and after a token, and perhaps CharacterOffsetBeginAnnotation and CharacterOffsetEndAnnotation to
 * record token begin/after end character offsets, if they were specified to be recorded in TokenFactory construction.
 * (Like the String class, begin and end are done so end - begin gives the token length.) Default is false.
 * <li>tokenizeNLs: Whether end-of-lines should become tokens (or just
 * be treated as part of whitespace). Default is false.
 * <li>tokenizePerLine: Run the tokenizer separately on each line of a file.
 * This has the following consequences: (i) A token (currently only SGML tokens) cannot span multiple lines of the
 * original input, and (ii) The tokenizer will not examine/wait for input from the next line before deciding
 * tokenization decisions on this line. The latter property affects treating periods by acronyms as end-of-sentence
 * markers. Having this true is necessary to stop the tokenizer blocking and waiting for input after a newline is seen
 * when the previous line ends with an abbreviation. </li>
 * <li>ptb3Escaping: Enable all traditional PTB3 token transforms
 * (like parentheses becoming -LRB-, -RRB-).  This is a macro flag that sets or clears all the options below. (Default
 * setting of the various properties below that this flag controls is equivalent to it being set to true.)
 * <li>americanize: Whether to rewrite common British English spellings
 * as American English spellings. (This is useful if your training material uses American English spelling, such as the
 * Penn Treebank.) Default is true.
 * <li>normalizeSpace: Whether any spaces in tokens (phone numbers, fractions
 * get turned into U+00A0 (non-breaking space).  It's dangerous to turn this off for most of our Stanford NLP software,
 * which assumes no spaces in tokens. Default is true.
 * <li>normalizeAmpersandEntity: Whether to map the XML {@code &amp;} to an
 * ampersand. Default is true.
 * <li>normalizeFractions: Whether to map certain common composed
 * fraction characters to spelled out letter forms like "1/2". Default is true.
 * <li>normalizeParentheses: Whether to map round parentheses to -LRB-,
 * -RRB-, as in the Penn Treebank. Default is true.
 * <li>normalizeOtherBrackets: Whether to map other common bracket characters
 * to -LCB-, -LRB-, -RCB-, -RRB-, roughly as in the Penn Treebank. Default is true.
 * <li>asciiQuotes: Whether to map all quote characters to the traditional ' and ".
 * Default is false.
 * <li>latexQuotes: Whether to map quotes to ``, `, ', '', as in Latex
 * and the PTB3 WSJ (though this is now heavily frowned on in Unicode). If true, this takes precedence over the setting
 * of unicodeQuotes; if both are false, no mapping is done.  Default is true.
 * <li>unicodeQuotes: Whether to map quotes to the range U+2018 to U+201D,
 * the preferred unicode encoding of single and double quotes. Default is false.
 * <li>ptb3Ellipsis: Whether to map ellipses to three dots (...), the
 * old PTB3 WSJ coding of an ellipsis. If true, this takes precedence over the setting of unicodeEllipsis; if both are
 * false, no mapping is done. Default is true.
 * <li>unicodeEllipsis: Whether to map dot and optional space sequences to
 * U+2026, the Unicode ellipsis character. Default is false.
 * <li>ptb3Dashes: Whether to turn various dash characters into "--",
 * the dominant encoding of dashes in the PTB3 WSJ. Default is true.
 * <li>keepAssimilations: true to tokenize "gonna", false to tokenize
 * "gon na".  Default is true.
 * <li>escapeForwardSlashAsterisk: Whether to put a backslash escape in front
 * of / and * as the old PTB3 WSJ does for some reason (something to do with Lisp readers??). Default is false. This
 * flag is no longer set by ptb3Escaping.
 * <li>normalizeCurrency: Whether to do some awful lossy currency mappings
 * to turn common currency characters into $, #, or "cents", reflecting the fact that nothing else appears in the old
 * PTB3 WSJ.  (No Euro!) Default is false. (Note: The default was true through CoreNLP v3.8.0, but we're gradually
 * inching our way towards the modern world!) This flag is no longer set by ptb3Escaping.
 * <li>untokenizable: What to do with untokenizable characters (ones not
 * known to the tokenizer).  Six options combining whether to log a warning for none, the first, or all, and whether to
 * delete them or to include them as single character tokens in the output: noneDelete, firstDelete, allDelete,
 * noneKeep, firstKeep, allKeep. The default is "firstDelete".
 * <li>strictTreebank3: SVKPTBTokenizer deliberately deviates from strict PTB3
 * WSJ tokenization in two cases.  Setting this improves compatibility for those cases.  They are: (i) When an acronym
 * is followed by a sentence end, such as "U.K." at the end of a sentence, the PTB3 has tokens of "Corp" and ".", while
 * by default SVKPTBTokenizer duplicates the period returning tokens of "Corp." and ".", and (ii) SVKPTBTokenizer will
 * return numbers with a whole number and a fractional part like "5 7/8" as a single token, with a non-breaking space in
 * the middle, while the PTB3 separates them into two tokens "5" and "7/8". (Exception: for only "U.S." the treebank
 * does have the two tokens "U.S." and "." like our default; strictTreebank3 now does that too.) The default is false.
 * <li>splitHyphenated: whether or not to tokenize segments of hyphenated words
 * separately ("school" "-" "aged", "frog" "-" "lipped"), keeping the exceptions in Supplementary Guidelines for ETTB
 * 2.0 by Justin Mott, Colin Warner, Ann Bies, Ann Taylor and CLEAR guidelines (Bracketing Biomedical Text) by Colin
 * Warner et al. (2012). Default is false, which maintains old treebank tokenizer behavior.
 * </ol>
 * <p>
 * A single instance of a SVKPTBTokenizer is not thread safe, as it uses a non-threadsafe JFlex object to do the
 * processing.  Multiple instances can be created safely, though.  A single instance of a SVKPTBTokenizerFactory is also
 * not thread safe, as it keeps its options in a local variable.
 * </p>
 *
 * @author Tim Grow (his tokenizer is a Java implementation of Professor Chris Manning's Flex tokenizer,
 * pgtt-treebank.l)
 * @author Teg Grenager (grenager@stanford.edu)
 * @author Jenny Finkel (integrating in invertible PTB tokenizer)
 * @author Christopher Manning (redid API, added many options, maintenance)
 */
public class SVKPTBTokenizer<T extends HasWord> extends AbstractTokenizer<T> {

  /**
   * A logger for this class
   */
  private static final Redwood.RedwoodChannels log = Redwood.channels(SVKPTBTokenizer.class);

  // the underlying lexer
  private final SVKPTBLexer lexer;


  /**
   * Constructs a new SVKPTBTokenizer that returns Word tokens and which treats carriage returns as normal whitespace.
   *
   * @param r The Reader whose contents will be tokenized
   * @return A SVKPTBTokenizer that tokenizes a stream to objects of type {@link Word}
   */
  public static SVKPTBTokenizer<Word> newSVKPTBTokenizer(Reader r) {
    return new SVKPTBTokenizer<>(r, new WordTokenFactory(), "");
  }


  /**
   * Constructs a new SVKPTBTokenizer that makes CoreLabel tokens. It optionally returns carriage returns as their own
   * token. CRs come back as Words whose text is the value of {@code AbstractTokenizer.NEWLINE_TOKEN}.
   *
   * @param r The Reader to read tokens from
   * @param tokenizeNLs Whether to return newlines as separate tokens (otherwise they normally disappear as whitespace)
   * @param invertible if set to true, then will produce CoreLabels which will have fields for the string before and
   * after, and the character offsets
   * @return A SVKPTBTokenizer which returns CoreLabel objects
   */
  public static SVKPTBTokenizer<CoreLabel> newSVKPTBTokenizer(Reader r, boolean tokenizeNLs, boolean invertible) {
    return new SVKPTBTokenizer<>(r, tokenizeNLs, invertible, false, new CoreLabelTokenFactory());
  }


  /**
   * Constructs a new SVKPTBTokenizer that optionally returns carriage returns as their own token, and has a custom
   * LexedTokenFactory. If asked for, CRs come back as Words whose text is the value of {@code PTBLexer.cr}.  This
   * constructor translates between the traditional boolean options of SVKPTBTokenizer and the new options String.
   *
   * @param r The Reader to read tokens from
   * @param tokenizeNLs Whether to return newlines as separate tokens (otherwise they normally disappear as whitespace)
   * @param invertible if set to true, then will produce CoreLabels which will have fields for the string before and
   * after, and the character offsets
   * @param suppressEscaping If true, all the traditional Penn Treebank normalizations are turned off.  Otherwise, they
   * all happen.
   * @param tokenFactory The LexedTokenFactory to use to create tokens from the text.
   */
  private SVKPTBTokenizer(final Reader r,
      final boolean tokenizeNLs,
      final boolean invertible,
      final boolean suppressEscaping,
      final LexedTokenFactory<T> tokenFactory) {
    StringBuilder options = new StringBuilder();
    if (suppressEscaping) {
      options.append("ptb3Escaping=false");
    } else {
      options.append("ptb3Escaping=true"); // i.e., turn on all the historical PTB normalizations
    }
    if (tokenizeNLs) {
      options.append(",tokenizeNLs");
    }
    if (invertible) {
      options.append(",invertible");
    }
    lexer = new SVKPTBLexer(r, tokenFactory, options.toString());
  }


  /**
   * Constructs a new SVKPTBTokenizer with a custom LexedTokenFactory. Many options for tokenization and what is
   * returned can be set via the options String. See the class documentation for details on the options String.  This is
   * the new recommended constructor!
   *
   * @param r The Reader to read tokens from.
   * @param tokenFactory The LexedTokenFactory to use to create tokens from the text.
   * @param options Options to the lexer.  See the extensive documentation in the class javadoc.  The String may be null
   * or empty, which means that all traditional PTB normalizations are done.  You can pass in "ptb3Escaping=false" and
   * have no normalizations done (that is, the behavior of the old suppressEscaping=true option).
   */
  public SVKPTBTokenizer(final Reader r,
      final LexedTokenFactory<T> tokenFactory,
      final String options) {
    lexer = new SVKPTBLexer(r, tokenFactory, options);
  }


  /**
   * Internally fetches the next token.
   *
   * @return the next token in the token stream, or null if none exists.
   */
  @Override
  @SuppressWarnings("unchecked")
  protected T getNext() {
    // if (lexer == null) {
    //   return null;
    // }
    try {
      return (T) lexer.next();
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
    // cdm 2007: this shouldn't be necessary: PTBLexer decides for itself whether to return CRs based on the same flag!
    // get rid of CRs if necessary
    // while (!tokenizeNLs && PTBLexer.cr.equals(((HasWord) token).word())) {
    //   token = (T)lexer.next();
    // }

    // horatio: we used to catch exceptions here, which led to broken
    // behavior and made it very difficult to debug whatever the
    // problem was.
  }

  /**
   * Returns the string literal inserted for newlines when the -tokenizeNLs options is set.
   *
   * @return string literal inserted for "\n".
   */
  public static String getNewlineToken() {
    return NEWLINE_TOKEN;
  }

  /**
   * Returns a presentable version of the given PTB-tokenized text. PTB tokenization splits up punctuation and does
   * various other things that makes simply joining the tokens with spaces look bad. So join the tokens with space and
   * run it through this method to produce nice looking text. It's not perfect, but it works pretty well.
   * <p>
   * <b>Note:</b> If your tokens have maintained the OriginalTextAnnotation and
   * the BeforeAnnotation and the AfterAnnotation, then rather than doing this you can actually precisely reconstruct
   * the text they were made from!
   *
   * @param ptbText A String in PTB3-escaped form
   * @return An approximation to the original String
   */
  public static String ptb2Text(String ptbText) {
    StringBuilder sb = new StringBuilder(ptbText.length()); // probably an overestimate
    PTB2TextLexer lexer = new PTB2TextLexer(new StringReader(ptbText));
    try {
      for (String token; (token = lexer.next()) != null; ) {
        sb.append(token);
      }
    } catch (IOException e) {
      throw new RuntimeIOException(e);
    }
    return sb.toString();
  }

  /**
   * Returns a presentable version of a given PTB token. For instance, it transforms -LRB- into (.
   */
  public static String ptbToken2Text(String ptbText) {
    return ptb2Text(' ' + ptbText + ' ').trim();
  }

  /**
   * Writes a presentable version of the given PTB-tokenized text. PTB tokenization splits up punctuation and does
   * various other things that makes simply joining the tokens with spaces look bad. So join the tokens with space and
   * run it through this method to produce nice looking text. It's not perfect, but it works pretty well.
   */
  public static int ptb2Text(Reader ptbText, Writer w) throws IOException {
    int numTokens = 0;
    PTB2TextLexer lexer = new PTB2TextLexer(ptbText);
    for (String token; (token = lexer.next()) != null; ) {
      numTokens++;
      w.write(token);
    }
    return numTokens;
  }

  private static void untok(List<String> inputFileList, List<String> outputFileList, String charset)
      throws IOException {
    final long start = System.nanoTime();
    int numTokens = 0;
    int sz = inputFileList.size();
    if (sz == 0) {
      Reader r = new InputStreamReader(System.in, charset);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, charset));
      numTokens = ptb2Text(r, writer);
      writer.close();
    } else {
      for (int j = 0; j < sz; j++) {
        try (Reader r = IOUtils.readerFromString(inputFileList.get(j), charset)) {
          BufferedWriter writer;
          if (outputFileList == null) {
            writer = new BufferedWriter(new OutputStreamWriter(System.out, charset));
          } else {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileList.get(j)), charset));
          }
          try {
            numTokens += ptb2Text(r, writer);
          } finally {
            writer.close();
          }
        }
      }
    }
    final long duration = System.nanoTime() - start;
    final double wordsPerSec = (double) numTokens / ((double) duration / 1000000000.0);
    System.err.printf("SVKPTBTokenizer untokenized %d tokens at %.2f tokens per second.%n", numTokens, wordsPerSec);
  }

  /**
   * Returns a presentable version of the given PTB-tokenized words. Pass in a List of Strings and this method will join
   * the words with spaces and call {@link #ptb2Text(String)} on the output.
   *
   * @param ptbWords A list of String
   * @return A presentable version of the given PTB-tokenized words
   */
  public static String ptb2Text(List<String> ptbWords) {
    return ptb2Text(StringUtils.join(ptbWords));
  }


  /**
   * Returns a presentable version of the given PTB-tokenized words. Pass in a List of Words or a Document and this
   * method will take the word() values (to prevent additional text from creeping in, e.g., POS tags), and call {@link
   * #ptb2Text(String)} on the output.
   *
   * @param ptbWords A list of HasWord objects
   * @return A presentable version of the given PTB-tokenized words
   */
  public static String labelList2Text(List<? extends HasWord> ptbWords) {
    List<String> words = new ArrayList<>();
    for (HasWord hw : ptbWords) {
      words.add(hw.word());
    }

    return ptb2Text(words);
  }


  private static void tok(List<String> inputFileList, List<String> outputFileList, String charset,
      Pattern parseInsidePattern, Pattern filterPattern, String options,
      boolean preserveLines, boolean oneLinePerElement, boolean dump, boolean lowerCase) throws IOException {
    final long start = System.nanoTime();
    long numTokens = 0;
    int numFiles = inputFileList.size();
    if (numFiles == 0) {
      Reader stdin = IOUtils.readerFromStdin(charset);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, charset));
      numTokens += tokReader(stdin, writer, parseInsidePattern, filterPattern, options, preserveLines,
          oneLinePerElement, dump, lowerCase);
      IOUtils.closeIgnoringExceptions(writer);

    } else {
      BufferedWriter out = null;
      if (outputFileList == null) {
        out = new BufferedWriter(new OutputStreamWriter(System.out, charset));
      }
      for (int j = 0; j < numFiles; j++) {
        try (Reader r = IOUtils.readerFromString(inputFileList.get(j), charset)) {
          if (outputFileList != null) {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileList.get(j)), charset));
          }
          numTokens += tokReader(r, out, parseInsidePattern, filterPattern, options, preserveLines, oneLinePerElement,
              dump, lowerCase);
        }
        if (outputFileList != null) {
          IOUtils.closeIgnoringExceptions(out);
        }
      } // end for j going through inputFileList
      if (outputFileList == null) {
        IOUtils.closeIgnoringExceptions(out);
      }
    }

    final long duration = System.nanoTime() - start;
    final double wordsPerSec = (double) numTokens / ((double) duration / 1000000000.0);
    System.err.printf("SVKPTBTokenizer tokenized %d tokens at %.2f tokens per second.%n", numTokens, wordsPerSec);
  }

  private static int tokReader(Reader r, BufferedWriter writer, Pattern parseInsidePattern, Pattern filterPattern,
      String options,
      boolean preserveLines, boolean oneLinePerElement, boolean dump, boolean lowerCase) throws IOException {
    int numTokens = 0;
    boolean beginLine = true;
    boolean printing = (parseInsidePattern == null); // start off printing, unless you're looking for a start entity
    Matcher m = null;
    if (parseInsidePattern != null) {
      m = parseInsidePattern.matcher(""); // create once as performance hack
      // System.err.printf("parseInsidePattern is: |%s|%n", parseInsidePattern);
    }
    for (SVKPTBTokenizer<CoreLabel> tokenizer = new SVKPTBTokenizer<>(r, new CoreLabelTokenFactory(), options);
        tokenizer.hasNext(); ) {
      CoreLabel obj = tokenizer.next();
      // String origStr = obj.get(CoreAnnotations.TextAnnotation.class).replaceFirst("\n+$", ""); // DanC added this to fix a lexer bug, hopefully now corrected
      String origStr = obj.get(CoreAnnotations.TextAnnotation.class);
      String str;
      if (lowerCase) {
        str = origStr.toLowerCase(Locale.ENGLISH);
        obj.set(CoreAnnotations.TextAnnotation.class, str);
      } else {
        str = origStr;
      }
      if (m != null && m.reset(origStr).matches()) {
        printing = m.group(1).isEmpty(); // turn on printing if no end element slash, turn it off it there is
        // System.err.printf("parseInsidePattern matched against: |%s|, printing is %b.%n", origStr, printing);
        if (!printing) {
          // true only if matched a stop
          beginLine = true;
          if (oneLinePerElement) {
            writer.newLine();
          }
        }
      } else if (printing) {
        if (dump) {
          // after having checked for tags, change str to be exhaustive
          str = obj.toShorterString();
        }
        if (filterPattern != null && filterPattern.matcher(origStr).matches()) {
          // skip
        } else if (preserveLines) {
          if (NEWLINE_TOKEN.equals(origStr)) {
            beginLine = true;
            writer.newLine();
          } else {
            if (!beginLine) {
              writer.write(' ');
            } else {
              beginLine = false;
            }
            // writer.write(str.replace("\n", ""));
            writer.write(str);
          }
        } else if (oneLinePerElement) {
          if (!beginLine) {
            writer.write(' ');
          } else {
            beginLine = false;
          }
          writer.write(str);
        } else {
          writer.write(str);
          writer.newLine();
        }
      }
      numTokens++;
    }
    return numTokens;
  }


  /**
   * @return A SVKPTBTokenizerFactory that vends Word tokens.
   */
  public static TokenizerFactory<Word> factory() {
    return SVKPTBTokenizerFactory.newTokenizerFactory();
  }

  /**
   * @return A SVKPTBTokenizerFactory that vends CoreLabel tokens.
   */
  public static TokenizerFactory<CoreLabel> factory(boolean tokenizeNLs, boolean invertible) {
    return SVKPTBTokenizerFactory.newSVKPTBTokenizerFactory(tokenizeNLs, invertible);
  }


  /**
   * @return A SVKPTBTokenizerFactory that vends CoreLabel tokens with default tokenization.
   */
  public static TokenizerFactory<CoreLabel> coreLabelFactory() {
    return coreLabelFactory("");
  }

  /**
   * @return A SVKPTBTokenizerFactory that vends CoreLabel tokens with default tokenization.
   */
  public static TokenizerFactory<CoreLabel> coreLabelFactory(String options) {
    return SVKPTBTokenizerFactory.newSVKPTBTokenizerFactory(new CoreLabelTokenFactory(), options);
  }

  /**
   * Get a TokenizerFactory that does Penn Treebank tokenization. This is now the recommended factory method to use.
   *
   * @param factory A TokenFactory that determines what form of token is returned by the Tokenizer
   * @param options A String specifying options (see the class javadoc for details)
   * @param <T> The type of the tokens built by the LexedTokenFactory
   * @return A TokenizerFactory that does Penn Treebank tokenization
   */
  public static <T extends HasWord> TokenizerFactory<T> factory(LexedTokenFactory<T> factory, String options) {
    return new SVKPTBTokenizerFactory<>(factory, options);

  }


  /**
   * This class provides a factory which will vend instances of SVKPTBTokenizer which wrap a provided Reader.  See the
   * documentation for {@link SVKPTBTokenizer} for details of the parameters and options.
   *
   * @param <T> The class of the returned tokens
   * @see SVKPTBTokenizer
   */
  public static class SVKPTBTokenizerFactory<T extends HasWord> implements TokenizerFactory<T> {

    private static final long serialVersionUID = -8859638719818931606L;

    @SuppressWarnings("serial")
    protected final LexedTokenFactory<T> factory;
    protected String options;


    /**
     * Constructs a new TokenizerFactory that returns Word objects and treats carriage returns as normal whitespace.
     * THIS METHOD IS INVOKED BY REFLECTION BY SOME OF THE JAVANLP CODE TO LOAD A TOKENIZER FACTORY.  IT SHOULD BE
     * PRESENT IN A TokenizerFactory.
     *
     * @return A TokenizerFactory that returns Word objects
     */
    public static TokenizerFactory<Word> newTokenizerFactory() {
      return newSVKPTBTokenizerFactory(new WordTokenFactory(), "");
    }

    /**
     * Constructs a new SVKPTBTokenizer that returns Word objects and uses the options passed in. THIS METHOD IS INVOKED
     * BY REFLECTION BY SOME OF THE JAVANLP CODE TO LOAD A TOKENIZER FACTORY.  IT SHOULD BE PRESENT IN A
     * TokenizerFactory.
     *
     * @param options A String of options
     * @return A TokenizerFactory that returns Word objects
     */
    public static SVKPTBTokenizerFactory<Word> newWordTokenizerFactory(String options) {
      return new SVKPTBTokenizerFactory<>(new WordTokenFactory(), options);
    }

    /**
     * Constructs a new SVKPTBTokenizer that returns CoreLabel objects and uses the options passed in.
     *
     * @param options A String of options. For the default, recommended options for PTB-style tokenization
     * compatibility, pass in an empty String.
     * @return A TokenizerFactory that returns CoreLabel objects o
     */
    public static SVKPTBTokenizerFactory<CoreLabel> newCoreLabelTokenizerFactory(String options) {
      return new SVKPTBTokenizerFactory<>(new CoreLabelTokenFactory(), options);
    }

    /**
     * Constructs a new SVKPTBTokenizer that uses the LexedTokenFactory and options passed in.
     *
     * @param tokenFactory The LexedTokenFactory
     * @param options A String of options
     * @return A TokenizerFactory that returns objects of the type of the LexedTokenFactory
     */
    public static <T extends HasWord> SVKPTBTokenizerFactory<T> newSVKPTBTokenizerFactory(
        LexedTokenFactory<T> tokenFactory, String options) {
      return new SVKPTBTokenizerFactory<>(tokenFactory, options);
    }

    public static SVKPTBTokenizerFactory<CoreLabel> newSVKPTBTokenizerFactory(boolean tokenizeNLs, boolean invertible) {
      return new SVKPTBTokenizerFactory<>(tokenizeNLs, invertible, false, new CoreLabelTokenFactory());
    }

    // Constructors

    // This one is historical
    private SVKPTBTokenizerFactory(boolean tokenizeNLs, boolean invertible, boolean suppressEscaping,
        LexedTokenFactory<T> factory) {
      this.factory = factory;
      StringBuilder optionsSB = new StringBuilder();
      if (suppressEscaping) {
        optionsSB.append("ptb3Escaping=false");
      } else {
        optionsSB.append("ptb3Escaping=true"); // i.e., turn on all the historical PTB normalizations
      }
      if (tokenizeNLs) {
        optionsSB.append(",tokenizeNLs");
      }
      if (invertible) {
        optionsSB.append(",invertible");
      }
      this.options = optionsSB.toString();
    }

    /**
     * Make a factory for SVKPTBTokenizers.
     *
     * @param tokenFactory A factory for the token type that the tokenizer will return
     * @param options Options to the tokenizer (see the class documentation for details)
     */
    private SVKPTBTokenizerFactory(LexedTokenFactory<T> tokenFactory, String options) {
      this.factory = tokenFactory;
      this.options = options;
    }


    /**
     * Returns a tokenizer wrapping the given Reader.
     */
    @Override
    public Iterator<T> getIterator(Reader r) {
      return getTokenizer(r);
    }

    /**
     * Returns a tokenizer wrapping the given Reader.
     */
    @Override
    public Tokenizer<T> getTokenizer(Reader r) {
      return new SVKPTBTokenizer<>(r, factory, options);
    }

    @Override
    public Tokenizer<T> getTokenizer(Reader r, String extraOptions) {
      if (options == null || options.isEmpty()) {
        return new SVKPTBTokenizer<>(r, factory, extraOptions);
      } else {
        return new SVKPTBTokenizer<>(r, factory, options + ',' + extraOptions);
      }
    }

    @Override
    public void setOptions(String options) {
      this.options = options;
    }

  } // end static class SVKPTBTokenizerFactory


  /**
   * Command-line option specification.
   */
  private static Map<String, Integer> optionArgDefs() {
    Map<String, Integer> optionArgDefs = Generics.newHashMap();
    optionArgDefs.put("options", 1);
    optionArgDefs.put("ioFileList", 0);
    optionArgDefs.put("fileList", 0);
    optionArgDefs.put("lowerCase", 0);
    optionArgDefs.put("dump", 0);
    optionArgDefs.put("untok", 0);
    optionArgDefs.put("encoding", 1);
    optionArgDefs.put("parseInside", 1);
    optionArgDefs.put("filter", 1);
    optionArgDefs.put("preserveLines", 0);
    optionArgDefs.put("oneLinePerElement", 0);
    return optionArgDefs;
  }

  /**
   * Reads files given as arguments and print their tokens, by default as one per line.  This is useful either for
   * testing or to run standalone to turn a corpus into a one-token-per-line file of tokens. This main method assumes
   * that the input file is in utf-8 encoding, unless an encoding is specified.
   * <p>
   * Usage: {@code java edu.stanford.nlp.process.SVKPTBTokenizer [options] filename+ }
   * <p>
   * Options:
   * <ul>
   * <li> -options options Set various tokenization options
   * (see the documentation in the class javadoc).
   * <li> -preserveLines Produce space-separated tokens, except
   * when the original had a line break, not one-token-per-line.
   * <li> -oneLinePerElement Print the tokens of an element space-separated on one line.
   * An "element" is either a file or one of the elements matched by the parseInside regex. </li>
   * <li> -filter regex Delete any token that matches() (in its entirety) the given regex. </li>
   * <li> -encoding encoding Specifies a character encoding. If you do not
   * specify one, the default is utf-8 (not the platform default).
   * <li> -lowerCase Lowercase all tokens (on tokenization).
   * <li> -parseInside regex Names an XML-style element or a regular expression
   * over such elements.  The tokenizer will only tokenize inside elements that match this regex.  (This is done by
   * regex matching, not an XML parser, but works well for simple XML documents, or other SGML-style documents, such as
   * Linguistic Data Consortium releases, which adopt the convention that a line of a file is either XML markup or
   * character data but never both.)
   * <li> -ioFileList file* The remaining command-line arguments are treated as
   * filenames that themselves contain lists of pairs of input-output filenames (2 column, whitespace separated).
   * Alternatively, if there is only one filename per line, the output filename is the input filename with ".tok"
   * appended.
   * <li> -fileList file* The remaining command-line arguments are treated as
   * filenames that contain filenames, one per line. The output of tokenization is sent to stdout.
   * <li> -dump Print the whole of each CoreLabel, not just the value (word).
   * <li> -untok Heuristically untokenize tokenized text.
   * <li> -h, -help Print usage info.
   * </ul>
   * <p>
   * A note on {@code -preserveLines}: Basically, if you use this option, your output file should have the same number
   * of lines as your input file. If not, there is a bug. But the truth of this statement depends on how you count
   * lines…. Unicode includes "line separator" and "paragraph separator" characters and Unicode says that you should
   * accept them. See e.g., http://unicode.org/standard/reports/tr13/tr13-5.html
   * <p>
   * However, Unix, Linux utilities, etc. don't recognize them and count only the traditional \n|\r|\r\n. And
   * SVKPTBTokenizer does normalize line separation. Hence, if your input text contains, say U+2028 Line Separator
   * characters, the Unix wc utility will report more lines after tokenization than before, even though line breaks have
   * been preserved, according to Unicode. It may be useful to compare results with the Perl uniwc script from
   * https://raw.githubusercontent.com/briandfoy/Unicode-Tussle/master/script/uniwc
   * <p>
   * If it reports the same number of input and output lines, then this difference is your problem, and in a certain
   * Unicode sense, our tokenizer did indeed preserve the line count. If not, please send us a bug report. At present
   * there is no way to disable this process of Unicode separator characters. If you don't want this anomaly, you'll
   * need to either delete these two characters or to map them to conventional Unix newline characters. Or to some other
   * weirdo character.
   *
   * @param args Command line arguments
   * @throws IOException If any file I/O problem
   */
  public static void main(String[] args) throws IOException {
    Properties options = StringUtils.argsToProperties(args, optionArgDefs());
    boolean showHelp = PropertiesUtils.getBool(options, "help", false);
    showHelp = PropertiesUtils.getBool(options, "h", showHelp);
    if (showHelp) {
      log.info("Usage: java edu.stanford.nlp.process.SVKPTBTokenizer [options]* filename*");
      log.info("  options: -h|-help|-options tokenizerOptions|-encoding encoding|-dump|");
      log.info("           -lowerCase|-preserveLines|-oneLinePerElement|-filter regex|");
      log.info("           -parseInside regex|-fileList|-ioFileList|-untok");
      return;
    }

    StringBuilder optionsSB = new StringBuilder();
    String tokenizerOptions = options.getProperty("options", null);
    if (tokenizerOptions != null) {
      optionsSB.append(tokenizerOptions);
    }
    boolean preserveLines = PropertiesUtils.getBool(options, "preserveLines", false);
    if (preserveLines) {
      optionsSB.append(",tokenizeNLs");
    }
    boolean oneLinePerElement = PropertiesUtils.getBool(options, "oneLinePerElement", false);
    boolean inputOutputFileList = PropertiesUtils.getBool(options, "ioFileList", false);
    boolean fileList = PropertiesUtils.getBool(options, "fileList", false);
    boolean lowerCase = PropertiesUtils.getBool(options, "lowerCase", false);
    boolean dump = PropertiesUtils.getBool(options, "dump", false);
    boolean untok = PropertiesUtils.getBool(options, "untok", false);
    String charset = options.getProperty("encoding", "utf-8");
    String parseInsideValue = options.getProperty("parseInside", null);
    Pattern parseInsidePattern = null;
    if (parseInsideValue != null) {
      try {
        // We still allow space, but SVKPTBTokenizer will change space to &nbsp; so need to also match it
        parseInsidePattern = Pattern.compile("<(/?)(?:" + parseInsideValue + ")(?:(?:\\s|\u00A0)[^>]*?)?>");
      } catch (PatternSyntaxException e) {
        // just go with null parseInsidePattern
      }
    }
    String filterValue = options.getProperty("filter", null);
    Pattern filterPattern = null;
    if (filterValue != null) {
      try {
        filterPattern = Pattern.compile(filterValue);
      } catch (PatternSyntaxException e) {
        // just go with null filterPattern
      }
    }

    // Other arguments are filenames
    String parsedArgStr = options.getProperty("", null);
    String[] parsedArgs = (parsedArgStr == null) ? null : parsedArgStr.split("\\s+");

    ArrayList<String> inputFileList = new ArrayList<>();
    ArrayList<String> outputFileList = null;
    if (parsedArgs != null) {
      if (fileList || inputOutputFileList) {
        outputFileList = new ArrayList<>();
        for (String fileName : parsedArgs) {
          BufferedReader r = IOUtils.readerFromString(fileName, charset);
          for (String inLine; (inLine = r.readLine()) != null; ) {
            String[] fields = inLine.split("\\s+");
            inputFileList.add(fields[0]);
            if (fields.length > 1) {
              outputFileList.add(fields[1]);
            } else {
              outputFileList.add(fields[0] + ".tok");
            }
          }
          r.close();
        }
        if (fileList) {
          // We're not actually going to use the outputFileList!
          outputFileList = null;
        }
      } else {
        // Concatenate input files into a single output file
        inputFileList.addAll(Arrays.asList(parsedArgs));
      }
    }

    if (untok) {
      untok(inputFileList, outputFileList, charset);
    } else {
      tok(inputFileList, outputFileList, charset, parseInsidePattern, filterPattern, optionsSB.toString(),
          preserveLines, oneLinePerElement, dump, lowerCase);
    }
  } // end main

} // end SVKPTBTokenizer