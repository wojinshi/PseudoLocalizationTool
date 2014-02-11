/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ringcentral.qa.xmn.i18n.pseudolocalization.format;

import com.google.i18n.pseudolocalization.format.FormatRegistry;
import com.google.i18n.pseudolocalization.format.MessageCatalog;
import com.google.i18n.pseudolocalization.format.ReadableMessageCatalog;
import com.google.i18n.pseudolocalization.format.WritableMessageCatalog;
import com.google.i18n.pseudolocalization.message.DefaultVisitor;
import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.MessageFragment;
import com.google.i18n.pseudolocalization.message.NonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.Placeholder;
import com.google.i18n.pseudolocalization.message.SimpleMessage;
import com.google.i18n.pseudolocalization.message.SimpleNonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.SimpleTextFragment;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Administrator
 */
public class AndroidResourceXml implements MessageCatalog {

  private static final Charset UTF8 = Charset.forName("UTF-8");
  private static final Pattern ENTITY_PATTERN = Pattern.compile(
          "&((#\\d+)|(#[xX][a-fA-F0-9]+)|(\\w+));");
  private List<Message> messages = new ArrayList<Message>();
  private static final Pattern REFERENCE_STRING_PATTERN = Pattern.compile(
          "^@string/.*");
  private static final Pattern TAG_PATTERN = Pattern.compile(
          "(<(\\S+)[^>]*>)[\\s\\S]*(</\\2>)");
  private static final String XLIFF_TAG = "xliff:g" ;

  static {
    FormatRegistry.register(AndroidResourceXml.class, "xml");
  }

  static class AndroidResourceMessage extends SimpleMessage {

    public AndroidResourceMessage(MessageFragment fragment) {
      super(Arrays.asList(fragment));
    }
  }

  @Override
  public ReadableMessageCatalog readFrom(InputStream stream) throws IOException {
    StringBuilder buf = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, UTF8));
    int ch;
    while ((ch = reader.read()) != -1) {
      buf.append((char) ch);
    }
    reader.close();
    final List<MessageFragment> result = new ArrayList<MessageFragment>();
    try {
      String xml = buf.toString();
      if (xml.startsWith("<?xml")) {
        int end = xml.indexOf("?>");
        String encoding = xml.substring(0, end + 2) + "\n";
        result.add(new SimpleNonlocalizableTextFragment(encoding));
      }
      Document document = DocumentHelper.parseText(xml);
      visitNodes(result, document);
    } catch (DocumentException ex) {
      ex.printStackTrace();
    }
    List<Message> list = new ArrayList<Message>();
    for (MessageFragment fragment : result) {
      list.add(new AndroidResourceMessage(fragment));
    }
    messages = Collections.unmodifiableList(list);
    return new ReadableMessageCatalog() {
      public Iterable<Message> readMessages() {
        return messages;
      }

      @Override
      public void close() throws IOException {
      }
    };
  }

  @Override
  public WritableMessageCatalog writeTo(final OutputStream out) {
    return new WritableMessageCatalog() {
      @Override
      public void writeMessage(Message msg) throws IOException {
        // TODO(jat): extract this to a common place
        final StringBuilder buf = new StringBuilder();
        msg.accept(new DefaultVisitor() {
          @Override
          public void visitNonlocalizableTextFragment(VisitorContext ctx,
                  NonlocalizableTextFragment fragment) {
            buf.append(fragment.getText());
          }

          @Override
          public void visitPlaceholder(VisitorContext ctx, Placeholder placeholder) {
            buf.append(placeholder.getTextRepresentation());
          }

          @Override
          public void visitTextFragment(VisitorContext ctx, TextFragment fragment) {
            buf.append(fragment.getText());
          }
        });
        out.write(buf.toString().getBytes(UTF8));
      }

      public void close() throws IOException {
        out.close();
      }
    };
  }

  public void visitTextFragment(final VisitorContext ctx, TextFragment text) {
    final List<MessageFragment> result = new ArrayList<MessageFragment>();
    try {
      String xml = text.getText();
      if (xml.startsWith("<?xml")) {
        int end = xml.indexOf("?>");
        String encoding = xml.substring(0, end + 2) + "\n";
        result.add(ctx.createNonlocalizableTextFragment(encoding));
      }
      Document document = DocumentHelper.parseText(text.getText());
      visitNodes(result, document);
      ctx.replaceFragment(text, result);
    } catch (DocumentException ex) {
      ex.printStackTrace();
    }
  }

  private void visitNodes(List<MessageFragment> result, Branch root) {
    Iterator<Node> iterator = root.nodeIterator();
    while (iterator.hasNext()) {
      Node node = iterator.next();
      String xml = node.asXML();
      if (node.getNodeType() == Node.COMMENT_NODE) {
        result.add(new SimpleNonlocalizableTextFragment(xml));
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        if (xml.trim().length() != 0) {
          result.add(new SimpleTextFragment(xml));
        } else {
          result.add(new SimpleNonlocalizableTextFragment(xml));
        }
      } else if (node.getNodeType() == Node.ELEMENT_NODE) {
        String startTag = xml.trim().replaceAll(TAG_PATTERN.pattern(), "$1");
        String endTag = xml.trim().replaceAll(TAG_PATTERN.pattern(), "$3");
        if(startTag.contains(XLIFF_TAG)) {
            result.add(new SimpleNonlocalizableTextFragment(xml));
        } else {
            result.add(new SimpleNonlocalizableTextFragment(startTag));
            if (((Element) node).isTextOnly()) {
                if (!node.getText().matches(REFERENCE_STRING_PATTERN.pattern())) {
                    result.add(new SimpleTextFragment(node.getText()));
                } else {
                    visitText(result, xml);
                }
            } else {
                visitNodes(result, (Element) node);
            }
            result.add(new SimpleNonlocalizableTextFragment(endTag));
        }
      } else if (node.getNodeType() == Node.NAMESPACE_NODE) {
        // do nothing
      } else {
        result.add(new SimpleNonlocalizableTextFragment(xml));
      }
    }
  }

  private void visitText(List<MessageFragment> result, String xml) {
    if (xml.trim().length() != 0) {
      Matcher m = ENTITY_PATTERN.matcher(xml);
      int start = 0;
      while (m.find()) {
        String plainText = xml.substring(start, m.start());
        start = m.end();
        String entity = m.group();
        if (plainText.length() > 0) {
          result.add(new SimpleTextFragment(plainText));
        }
        result.add(new SimpleNonlocalizableTextFragment(entity));
      }
      String plainText = xml.substring(start);
      if (plainText.length() > 0) {
        result.add(new SimpleTextFragment(plainText));
      }
    } else {
      result.add(new SimpleNonlocalizableTextFragment(xml));
    }
  }
}
