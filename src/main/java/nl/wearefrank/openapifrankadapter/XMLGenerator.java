/*
   Copyright 2023 WeAreFrank!
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package nl.wearefrank.openapifrankadapter;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import nl.wearefrank.openapifrankadapter.xml.AdapterClass;
import nl.wearefrank.openapifrankadapter.xml.AdapterExits;
import nl.wearefrank.openapifrankadapter.xml.AdapterJsonfiyer;
import nl.wearefrank.openapifrankadapter.xml.AdapterRefs;
import nl.wearefrank.openapifrankadapter.error.ErrorApiResponse;
import nl.wearefrank.openapifrankadapter.xml.receiver.ReceiverJSONObject;
import nl.wearefrank.openapifrankadapter.xml.sender.SenderJSONObject;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;

public class XMLGenerator {
    static public LinkedList<GenFiles> execute(OpenAPI openAPI, Option templateOption) throws SAXException, ErrorApiResponse, IOException {
        Paths paths = openAPI.getPaths();

        LinkedList<GenFiles> genFiles = new LinkedList<>();

        // For loop going through all the paths and instantiating a new AdapterClass
        for (Map.Entry<String, PathItem> path : paths.entrySet()) {
            // For loop going one level deeper in the path
            for (Map.Entry<PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> operation : path.getValue().readOperationsMap().entrySet()) {

                // Create a new AdapterClass
                AdapterClass adapter = new AdapterClass(openAPI, path, operation);

                //// Generate XSD ////
                // Generate XSD for the xml, add it to GenFiles (name + xsd, content as byte[])
                AdapterRefs adapterRefs = new AdapterRefs(openAPI, operation);
                // Check if there is a need to generate an XSD
                if (adapterRefs.root != null) {
                    genFiles.add(new GenFiles(adapter.getAdapterName() + ".xsd", adapterRefs.xsd.toString().getBytes()));
                }

                //// Generate Exits ////
                AdapterExits adapterExits = new AdapterExits();
                adapterExits.GetAdapterExits(operation);

                if (templateOption == Option.RECEIVER || templateOption == Option.SENDER) {
                    //// Template ////
                    // Get the template file as an input stream
                    InputStream inputStream = XMLGenerator.class.getResourceAsStream(templateOption.getTemplateName());

                    // Read the input stream into a String
                    String templateString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    // Close the input stream
                    inputStream.close();

                    // Create a new Handlebars object
                    Handlebars handlebars = new Handlebars();
                    handlebars.registerHelper("when", new WhenHelper());
                    Template template = handlebars.compileInline(templateString);

                    // Create JSON and apply the template
                    AdapterJsonfiyer adapterJsonfiyer = new AdapterJsonfiyer(adapter, adapterRefs, adapterExits, path);
                    String adapterTemplate = template.apply(adapterJsonfiyer.getAdapterJsonObj(templateOption));

                    // Pretty print the XML
                    String prettyTemplate = prettyPrintByDom4j(adapterTemplate, 8, false);

                    // Export the template to xml file
                    genFiles.add(new GenFiles(adapter.getAdapterName() + ".xml", prettyTemplate.getBytes()));
                }
            }
        }

        // Return all the Files generated
        return genFiles;
    }

    //// Method to pretty-fy XML ////
    public static String prettyPrintByDom4j(String xmlString, int indent, boolean skipDeclaration) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setIndentSize(indent);
            format.setSuppressDeclaration(skipDeclaration);
            format.setEncoding("UTF-8");

            org.dom4j.Document document = DocumentHelper.parseText(xmlString);
            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }
}
