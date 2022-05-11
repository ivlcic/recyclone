package com.dropchop.recyclone.model.api.expr.parse;

import com.dropchop.recyclone.model.api.attr.Attribute;
import com.dropchop.recyclone.model.api.attr.AttributeBool;
import com.dropchop.recyclone.model.api.attr.AttributeDecimal;
import com.dropchop.recyclone.model.api.attr.AttributeSet;
import com.dropchop.recyclone.model.api.attr.AttributeString;
import com.dropchop.recyclone.model.api.expr.Operand;
import com.dropchop.recyclone.model.api.expr.ParserError;
import com.dropchop.recyclone.model.api.expr.Position;
import com.dropchop.recyclone.model.api.expr.ReservedSymbols;
import com.dropchop.recyclone.model.api.expr.operand.*;
import com.dropchop.recyclone.model.api.expr.ParseException;
import com.dropchop.recyclone.model.api.expr.operand.Bool;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;

import static com.dropchop.recyclone.model.api.utils.Iso8601.*;
import static com.dropchop.recyclone.model.api.expr.parse.ParserState.WINDOW_OFFSET;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 27. 11. 21.
 */
public class ParserHelper {

  static ParserError makeError(Position pos, ParserState state, ParserError.Code code) {
    ParserError error = new ParserError();
    if (state != null) {
      error.setColumnNum(state.pos + 1);
      error.setLineNum(state.lineNum + 1);
      error.setExpression(state.sequence.toString());
    }

    if (pos != null) {
      error.setColumnNum(pos.getLinePos() + 1);
      error.setLineNum(pos.getLineNum() + 1);
    }

    error.setCode(code);
    return error;
  }

  static ParserError makeError(ParserState state, ParserError.Code code) {
    return makeError(null, state, code);
  }

  static char[] getWindow(ParserState state,
                          @SuppressWarnings("SameParameterValue") int left,
                          @SuppressWarnings("SameParameterValue") int right) {
    int len = state.len;
    char[] window = new char[java.lang.Math.abs(right) + java.lang.Math.abs(left) + 1];
    for (int i = left, j = 0; i <= right; i++, j++) {
      int curr = state.pos + i;
      if (curr < 0 || curr >= len) {
        window[j] = ' ';
        continue;
      }
      window[j] = state.charAt(curr);
    }
    return window;
  }

  static boolean endsWith(CharSequence sequence, @SuppressWarnings("SameParameterValue") CharSequence ends) {
    int len = ends.length();
    int slen = sequence.length();
    if (slen < len) {
      return false;
    }
    for (int i = 0; i < len; i++) {
      if (sequence.charAt(slen - len + i) != ends.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  static boolean isSymbolInWindowAt(char[] window, int pos, CharSequence symbol) {
    for (int i = pos; i < pos + symbol.length(); i++) {
      if (i >= window.length) {
        return false;
      }
      char c = window[i];
      if (c != symbol.charAt(i - pos)) {
        return false;
      }
    }
    return true;
  }

  static boolean isNotEscapedSymbol(char[] window, CharSequence symbol) {
    boolean nesc = !isSymbolInWindowAt(window, 0, ReservedSymbols.ESCAPE_SYMBOL);
    boolean sym = isSymbolInWindowAt(window, ReservedSymbols.ESCAPE_SYMBOL.length(), symbol);
    return sym & nesc;
  }

  static boolean isNotEscapedSymbolAt(char[] window, int pos, CharSequence symbol) {
    boolean nesc = !isSymbolInWindowAt(window, pos - ReservedSymbols.ESCAPE_SYMBOL.length(), ReservedSymbols.ESCAPE_SYMBOL);
    boolean sym = isSymbolInWindowAt(window, pos, symbol);
    return sym & nesc;
  }

  static boolean isNotEscapedWhitespace(char[] window) {
    boolean nesc = !isSymbolInWindowAt(window, 0, ReservedSymbols.ESCAPE_SYMBOL);
    boolean sym = Character.isWhitespace(window[WINDOW_OFFSET]);
    return sym & nesc;
  }

  @SuppressWarnings("SameParameterValue")
  static void readUntil(ParserState state, CharSequence symbol, ParserError.Code errorCode) throws ParseException {
    state.appendAtPos();
    state.inc(1);
    do {
      char[] window = getWindow(state, -ReservedSymbols.ESCAPE_SYMBOL.length(), ReservedSymbols.MAX_SYMBOL_LEN);
      if (isNotEscapedSymbol(window, symbol)) {
        state.appendAtPos();
        state.inc(1);
        return;
      }
      if (state.atLastPos()) {
        throw new ParseException(makeError(state, errorCode));
      }
      state.appendAtPos();
    } while (state.inc(1) > Integer.MIN_VALUE);
  }

  static String trimUnwrap(String value) {
    if (value.startsWith(ReservedSymbols.VALUE_SYMBOL)) {
      value = value.substring(ReservedSymbols.VALUE_SYMBOL.length());
    }
    value = value.trim();
    if (value.endsWith(ReservedSymbols.VALUE_SYMBOL)) {
      value = value.substring(0, value.length() - ReservedSymbols.VALUE_SYMBOL.length());
    }
    return value;
  }

  static void parseAttributeValue(ParserState state, Deque<Set<Attribute<?>>> attributeStack, Set<Attribute<?>> attributes,
                                  String name, String value) throws ParseException {
    if (value.isEmpty()) {
      Set<Attribute<?>> nested = attributeStack.pollFirst();
      if (nested == null) {
        //noinspection SortedCollectionWithNonComparableKeys
        nested = new TreeSet<>();
      }
      AttributeSet attribute = new AttributeSet(name, nested);
      attributes.add(attribute);
      return;
    }
    if ("TRUE".equalsIgnoreCase(value)) {
      attributes.add(new AttributeBool(value, Boolean.TRUE));
      return;
    }
    if ("FALSE".equalsIgnoreCase(value)) {
      attributes.add(new AttributeBool(value, Boolean.FALSE));
    }
    if (value.matches("-*[0-9]+(\\.[0-9]+)*")) {
      try {
        attributes.add(new AttributeDecimal(name, new BigDecimal(value)));
        return;
      } catch (NumberFormatException ex0) {
        throw new ParseException(ex0.getMessage(),
          makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
      }
    }
    attributes.add(new AttributeString(name, value));
  }

  static Set<Attribute<?>> parseAttributeObject(ParserState state, Deque<Set<Attribute<?>>> attributeStack, String s)
    throws ParseException {
    if (s.startsWith(ReservedSymbols.ATTRIBUTE_DATA_SYMBOL_START)) {
      s = s.substring(ReservedSymbols.ATTRIBUTE_DATA_SYMBOL_START.length());
    }
    if (s.endsWith(ReservedSymbols.ATTRIBUTE_DATA_SYMBOL_END)) {
      s = s.substring(0, s.length() - ReservedSymbols.ATTRIBUTE_DATA_SYMBOL_END.length());
    }
    String[] metaNameValues = s.split(ReservedSymbols.ATTRIBUTE_DATA_DELIM);
    Set<Attribute<?>> attributes = new LinkedHashSet<>();
    for (String metaNameValueStr : metaNameValues) {
      if (metaNameValueStr == null) {
        throw new ParseException("Missing name value pair",
          makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
      }
      metaNameValueStr = metaNameValueStr.trim();
      if (metaNameValueStr.isEmpty()) {
        throw new ParseException("Empty name value pair.",
          makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
      }
      String[] metaNameValue = metaNameValueStr.split(ReservedSymbols.NAME_DELIM, 2);
      if (metaNameValue.length <= 0) {
        throw new ParseException(makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
      }
      if (metaNameValue.length > 2) {
        throw new ParseException("Too many delimiter >:< characters.",
          makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
      }
      metaNameValue[0] = metaNameValue[0].trim();
      if (metaNameValue[0].isEmpty()) {
        throw new ParseException("Empty name.", makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
      }
      if (metaNameValue[0].contains(" ")) {
        throw new ParseException("Missing name value delimiter >:< characters.",
          makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
      }
      String name = trimUnwrap(metaNameValue[0]);
      if (metaNameValue.length == 1) {
        if (name.matches("-*[0-9]+(\\.[0-9]+)*") || "TRUE".equalsIgnoreCase(name) || "FALSE".equalsIgnoreCase(name)) {
          parseAttributeValue(state, attributeStack, attributes, ReservedSymbols.DEFAULT_ATTR_NAME, name);
        } else {
          parseAttributeValue(state, attributeStack, attributes, name, "");
        }
      } else {
        if (metaNameValue[1] == null) {
          throw new ParseException("Missing value.",
            makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
        }
        String value = trimUnwrap(metaNameValue[1]);
        if (value.contains(":")) {
          throw new ParseException("Too many delimiter >:< characters.",
            makeError(state, ParserError.Code.INVALID_ATTRIBUTE_DATA_NAME_VALUE_PAIR));
        }
        parseAttributeValue(state, attributeStack, attributes, name, value);
      }
    }

    return attributes;
  }

  static List<Token> parseTokenList(ParserState state, String s) throws ParseException {
    if (s == null || s.isEmpty()) {
      throw new ParseException(makeError(state, ParserError.Code.EMPTY_OPERATOR));
    }
    StringBuilder builder = new StringBuilder();
    List<Token> tokenList = new ArrayList<>();
    boolean mixedCase = false;
    boolean upperCase = false;
    StringBuilder buffer = new StringBuilder();
    buffer.append(" ".repeat(ReservedSymbols.ESCAPE_SYMBOL.length()));
    buffer.append(s);
    buffer.append(" ".repeat(ReservedSymbols.ESCAPE_SYMBOL.length()));
    for (int i = ReservedSymbols.ESCAPE_SYMBOL.length(); i < buffer.length(); i++) {
      char c = buffer.charAt(i);
      if (Character.isUpperCase(c)) {
        upperCase = true;
      }
      if (Character.isLowerCase(c) && upperCase) {
        mixedCase = true;
        upperCase = false;
      }
      String prev = buffer.substring(i - ReservedSymbols.ESCAPE_SYMBOL.length(), i);
      String curr = buffer.substring(i, i + ReservedSymbols.ESCAPE_SYMBOL.length());
      if (Character.isWhitespace(c) && !prev.equals(ReservedSymbols.ESCAPE_SYMBOL)) {
        if (builder.length() > 0) {
          Token token = new Token();
          token.setMixedCase(mixedCase);
          token.setUpperCase(upperCase);
          token.setParsedValue(builder.toString());
          token.setValue(s);
          tokenList.add(token);
          builder = new StringBuilder();
        }
      } else {
        if (!curr.equals(ReservedSymbols.ESCAPE_SYMBOL)) {
          builder.append(c);
        }
        if (curr.equals(ReservedSymbols.ESCAPE_SYMBOL) && prev.equals(ReservedSymbols.ESCAPE_SYMBOL)) {
          builder.append(c);
        }
      }
    }
    if (builder.length() > 0) {
      Token token = new Token();
      token.setMixedCase(mixedCase);
      token.setUpperCase(upperCase);
      token.setParsedValue(builder.toString());
      token.setValue(s);
      tokenList.add(token);
    }
    return tokenList;
  }

  static Token parseTokenOperand(ParserState state, String value) throws ParseException {
    List<Token> tokens = parseTokenList(state, value);
    if (tokens.isEmpty()) {
      throw new ParseException(makeError(state, ParserError.Code.EMPTY_OPERATOR));
    }
    Token token = tokens.get(0);
    token.setValue(value);
    return token;
  }

  static Phrase parsePhraseOperand(ParserState state, String value) throws ParseException {
    Phrase phrase = new Phrase();
    List<Token> tokenList = parseTokenList(state, value);
    phrase.setParsedValue(tokenList);
    return phrase;
  }

  static Operand<?> parseOperand(ParserState state, String value) throws ParseException {
    if (value == null || value.isEmpty()) {
      throw new ParseException(makeError(state, ParserError.Code.EMPTY_OPERATOR));
    }
    if (value.startsWith(ReservedSymbols.TextSearch.PHRASE_SYMBOL)) {
      value = value.substring(ReservedSymbols.TextSearch.PHRASE_SYMBOL.length());
    }
    value = value.trim();
    if (value.endsWith(ReservedSymbols.TextSearch.PHRASE_SYMBOL)) {
      value = value.substring(0, value.length() - ReservedSymbols.TextSearch.PHRASE_SYMBOL.length());
      return parsePhraseOperand(state, value.trim());
    }

    if (value.startsWith(ReservedSymbols.VALUE_SYMBOL)) {
      value = value.substring(ReservedSymbols.VALUE_SYMBOL.length());
    }
    value = value.trim();
    if (value.endsWith(ReservedSymbols.VALUE_SYMBOL)) {
      value = value.substring(0, value.length() - ReservedSymbols.VALUE_SYMBOL.length());
    }

    if ("TRUE".equals(value)) {
      Bool bool = new Bool();
      bool.setParsedValue(Boolean.TRUE);
      return bool;
    }
    if ("FALSE".equals(value)) {
      Bool bool = new Bool();
      bool.setParsedValue(Boolean.FALSE);
      return bool;
    }
    if (value.matches("-*[0-9]+(\\.[0-9]+)*")) {
      try {
        Double d = Double.parseDouble(value);
        Numeric num = new Numeric();
        num.setParsedValue(d);
        return num;
      } catch (NumberFormatException ex0) {
        //ignored
      }
    }
    Matcher matcher = dateTimePattern.matcher(value);
    if (matcher.matches()) {
      String completeStr = matcher.group(0);
      String timeStr = matcher.group(1);
      String secsStr = matcher.group(2);
      String msecStr = matcher.group(3);
      String zoneStr = matcher.group(4);
      try {
        if (zoneStr != null) {
          return new DateTime(value, ZonedDateTime.parse(completeStr));
        } else if (msecStr != null) {
          return new DateTime(value, LocalDateTime.parse(completeStr).atZone(ZoneId.systemDefault()));
        } else if (secsStr != null) {
          return new DateTime(value, LocalDateTime.parse(completeStr).atZone(ZoneId.systemDefault()));
        } else if (timeStr != null) {
          return new DateTime(value, LocalDateTime.parse(completeStr).atZone(ZoneId.systemDefault()));
        } else {
          return new DateTime(value, LocalDate.parse(completeStr).atTime(0,0).atZone(ZoneId.systemDefault()));
        }
      } catch (Exception e) {
        makeError(state, ParserError.Code.PARSE_VALUE_ERROR);
      }
    }
    return parseTokenOperand(state, value);
  }
}
