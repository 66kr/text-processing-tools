
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