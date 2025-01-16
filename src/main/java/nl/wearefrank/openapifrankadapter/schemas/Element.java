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

package nl.wearefrank.openapifrankadapter.schemas;

import lombok.Getter;
import lombok.Setter;
import nl.nn.adapterframework.xml.SaxElementBuilder;
import nl.wearefrank.openapifrankadapter.schemas.Types.Typing;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.Objects;

public class Element {
    private String name;
    private String type;
    @Setter
    @Getter // TODO: GETTER Only used for testing
    private Integer minOccurs;
    @Setter
    @Getter // TODO: GETTER Only used for testing
    private Integer maxOccurs;
    private Typing object;

    // Constructors
    public Element(String name) {
        this.name = name;
    }

    // Generic construction to add type: complexType or simpleType
    public Element(Typing typing) {
        this.object = typing;
    }

    // Setters
    public void setType(String type) {
        if (Objects.equals(type, "numberType")) {
            this.type = "float";
        } else {
            this.type = type;
        }
    }

    public void setMinOccursCheck(List<String> required) {
        // Check if the property is required, if so the min occurs is set to 1
        if (required != null && required.contains(this.name)) {
            this.minOccurs = 1;
        } else {
            this.minOccurs = 0;
        }
    }

    public void AddElementToBuilder(SaxElementBuilder builder) throws SAXException {
        if (this.object != null) {
            this.object.AddToBuilder(builder);
        } else {
            try (SaxElementBuilder subElement = builder.startElement("xs:element")) {
                subElement.addAttribute("name", this.name);
                subElement.addAttribute("type", "xs:" + this.type);
                if (this.minOccurs != null) {
                    subElement.addAttribute("minOccurs", this.minOccurs.toString());
                }
                if (this.maxOccurs != null) {
                    subElement.addAttribute("maxOccurs", this.maxOccurs.toString());
                }
            }
        }
    }
}
