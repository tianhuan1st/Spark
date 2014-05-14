
package cn.bmwm.navigate;

import com.meterware.httpunit.*;
import com.sun.org.apache.xpath.internal.XPathAPI;
import org.jivesoftware.spark.util.log.Log;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * A utility class that uses google's translation service to translate text to various languages.
 */
public class DirectionUtil {
    private static final String output = "xml";
    private static final String ak = "k6LRNg83n9cTeZIgqAClxL4Z";

    private DirectionUtil() {

    }



    public static String navigate(String origin, String destination, String origin_region, String destination_region) {
    	
        String response = "";
        String urlString = "http://api.map.baidu.com/direction/v1?mode=driving&origin=" + origin +
                                        "&destination=" + destination +
                                        " &origin_region=" + origin_region +
                                        "&destination_region=" + destination_region +
                                        "&output=" + output +
                                         "&ak=" + ak;
        // disable scripting to avoid requiring js.jar
        HttpUnitOptions.setScriptingEnabled(false);

        // create the conversation object which will maintain state for us
        WebConversation wc = new WebConversation();

        // Obtain the baidu navigate result
        WebRequest webRequest = new GetMethodWebRequest(urlString);
        // required to prevent a 403 forbidden error from baidu
        webRequest.setHeaderField("User-agent", "Mozilla/4.0");
        try {
            WebResponse webResponse = wc.getResponse(webRequest);
            //NodeList list = webResponse.getDOM().getDocumentElement().getElementsByTagName("div");
            try {
				NodeList list2 = XPathAPI.selectNodeList(webResponse.getDOM(), "//steps/content/instructions/text()");
			
				for (int i = 0; i < list2.getLength(); ++i)
				{
					response = response + list2.item(i).getNodeValue()+" \n";
				}

            } catch (TransformerException e) {
				Log.warning("Translator error",e);
			}
            
//            int length = list.getLength();
//            for (int i = 0; i < length; i++) {
//                Element element = (Element)list.item(i);
//                if ("result_box".equals(element.getAttribute("id"))) {
//                    Node translation = element.getFirstChild();
//                    if (translation != null) {
//                        response = translation.getNodeValue();
//                    }
//                }
//            }
        }
        catch (MalformedURLException e) {
            Log.error("Could not for url: " + e);
        }
        catch (IOException e) {
            Log.error("Could not get response: " + e);
        }
        catch (SAXException e) {
            Log.error("Could not parse response content: " + e);
        }

        return response;
    }

    /**
     * A typesafe enum class for translation types.
     */
    public static final class TranslationType {
        private final String id;
        private final String name;

        private TranslationType(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getID() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static TranslationType[] getTypes() {
            return types;
        }

        public String toString() {
            return name;
        }

        /**
         * Type representing no translation.
         */
        public static final TranslationType None = new TranslationType("", "No Translation");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType EnglishToGerman = new TranslationType("en%7Cde", "English to German");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType EnglishToSpanish = new TranslationType("en%7Ces", "English to Spanish");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType EnglishToFrench = new TranslationType("en%7Cfr", "English to French");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType EnglishToItalian = new TranslationType("en%7Cit", "English to Italian");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType EnglishToPortuguese = new TranslationType("en%7Cpt", "English to Portuguese");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType EnglishToJapanese = new TranslationType("en%7Cja", "English to Japanese");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType EnglishToKorean = new TranslationType("en%7Cko", "English to Korean");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType EnglishToChineseSimplified = new TranslationType("en%7Czh-CN", "English to Chinese (Simplified)");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType GermanToEnglish = new TranslationType("de%7Cen", "German to English");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType GermanToFrench = new TranslationType("de%7Cfr", "German to French");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType SpanishToEnglish = new TranslationType("es%7Cen", "Spanish to English");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType FrenchToEnglish = new TranslationType("fr%7Cen", "French to English");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType FrenchToGerman = new TranslationType("fr%7Cde", "French to German");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType ItalianToEnglish = new TranslationType("it%7Cen", "Italian to English");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType PortugueseToEnglish = new TranslationType("pt%7Cen", "Portuguese to English");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType JapaneseToEnglish = new TranslationType("ja%7Cen", "Japanese to English");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType KoreanToEnglish = new TranslationType("ko%7Cen", "Korean to English");
        /**
         * Type representing a translation from english to german.
         */
        public static final TranslationType ChineseSimplifiedToEnglish = new TranslationType("zh-CN%7Cen", "Chinese (Simplified) to English");

        /**
         * Array containing all TranslationTypes
         */
        private static final TranslationType[] types = {
                None,
                EnglishToGerman,
                EnglishToSpanish,
                EnglishToFrench,
                EnglishToItalian,
                EnglishToPortuguese,
                EnglishToJapanese,
                EnglishToKorean,
                EnglishToChineseSimplified,
                GermanToEnglish,
                GermanToFrench,
                SpanishToEnglish,
                FrenchToEnglish,
                FrenchToGerman,
                ItalianToEnglish,
                PortugueseToEnglish,
                JapaneseToEnglish,
                KoreanToEnglish,
                ChineseSimplifiedToEnglish
        };
    }
}

