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

package nl.wearefrank.openapifrankadapter.xml;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;

import java.util.Map;

public class AdapterClass {
    private final String adapterName;
    private final String adapterDescription;

    public AdapterClass(OpenAPI openAPI, Map.Entry<String, PathItem> path, Map.Entry<PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> operation) {
        // adapterName: remove the first slash and replace the remaining slashes with -
        this.adapterName = path.getKey()
                .substring(1)
                .replace("/", "-")
                .replace("{", "")
                .replace("}", "")
                + "-" + operation.getKey();
        this.adapterDescription = openAPI.getInfo().getDescription();
    }

    public String getAdapterName() {
        return "Adapter-" + this.adapterName;
    }

    public String getAdapterDescription() {
        return this.adapterDescription;
    }
}
