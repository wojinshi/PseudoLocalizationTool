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
import com.google.i18n.pseudolocalization.message.impl.AbstractPlaceholder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.json.JSONObject;
import org.dom4j.Branch;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author julia.lin
 */
public class WebResourceJS implements MessageCatalog {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private List<Message> messages = new ArrayList<Message>();
    private String contentBeforeJson = null;
    private String contentAfterJson = null;

    static {
        FormatRegistry.register(WebResourceJS.class, "js");
    }

    static class JSONMessage extends SimpleMessage {

        static final Pattern MESSAGE_FORMAT_ARG = Pattern.compile(
                "\\{((\\d+)|#|(\\w+))(,\\w+(,[^\\}]+)?)?\\}");

        private static List<MessageFragment> parseMessage(String text) {
            List<MessageFragment> list = new ArrayList<MessageFragment>();
            // TODO: handle quoting
            Matcher m = MESSAGE_FORMAT_ARG.matcher(text);
            int start = 0;
            while (m.find()) {
                String plainText = text.substring(start, m.start());
                start = m.end();
                if (plainText.length() > 0) {
                    list.add(new SimpleTextFragment(plainText));
                }
                list.add(new MessageFormatPlaceholder(m.group()));
            }
            String plainText = text.substring(start);
            if (plainText.length() > 0) {
                list.add(new SimpleTextFragment(plainText));
            }
            return list;
        }
        private final String key;

        public JSONMessage(String key, String text) {
            super(parseMessage(text));
            this.key = key;
        }

        public JSONMessage(MessageFragment fragment) {
            super(Arrays.asList(fragment));
            key = null;
        }

        @Override
        public String getId() {
            return key;
        }
    }

    private static class MessageFormatPlaceholder extends AbstractPlaceholder {

        private final String text;

        public MessageFormatPlaceholder(String text) {
            this.text = text;
        }

        @Override
        public String getTextRepresentation() {
            return text;
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

        String content = buf.toString();
        int jsonStart = content.indexOf("{");
        int jsonEnd = content.lastIndexOf("}");

        final List<MessageFragment> result = new ArrayList<MessageFragment>();
        contentBeforeJson = content.substring(0, jsonStart);
        // process json content
        String json = content.substring(jsonStart, jsonEnd + 1);
        
        json = json.replaceAll("(['\"])\\s*\\+\\s*\\1", "");
        
        JSONObject jsonObject = JSONObject.fromObject(json);
        String key = null;
        Iterator it = jsonObject.keys();
        while (it.hasNext()) {
            key = (String) it.next();
            messages.add(new JSONMessage(key, jsonObject.getString(key)));
        }
        contentAfterJson = content.substring(jsonEnd + 1);

        return new ReadableMessageCatalog() {

            @Override
            public Iterable<Message> readMessages() {
                return messages;
            }

            @Override
            public void close() throws IOException {
            }
        };
    }

    private class JSONWriter implements WritableMessageCatalog {

        private final Map map = new HashMap();
        private final OutputStream stream;

        JSONWriter(OutputStream stream) {
            this.stream = stream;
        }

        @Override
        public void close() throws IOException {
            String content = contentBeforeJson + JSONObject.fromObject(map).toString() + contentAfterJson;
            stream.write(content.getBytes(UTF8));
            stream.close();
        }

        @Override
        public void writeMessage(Message msg) {
            map.put(msg.getId(), ((JSONMessage) msg).getText());
        }
    }

    @Override
    public WritableMessageCatalog writeTo(final OutputStream out) {
        return new JSONWriter(out);
    }
}
