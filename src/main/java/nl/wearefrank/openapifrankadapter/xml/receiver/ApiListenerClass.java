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

package nl.wearefrank.openapifrankadapter.xml.receiver;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.*;
import java.util.stream.Stream;

public class ApiListenerClass {
    private final String apiListenerName;
    private Operation operation;
    private final String produces;
    private String method;
    private final String uriPattern;

    public ApiListenerClass(Map.Entry<String, PathItem> path) {
        this.apiListenerName = path.getKey()
                .substring(1)
                .replace("/", "-")
                .replace("{", "")
                .replace("}", "");

        this.uriPattern = path.getKey();

        for (Map.Entry<String, Operation> operation : getOperations(path.getValue()).entrySet()) {
            this.method = operation.getKey();
            this.operation = operation.getValue();
        }
        this.produces = extractContentType(this.operation);
    }

    public String getApiListenerName() {
        return "ApiListener-" + this.apiListenerName;
    }

    public String getProduces() {
        return this.produces;
    }

    public String getMethod() {
        return this.method;
    }

    public String getUriPattern() { return this.uriPattern; }

    ///////////////////////

    /**
     * Get the reference from the content
     *
     * @param content - The content that contains the reference
     * @return the reference if it exists, else an empty string
     */
    public static String extractReference(Content content) {
        return Optional.ofNullable(content)
                .map(LinkedHashMap::entrySet)
                .map(Collection::stream)
                .map(Stream::findAny)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Map.Entry::getKey)
                .orElse("");
    }

    /**
     * Extracts the content type and converts it to the Frank content types.
     * We need to look for any pay load and check what kind of content type is used
     *
     * @param operation - the operation of which the content type is extracted
     * @return a Frank content type (e.g. JSON, XML)
     */
    public static String extractContentType(Operation operation) {
        String content = "";

        // Try request body
        if (operation.getRequestBody() != null) {
            content = extractReference(operation.getRequestBody().getContent());
        }

        // Try the responses if there is no pay load on the request body
        if (content.isEmpty() && operation.getResponses() != null) {
            for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
                content = extractReference(entry.getValue().getContent());
                if (!content.isEmpty()) {
                    break;
                }
            }
        }

        return mapContentType(content);
    }

    /**
     * map the content of oap to that of Frank
     *
     * @param content - the oap naming convention for content type
     * @return the Frank naming convention for content type
     */
    public static String mapContentType(String content) {
        // Translate the OAP content type to that of a Frank
        String contentType = "UNKNOWN";
        switch (content) {
            case "application/json":
                contentType = "JSON";
                break;
            case "application/xml":
                contentType = "XML";
                break;
            case "*/*":
                // TODO: Bespreek met jeroen wat er moet gebeuren als er een any case is
                contentType = "ANY";
                break;
            default:
                System.out.println("Warning! No valid content type has been provided: [" + content + "]");
                break;
        }
        return contentType;
    }

    /**
     * Get the operations of this xml
     *
     * @param item - the path that represents an xml
     * @return a list of operations in the xml/path
     */
    public static Map<String, Operation> getOperations(PathItem item) {
        Map<String, Operation> operations = new HashMap<>();
        try {

            // If an operation exists, add it to the list of operations
            if (item.getGet() != null) {
                operations.put("GET", item.getGet());
            }
            if (item.getHead() != null) {
                operations.put("HEAD", item.getHead());
            }
            if (item.getDelete() != null) {
                operations.put("DELETE", item.getDelete());
            }
            if (item.getPost() != null) {
                operations.put("POST", item.getPost());
            }
            if (item.getPut() != null) {
                operations.put("PUT", item.getPut());
            }
            if (item.getOptions() != null) {
                operations.put("OPTIONS", item.getOptions());
            }
            if (item.getPatch() != null) {
                operations.put("PATCH", item.getPatch());
            }
            if (item.getTrace() != null) {
                operations.put("TRACE", item.getTrace());
            }

        } catch (NullPointerException e) {
            System.out.println("Warning! No operations found for this xml");
            e.printStackTrace();
            throw e;
        }

        return operations;
    }
}
